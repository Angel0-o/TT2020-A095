package com.trabajoTerminal.violenceAlarm.User;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trabajoTerminal.violenceAlarm.DataModel.Alert;
import com.trabajoTerminal.violenceAlarm.DataModel.UserLocation;
import com.trabajoTerminal.violenceAlarm.ForegroundServices.ListenerService;
import com.trabajoTerminal.violenceAlarm.ForegroundServices.LocationService;
import com.trabajoTerminal.violenceAlarm.R;
import com.trabajoTerminal.violenceAlarm.databinding.FragmentMainScreenBinding;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainScreenFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private FusedLocationProviderClient fusedLocationClient;
    private FragmentMainScreenBinding binding;
    UserLocation userLocation;
    Alert alert;
    TimerTask timerTask;
    boolean isActive = false;
    //
    ImageView menuView;
    //menu variables
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    View headerView;
    TextView headerName;
    AlertDialog.Builder builder;
    //Sign up Variables
    String userID;
    String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentMainScreenBinding.bind(view);
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
        //Bundle
        final Bundle args = new Bundle();
        args.putString("userID", userID);
        //initializing variables
        menuView = binding.menuIcon;
        drawerLayout = binding.drawerId;
        navigationView = binding.navigationId;
        headerView = navigationView.getHeaderView(0);
        headerName = headerView.findViewById(R.id.header_name);
        userLocation = new UserLocation();
        alert= new Alert();
        alert.setAlertFlag(false);
        //Recover data from SingUp
        navigationDrawer();
        updateUI();
        alert.setMessage(
//                "Este es un mensaje de alerta, es muy probable que "+ userName
//                +" sea víctima de un asalto con violencia, esta es su última ubicación conocida "+
                "https://www.google.com/maps/search/?api=1&query="
        );
        //UserLocation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        askForLocation();
        binding.addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(MainScreenFragment.this).navigate(R.id.action_mainScreen_to_emergencyContactsFragment,args);
            }
        });
        binding.alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActive = true;
                Toast.makeText(getContext(),"Alerta iniciada ",Toast.LENGTH_LONG).show();
                try {
                    Intent intent = new Intent(getContext(), ListenerService.class);
                    intent.setAction("START");
                    intent.putExtra("userID",userID);
                    intent.putExtra("message",alert.getMessage());
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        getActivity().startForegroundService(intent);
                    }else{
                        getActivity().startService(intent);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(getContext(),"Error al iniciar la alerta",Toast.LENGTH_LONG).show();
                }
            }
        });
        binding.defuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActive){
                    isActive = false;
                    Toast.makeText(getContext(),"Alerta detenida manualmente",Toast.LENGTH_LONG).show();
                    try {
                        alert.setAlertFlag(false);
                        Intent intent = new Intent(getContext(), ListenerService.class);
                        intent.setAction("STOP");
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            getActivity().startForegroundService(intent);
                        }else{
                            getActivity().startService(intent);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getContext(),"Error al detener la alerta",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(),"El servicio no esta activo",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    //Firebase Functions
    private void updateUI(){
        Query userData = FirebaseDatabase.getInstance().getReference("Users").orderByKey().equalTo(userID);
        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    userName = dataSnapshot.child(userID).child("name").getValue(String.class);
                    String lastName = dataSnapshot.child(userID).child("lastName").getValue(String.class);
                    headerName.setText(userName+" "+lastName);
                }else {
                    Toast.makeText(getContext(),"Error: No se encontraron datos",Toast.LENGTH_LONG).show();
                    headerName.setText("Usuario desconocido");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //Navigation Functions
    private void navigationDrawer() {
        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.profile_edition);
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Bundle args = new Bundle();
        args.putString("userID", userID);
        switch (menuItem.getItemId()) {
            case R.id.profile_edition:
                NavHostFragment.findNavController(MainScreenFragment.this).navigate(R.id.action_mainScreen_to_profileEditionFragment,args);
                break;
            case R.id.help:
                NavHostFragment.findNavController(MainScreenFragment.this).navigate(R.id.action_mainScreen_to_helpFragment,args);
                break;
            case R.id.about:
                NavHostFragment.findNavController(MainScreenFragment.this).navigate(R.id.action_mainScreen_to_aboutFragment,args);
                break;
            case R.id.close_session:
                builder = new AlertDialog.Builder(getContext());
                builder.setMessage("¿Deseas cerrar sesión?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavHostFragment.findNavController(MainScreenFragment.this).navigate(R.id.action_mainScreen_to_loginFragment);
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
        return true;
    }

    //
    private void askForLocation() {
        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED))
        {
            fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        userLocation.setLatitude(String.valueOf(location.getLatitude()));
                        userLocation.setLongitude(String.valueOf(location.getLongitude()));
                    }else {
                        Toast.makeText(getContext(),"No se pudo obtener la ubicación",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            });
        }
    }
}