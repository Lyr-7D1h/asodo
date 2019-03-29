package com.example.ninja.Domain;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

public abstract class PermissionActivity extends AppCompatActivity {
    // Request codes
    private final int REQUEST_CODE_FINE_LOCATION = 123;

    // Get codes
    public int get_REQUEST_CODE_FINE_LOCATION() {
        return REQUEST_CODE_FINE_LOCATION;
    }

    // Permission callbacks
    public abstract void permissionAccepted(int requestCode);
    public abstract void permissionDeclined(int requestCode);

    // On Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAccepted(requestCode);
                } else {
                    permissionDeclined(requestCode);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
