package com.example.attendance;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.UUID;

public class Advertiser {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private Context mContext;
    private String deviceMac;
    private String shortMac;
    private String testUUID = "00000000-0000-1000-8000-00805F9B34FB";

    public Advertiser(Context mContext, String deviceMac) {
        this.mContext = mContext;
        this.deviceMac = deviceMac;
        shortMac = deviceMac.split(":")[0] + deviceMac.split(":")[1];
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    }

    public void startAdvertise(int duration){
        if(bluetoothLeAdvertiser == null || !bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(intent);
            //bluetoothAdapter.enable();
        } else{
            AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                    .setConnectable(false)
                    .build();

            AdvertiseData advertiseData = new AdvertiseData.Builder()
                    .setIncludeTxPowerLevel(true)
                    .addServiceUuid(new ParcelUuid(UUID.fromString(testUUID)))
                    .addServiceData(new ParcelUuid(UUID.fromString(testUUID)), deviceMac.getBytes(Charset.forName("UTF-8")))
                    .build();

            bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
            Log.d("test", "advertising");

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopAdvertise();
                }
            }, duration*1000);
        }
    }

    public void stopAdvertise(){
        bluetoothLeAdvertiser.stopAdvertising(stopCallback);
    }

    public AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d("test", "success advertising");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.d("test", "fail advertising");
            Log.d("test", Integer.toString(errorCode));
        }
    };

    public AdvertiseCallback stopCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
        }
    };
}
