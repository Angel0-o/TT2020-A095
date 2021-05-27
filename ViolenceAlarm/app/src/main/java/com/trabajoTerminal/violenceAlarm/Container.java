package com.trabajoTerminal.violenceAlarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class Container extends AppCompatActivity {
    static final int REQUEST_CODE = 123;
    String []listPermissions = new String[]{
        Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.FOREGROUND_SERVICE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        askForPermissions();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea salir de la aplicación?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

    }

    //Permissions
    private void askForPermissions() {
        if ((ContextCompat.checkSelfPermission(Container.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(Container.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(Container.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(Container.this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(Container.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(Container.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(Container.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED))
        {
            // When Permission is not granted
            if ((ActivityCompat.shouldShowRequestPermissionRationale(Container.this, Manifest.permission.ACCESS_COARSE_LOCATION)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(Container.this, Manifest.permission.ACCESS_FINE_LOCATION)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(Container.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(Container.this, Manifest.permission.FOREGROUND_SERVICE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(Container.this, Manifest.permission.INTERNET)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(Container.this, Manifest.permission.RECORD_AUDIO)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(Container.this, Manifest.permission.SEND_SMS))) {
                ActivityCompat.requestPermissions(Container.this, listPermissions, REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(Container.this, listPermissions, REQUEST_CODE);
            }
        } else {
            // Permission has already been granted
            Toast.makeText(Container.this , "Permisos concedidos", Toast.LENGTH_LONG).show();
            Log.d("requestPermissions", "Se obtuvieron los permisos correspondientes.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.length > 0) && (
                    grantResults[0] +
                            grantResults[1] +
                            grantResults[2] +
                            grantResults[3] +
                            grantResults[4] +
                            grantResults[5] +
                            grantResults[6] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted, yay! Do the location task you need to do.
                Toast.makeText(Container.this, "Se obtuvo el permiso requerido", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Container.this, "Se denego el permiso requerido", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(Container.this, listPermissions, REQUEST_CODE);
                //Open Permissions Screen
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package",getPackageName(),null);
//                intent.setData(uri);
//                startActivity(intent);
            }
        }
    }

}