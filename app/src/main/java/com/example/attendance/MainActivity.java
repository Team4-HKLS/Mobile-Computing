package com.example.attendance;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String deviceMac;
    private String classId;
    private Context context;

    private Scanner scanner;
    private Advertiser advertiser;

    private Button registerButton;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        deviceMac = getMacAddress("wlan0");
        Log.d("test", deviceMac);
        classId = "Mobile Computing";

        registerButton = findViewById(R.id.bt_register);
        startButton = findViewById(R.id.bt_start);

        scanner = new Scanner(context);
        advertiser = new Advertiser(context, deviceMac);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(deviceMac, classId);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlan(deviceMac);
            }
        });
    }
    public void register(String deviceMac, String classId){
        Call<ResponseBody> responseBodyCall = Client.getClient().register(deviceMac, classId);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Log.d("test", "registered");
                } else{
                    Log.d("test", "registration failed");
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
                if(role.equalsIgnoreCase("transmit")){
                    Log.d("test", role);
                } else{
                    Log.d("test", role);
                }

                this.sleep(duration * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


