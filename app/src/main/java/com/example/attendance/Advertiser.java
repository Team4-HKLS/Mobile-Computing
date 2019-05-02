package com.example.attendance;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

public class Advertiser {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private Context mContext;
    private String deviceMac;

    public Advertiser(Context mContext, String deviceMac) {
        this.mContext = mContext;
        this.deviceMac = deviceMac;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    }

    public void startAdvertise(){
        if(bluetoothLeAdvertiser == null || !bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(intent);
            //bluetoothAdapter.enable();
        } else{
            AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .build();

            AdvertiseData advertiseData = new AdvertiseData.Builder()
                    .addServiceUuid(ParcelUuid.fromString(deviceMac))
                    .build();

            bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
            Log.d("test", "advertising");
        }
    }

    public void stopAdvertise(){
        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
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
}
