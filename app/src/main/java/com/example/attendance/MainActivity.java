package com.example.attendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String deviceMac;
    private String name;
    private String classId;
    private Context mContext;

    private Scanner scanner;
    private Advertiser advertiser;

    private Button registerButton;
    private Button startButton;
    private EditText nameEditText;

    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        deviceMac = getMacAddress("wlan0");
        Log.d("test", deviceMac);
        classId = "Mobile Computing";

        registerButton = findViewById(R.id.bt_register);
        startButton = findViewById(R.id.bt_start);
        nameEditText = findViewById(R.id.et_name);

        scanner = new Scanner(mContext, classId);
        advertiser = new Advertiser(mContext, deviceMac);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                register(deviceMac, name, classId);
                polling(deviceMac, name);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPlan(deviceMac);
                stopPolling();
            }
        });

        permissionCheck();
    }

    public void register(String deviceMac, String name, String classId){
        Call<ResponseBody> responseBodyCall = Client.getClient().register(deviceMac, name, classId);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Log.d("test", "registered");
                    Toast.makeText(mContext, "Registration succeed", Toast.LENGTH_SHORT).show();
                } else{
                    Log.d("test", "registration failed");
                    Toast.makeText(mContext, "Registered failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void getPlan(String deviceMac){
        Call<ResponseBody> responseBodyCall = Client.getClient().getPlan(deviceMac);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                JSONObject jsonObject;

                try{
                    String body = response.body().string();
                    jsonObject = new JSONObject(body);

                    if(jsonObject.length() != 0){
                        int duration = jsonObject.getInt("duration");
                        int order = jsonObject.getInt("deviceOrder");

                        JSONArray plan = jsonObject.getJSONArray("plan");
                        executePlan(plan, duration, order);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void executePlan(JSONArray plan, int duration, int order){
        PlanTask planTask = new PlanTask(plan, duration, order);
        planTask.start();
    }

    class PlanTask extends Thread{
        JSONArray plan;
        int duration;
        int order;

        public PlanTask(JSONArray plan, int duration, int order){
            this.plan = plan;
            this.duration = duration;
            this.order = order;
        }

        @Override
        public void run(){
            int sleepTime = duration / 10;

            for(int i = 0; i < plan.length(); i++){
                try {
                    sleep(sleepTime * 1000);

                    JSONObject round = plan.getJSONObject(i);
                    String role = round.getString("role");

                    RoundTask roundTask = new RoundTask(role, duration);
                    roundTask.start();

                    roundTask.join();

                    Log.d("test", "round finished");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            uploadFiles();
        }
    }

    class RoundTask extends Thread{
        private int duration;
        private String role;

        public RoundTask(String role, int duration){
            this.role = role;
            this.duration = duration;
        }

        @Override
        public void run(){
            try {
                if(role.equalsIgnoreCase("scan")){
                    Log.d("test", role);
                    scanner.startScan(duration);

                } else{
                    Log.d("test", role);
                    advertiser.startAdvertise(duration);
                }
                this.sleep(duration * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadFiles(){
        String[] list = getFileList("attendance");
        List<MultipartBody.Part> parts = new ArrayList<>();
        for(int i = 0; i < list.length; i++){
            String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/attendance/" + list[i];
            Log.d("test", filename);
            File file = new File(filename);

            RequestBody requestBody = RequestBody.create(null, file);
            parts.add(MultipartBody.Part.createFormData("file", list[i], requestBody));
        }

        Call<ResponseBody> responseBodyCall = Client.getClient().sendData(deviceMac, parts);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Log.d("test", "registered");
                    Toast.makeText(mContext, "Upload succeed", Toast.LENGTH_SHORT).show();
                } else{
                    Log.d("test", "Upload failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("test", "Upload failed");
            }
        });
    }

    public void polling(final String deviceMac, final String name){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Call<ResponseBody> responseBodyCall = Client.getClient().polling(deviceMac, name);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response.body().string());
                                if (jsonObject.length() != 0){
                                    String type = jsonObject.getString("type");

                                    if(type.equalsIgnoreCase("plan")){
                                        stopPolling();
                                        int duration = jsonObject.getInt("duration");
                                        int order = jsonObject.getInt("deviceOrder");
                                        JSONArray plan = jsonObject.getJSONArray("plan");
                                        executePlan(plan, duration, order);
                                    } else if(type.equalsIgnoreCase("authentication")){
                                        stopPolling();
                                        Intent intent = new Intent(mContext, FingerprintActivity.class);
                                        intent.putExtra("deviceMac", deviceMac);
                                        intent.putExtra("name", name);
                                        startActivity(intent);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(response.code() == 202){
                            Log.d("test", "nothing to notice");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    public void stopPolling(){
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void permissionCheck(){
        int permissionResult = mContext.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionResult2 = mContext.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> permissionList = new ArrayList<>();

        if(permissionResult == PackageManager.PERMISSION_DENIED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if(permissionResult2 == PackageManager.PERMISSION_DENIED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (permissionList.isEmpty()){
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/attendance");
            if(!dir.exists()){
                dir.mkdirs();
                Log.d("test", "mkdir succeed");
            }
        } else{
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case 1:
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0){
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/attendance");
                        if (!dir.exists()) {
                            dir.mkdirs();
                            Log.d("test", "mkdir succeed");
                        }
                    } else{
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    public static String getMacAddress(String interfaceName){
        try{
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName))
                        continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null)
                    return "";

                StringBuilder buf = new StringBuilder();

                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));

                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) {

        }

        return "";
    }

    public String[] getFileList(String path){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path);

        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String date = dateFormat.format(new Date(System.currentTimeMillis()));
                if (name.contains(classId + date))
                    return true;
                return false;
            }
        };

        File[] files = file.listFiles(filenameFilter);
        String[] list = new String[files.length];
        for(int i = 0; i < files.length; i++){
            list[i] = files[i].getName();
        }

        return list;
    }
}


