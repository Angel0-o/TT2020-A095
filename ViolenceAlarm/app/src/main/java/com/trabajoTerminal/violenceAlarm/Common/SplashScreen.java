package com.trabajoTerminal.violenceAlarm.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.trabajoTerminal.violenceAlarm.Container;
import com.trabajoTerminal.violenceAlarm.R;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMER = 2000;
    //Variables
    ImageView backgroundImage;
    TextView poweredByLine;

    //Animations
    Animation sideAnim,bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        //Hooks
        backgroundImage = findViewById(R.id.background_image);
        poweredByLine = findViewById(R.id.powered_by);

        //Animations
        sideAnim = AnimationUtils.loadAnimation(this,R.anim.sideanimation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //set Animations on elements
        backgroundImage.setAnimation(sideAnim);
        poweredByLine.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Container.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIMER);
    }
}