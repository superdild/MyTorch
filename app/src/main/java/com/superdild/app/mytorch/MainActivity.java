package com.superdild.app.mytorch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraManager.TorchCallback;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_PERMISSION = 17;
    ToggleButton toggleOnOff;
    boolean isTorchEnable = false;
    boolean isCameraPermissionGranted = false;
    CameraManager cameraManager;
    CameraManager.TorchCallback torchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleOnOff = findViewById(R.id.button);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        // CameraManager.TorchCallback
        torchCallback = new TorchCallback() {
            @Override
            public void onTorchModeUnavailable(@NonNull String cameraId) {
                super.onTorchModeUnavailable(cameraId);
            }

            @Override
            public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                Log.w("onTorchModeChanged", "Enabled: " + enabled);
                isTorchEnable = enabled;
            }
        };

        //cameraManager.registerTorchCallback(torchCallback, null);

        final boolean hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasCameraFlash) {
            Toast.makeText(this, "No camera...", Toast.LENGTH_LONG).show();
            finish();
        }

        isCameraPermissionGranted = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

        toggleOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isCameraPermissionGranted) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
                    Log.v("onClick ", "!isCameraPermissionGranted");

                }  if (!isTorchEnable) {
                    flashLightOn();

                    Log.d("onClick ", "!isTorchEnable - ON");
                } else {
                    flashLightOff();

                    Log.e("onClick ", " isTorchEnable - OFF");
                }
            }
        });
    }

    private void flashLightOff() {
        // CameraManager
      //  cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraID = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraID, false);
            toggleOnOff.setText("OFF");
            toggleOnOff.setChecked(false);
            //  isTorchEnable = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void flashLightOn() {
        //CameraManager
       //  cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraID = cameraManager.getCameraIdList()[0];

            cameraManager.setTorchMode(cameraID, true);

            toggleOnOff.setText("ON");
            toggleOnOff.setChecked(true);
            //  isTorchEnable = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isCameraPermissionGranted = true;
                    Log.v("OnREquestPermission", "Permission Result: " + permissions[0].toString());
                } else {
                    Toast.makeText(this, "Permission....!!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraManager.unregisterTorchCallback(torchCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager.registerTorchCallback(torchCallback,null);
        Log.v("onREsume ", "OnRESUME");
        if (isTorchEnable)flashLightOn();
        else flashLightOff();

    }
}
