package com.example.attendance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FingerprintActivity extends AppCompatActivity {

    private static final String KEY_NAME = "example_key";
    private FingerprintManager fingerprintManager;
    private KeyStore keyStore;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintHandler helper;

    private String deviceMac;
    private String name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        deviceMac = getIntent().getStringExtra("deviceMac");
        name = getIntent().getStringExtra("name");

        if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(this,"Lock screen security not enabled in Settings", Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)
                != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Fingerprint authentication permission not enabled", Toast.LENGTH_LONG).show();
                return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {

            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        generateKey();

        if(cipherInit()) {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            helper = new FingerprintHandler(this);
            helper.startAuth(fingerprintManager, cryptoObject);
        }
    }

    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");

        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public class FingerprintHandler extends
            FingerprintManager.AuthenticationCallback {

        private CancellationSignal cancellationSignal;
        private Context appContext;

        FingerprintHandler(Context context) {
            appContext = context;
        }

        void startAuth(FingerprintManager manager,
                       FingerprintManager.CryptoObject cryptoObject) {

            cancellationSignal = new CancellationSignal();

            if (ActivityCompat.checkSelfPermission(appContext,
                    Manifest.permission.USE_FINGERPRINT) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }
        @Override
        public void onAuthenticationError(int errMsgId,
                                          CharSequence errString) {
            Toast.makeText(appContext,
                    "Authentication error\n" + errString,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId,
                                         CharSequence helpString) {
            Toast.makeText(appContext,
                    "Authentication help\n" + helpString,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationFailed() {
            showAuthFailureAlert();
        }

        @Override
        public void onAuthenticationSucceeded(
                FingerprintManager.AuthenticationResult result) {
//
//            Toast.makeText(appContext,
//                    "Authentication succeeded.",
//                    Toast.LENGTH_SHORT).show();

            confirmAttendance(deviceMac, name, true);

        }
    }

    public void confirmAttendance(String deviceMac, String name, boolean isAttended){
        Call<ResponseBody> responseBodyCall = Client.getClient().confirmAttendance(deviceMac, name, isAttended);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    showSuccessAlert();
                } else {
//                    Toast.makeText(getApplicationContext(), "Confirmation failed", Toast.LENGTH_LONG).show();
                    Log.d("server failure", response.code()+"");
                    showServerFailureAlert();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void showSuccessAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FingerprintActivity.this);

        alertDialogBuilder.setTitle("Attendance Check");

        alertDialogBuilder
                .setMessage("Your attendance check is successfully done. Keep focus on your class. :)")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void showAuthFailureAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FingerprintActivity.this);

        alertDialogBuilder.setTitle("Attendance Check");

        alertDialogBuilder
                .setMessage("Fingerprint authentication is not succeeded. Do you want to try again?")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                helper.startAuth(fingerprintManager, cryptoObject);
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void showServerFailureAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FingerprintActivity.this);

        alertDialogBuilder.setTitle("Attendance Check");

        alertDialogBuilder
                .setMessage("Server authentication is not succeeded. Do you want to try again?")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                helper.startAuth(fingerprintManager, cryptoObject);
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}

