package com.trabajoTerminal.violenceAlarm.ForegroundServices;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trabajoTerminal.violenceAlarm.Container;
import com.trabajoTerminal.violenceAlarm.R;

public class LocationService extends Service {
    String userID;
    String alertMessage;
    int limit = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            userID = intent.getStringExtra("userID");
            alertMessage = intent.getStringExtra("message");
            if(action != null){
                if ("START".equals(action)){
                    startLocationService();
                } else if("STOP".equals(action)){
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "LocationID", "LocationService", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
//            Toast.makeText(getApplicationContext(), "Alerta iniciada: ", Toast.LENGTH_SHORT).show();
            if(limit < 3){
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    String latitude = String.valueOf(locationResult.getLastLocation().getLatitude());
                    String longitude = String.valueOf(locationResult.getLastLocation().getLongitude());
                    String smsText = alertMessage + latitude + "," + longitude;
//                    Toast.makeText(getApplicationContext(),smsText,Toast.LENGTH_SHORT).show();
                    sendSMS(smsText);
//                Toast.makeText(getApplicationContext(), "Ubicación: (" + latitude + "," + longitude + ")", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(LocationService.this,"Alerta detenida automaticamente",Toast.LENGTH_LONG).show();
                limit = 0;
                stopLocationService();
            }
        }
    };

    private void startLocationService() {
        createNotificationChannel();
        Intent intent1 = new Intent(this, Container.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        Notification notification = new NotificationCompat.Builder(this, "LocationID")
                .setContentTitle("Modulo de rastreo")
                .setContentText("Enviando coordenadas...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(1, notification);
    }


    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    //Send SMS
    private void sendSMS(final String text) {
        limit++;
        final SmsManager smsManager = SmsManager.getDefault();

        Query userData = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("contacts");
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    smsManager.sendTextMessage(postSnapshot.getKey(),null,  text,null,null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error: " + error,Toast.LENGTH_LONG).show();
            }
        });
        userData.getRef().removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
