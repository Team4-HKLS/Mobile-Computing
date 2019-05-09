package com.example.attendance;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class Scanner {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private Context mContext;

    public Scanner(Context mContext){
        this.mContext = mContext;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public void startScan(){
        if(bluetoothLeScanner == null || !bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivity(intent);
            //bluetoothAdapter.enable();
        } else{
            bluetoothLeScanner.startScan(scanCallback);
        }
    }

    public void stopScan(){
        bluetoothLeScanner.stopScan(stopCallback);
    }

    public ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            //super.onScanResult(callbackType, result);
            Log.d("test", result.toString());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("test", "failed");
        }
    };

    public ScanCallback stopCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
}
