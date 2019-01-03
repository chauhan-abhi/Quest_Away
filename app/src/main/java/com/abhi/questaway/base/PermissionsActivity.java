package com.abhi.questaway.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public abstract class PermissionsActivity extends AppCompatActivity {

    private PermissionListener permissionListener;

    public void requestPermissions(String permission, Integer requestCode,
                                   PermissionListener permissionListener) {
        this.permissionListener = permissionListener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    // called when user has denied permission before
                    ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
                } else {
                    ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
                }
            } else {
                // permission already granted
                permissionListener.onGranted(requestCode);
            }
        } else {
            permissionListener.onGranted(requestCode);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0])
                == PackageManager.PERMISSION_GRANTED && permissionListener != null) {

            switch (requestCode) {
                case 1:
                    //ask for gps
                    permissionListener.onGranted(requestCode);
                    break;
                case 2:
                    // ask for call
                    permissionListener.onGranted(requestCode);
                    break;
                case 3:
                    //ask for receive sms
                    permissionListener.onGranted(requestCode);
                    break;
                case 4:
                    // ask for camera
                    permissionListener.onGranted(requestCode);
                    break;
                case 5:
                    // ask for write storage
                    permissionListener.onGranted(requestCode);
                    break;
            }
        } else {
            if (permissionListener != null) {
                permissionListener.onRejected(requestCode);
            }
        }
    }
}