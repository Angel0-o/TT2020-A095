package com.trabajoTerminal.violenceAlarm.ForegroundServices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.trabajoTerminal.violenceAlarm.Container;
import com.trabajoTerminal.violenceAlarm.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ListenerService extends Service {
    private final static String serviceID = "ListenerServiceID";
    //Demand
    private final static String PASAME = "pásame";
    private final static String DAME = "dame";
    private final static String SACA = "saca";
    private final static String SAQUEN = "saquen";
    private final static String TODO = "todo";
    private final static String TRAIGAN = "traigan";
    private final static String TIENES = "tienes";
    private final static String TENGAN = "tengan";
    //Devices
    private final static String CELULAR = "celular";
    private final static String TELEFONO = "teléfono";
    //Money
    private final static String DINERO = "dinero";
    private final static String BILLETE = "billete";
    private final static String LANA = "lana";
    private final static String VARO = "varo";
    // valuable things
    private final static String CARTERA = "cartera";
    private final static String MOCHILA = "mochila";
    private final static String BOLSA = "bolsa";
    private final static String RELOJ = "reloj";
    //Warning
    private final static String NO = "no";
    private final static String VALIERON = "valieron";
    private final static String VALIO = "valió";
    private final static String CAYO = "cayó";
    private final static String CARGO = "cargó";
    //Violence suffix
    private final static String BERGA = "berga";
    private final static String MAMADA = "m*****";
    private final static String CHINGADA = "c*******";
    //Prefix
    private final static String HIJO = "hijo";
    private final static String PINCHE = "pinche";
    private final static String MADRE = "madre";
    //Violence words
    private final static String PENDEJO = "p******";
    private final static String CHINGAR = "chingar";
    private final static String CHINGADAMADRE = "chingadamadre";
    private final static String PERRA = "perra";
    private final static String PUTO = "p***";
    //Dead threat
    private final static String BALAZO = "balazo";
    private final static String PLOMAZO = "plomazo";
    private static final int RECOGNIZER_RESULT = 1;
    //Timer
    int seconds;
    Double time = 0.0;
    Timer timer;
    TimerTask timerTask;
    //Speech
    SpeechRecognizer speechRecognizer;
    Intent speechIntent;
    String line;
    String userID;
    String alertMessage;
    boolean isActive = false;
    int levelDemand = 0;
    int levelWarning = 0;
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
                    startListener();
                } else if("STOP".equals(action)){
                    stopListenerService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    serviceID, "Listener", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void startListener() {
        isActive = true;
        createNotificationChannel();
        Intent intent1 = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), serviceID)
                .setContentTitle("Servicio de escucha")
                .setContentText("Servicio ejecutandose")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();
        timer = new Timer();
        startRecognizer();
        startForeground(1, notification);
    }

    private void stopListenerService(){
        if (isActive){
            stopForeground(true);
            stopSelf();
            speechRecognizer.stopListening();
            isActive = false;
        }else {
            Toast.makeText(ListenerService.this,"El servicio no esta activo",Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecognizer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                time++;
                int round = (int) Math.round(time);
                seconds = ((round % 86400) % 3600);
                if(seconds % 60 == 0){
                    levelDemand = 0;
                    levelWarning = 0;
                }
            }
        };
        timer.schedule(timerTask,0,1000);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(ListenerService.this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-MX");
//        speechIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,1000);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                speechRecognizer.startListening(speechIntent);
            }

            @Override
            public void onError(int error) {
                speechRecognizer.startListening(speechIntent);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> words = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(words != null){
                    line = words.get(0);
//                    Toast.makeText(ListenerService.this,line,Toast.LENGTH_SHORT).show();
                    if(line.contains(PASAME)){
                        levelDemand++;
                    }if (line.contains(DAME)) {
                        levelDemand++;
                    }if (line.contains(SACA)) {
                        levelDemand++;
                    }if (line.contains(SAQUEN)) {
                        levelDemand++;
                    }if (line.contains(TIENES)) {
                        levelDemand++;
                    }if (line.contains(TENGAN)) {
                        levelDemand++;
                    }if (line.contains(TRAIGAN)){
                        levelDemand++;
                    }if (line.contains(CELULAR)){
                        levelDemand++;
                    }if (line.contains(TELEFONO)){
                        levelDemand++;
                    }if (line.contains(DINERO)){
                        levelDemand++;
                    }if (line.contains(BILLETE)){
                        levelDemand++;
                    }if (line.contains(LANA)){
                        levelDemand++;
                    }if (line.contains(VARO)){
                        levelDemand++;
                    }if (line.contains(CARTERA)){
                        levelDemand++;
                    }if (line.contains(MOCHILA)){
                        levelDemand++;
                    }if (line.contains(BOLSA)){
                        levelDemand++;
                    }if (line.contains(TODO)){
                        levelDemand++;
                    }if (line.contains(VALIERON)){
                        levelWarning++;
                    }if (line.contains(VALIO)){
                        levelWarning++;
                    }if (line.contains(CAYO)){
                        levelWarning++;
                    }if (line.contains(CARGO)){
                        levelWarning++;
                    }if (line.contains(HIJO)){
                        levelWarning++;
                    }if (line.contains(PENDEJO)){
                        levelWarning++;
                    }if (line.contains(PINCHE)){
                        levelWarning++;
                    }if (line.contains(MADRE)){
                        levelWarning++;
                    }if (line.contains(MAMADA)){
                        levelWarning++;
                    }if (line.contains(CHINGADA)){
                        levelWarning++;
                    }if (line.contains(PERRA)){
                        levelWarning++;
                    }if (line.contains(BERGA)){
                        levelWarning++;
                    }if (line.contains(PUTO)){
                        levelWarning++;
                    }
//                    Toast.makeText(ListenerService.this,"LevelDemand: "+levelDemand+"\nLevelWarning: "+levelWarning,Toast.LENGTH_SHORT).show();
                    if (levelDemand > 4 && levelWarning > 5){
                        //start alert
                        Intent intent = new Intent(getApplicationContext(), LocationService.class);
                        intent.setAction("START");
                        intent.putExtra("userID",userID);
                        intent.putExtra("message",alertMessage);
                        levelWarning = 0;
                        levelDemand = 0;
                        speechRecognizer.stopListening();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            getApplication().startForegroundService(intent);
                        }else{
                            getApplication().startService(intent);
                        }
                    }else
                        speechRecognizer.startListening(speechIntent);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        speechRecognizer.startListening(speechIntent);
    }
}
