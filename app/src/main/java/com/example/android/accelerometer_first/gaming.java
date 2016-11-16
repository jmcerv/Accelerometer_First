package com.example.android.accelerometer_first;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Random;

public class gaming extends AppCompatActivity implements SensorEventListener{

    private Sensor mySensor;
    private SensorManager SM;
    private SensorEvent se;

    private long lastUpdate = 0;
    //variavel para verificar aceleraçao
    float AC=3;

    boolean failed = false;
    boolean game_ended = false;
    boolean checknextx = false;
    boolean checknexty = false;
    boolean checknextxy = false;

    float xIni, yIni, zIni;

    int nivel;
    int current_image = 0;
    String[] setasImagens;
    String[] setas = {"","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gaming);

        Intent intent = getIntent();
        String level = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        nivel = Integer.parseInt(level);

        if (nivel == 1){setasImagens= new String[]{"left", "right", "up", "down"};}
//        else if (nivel == 2){setasImagens= new String[]{"left", "right", "up", "down", "rightleft", "updown"};}
//        else if (nivel == 3){setasImagens= new String[]{"left", "right", "circumflex", "up", "down", "rightleft", "updown"};}

        //Create our Sensor Manager
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        //Accelerometer Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Register sensor Listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SM.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        se=sensorEvent;
        mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                if (!setas[current_image].equals("")) {
                    boolean done = analysingInput(current_image + 1, setas[current_image], sensorEvent);

                    if (done || failed){
                        current_image++;

                        if (current_image == 3 || setas[current_image-1].equals("")){
                            game_ended = true;
                            current_image = 0;

                            ImageView imageView1 = (ImageView) findViewById(R.id.seta1);
                            ImageView imageView2 = (ImageView) findViewById(R.id.seta2);
                            ImageView imageView3 = (ImageView) findViewById(R.id.seta3);
                            if (imageView1.getTag().toString().equals("check") && imageView2.getTag().toString().equals("check") &&
                                    imageView3.getTag().toString().equals("check")){
                                //INFLAR AQUI UM FRAGMENTO COM O PARABENS
                                finish fragment = new finish();

                                getSupportFragmentManager().beginTransaction().add(R.id.container,fragment,"one").commit();
                            }else if (!imageView1.getTag().toString().equals("check") || !imageView2.getTag().toString().equals("check") ||
                                    !imageView3.getTag().toString().equals("check")){

                                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                                alertDialog.setTitle("Game Over");
                                alertDialog.setMessage("It seems time's up!\nPlay again?");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();

                            }
                            setas[0]=setas[1]=setas[2]=setas[3]="";
                        }
                        failed = false;
                    }
                }
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
            }
        }
    }

    public void CastSpell(View view){
        game_ended = false;
        final Button button = (Button) findViewById(R.id.magic);
        button.setEnabled(false);
        int game_time = 10000;

        geraSetas();
        setPosInicial();

        new CountDownTimer(game_time, 1000) {
            TextView mTextField = (TextView) findViewById(R.id.timer);

            public void onTick(long millisUntilFinished) {
                if (!game_ended){
                    mTextField.setBackgroundColor(Color.WHITE);
                    mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                //here you can have your logic to set text to edittext
                if ((int) (millisUntilFinished) / 1000 == 5){
                    ImageView imagem = (ImageView) findViewById(R.id.seta1);
                    if (!imagem.getTag().toString().equals("check")){
                        crossImage(R.id.seta1);
                        failed = true;
                    }

                }else if((int) (millisUntilFinished) / 1000 == 2){
                    ImageView imagem = (ImageView) findViewById(R.id.seta2);
                    if (!imagem.getTag().toString().equals("check")){
                        crossImage(R.id.seta2);
                        failed = true;
                    }
                }
            }

            public void onFinish() {
                ImageView imagem = (ImageView) findViewById(R.id.seta3);
                if (!imagem.getTag().toString().equals("check")) {
                    crossImage(R.id.seta3);
                    failed = true;
                }
                button.setEnabled(true);
                if (!game_ended){
                    mTextField.setText("Time is up");
                    mTextField.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void geraSetas() {
        Random randNumber = new Random();
        ImageView imagem;

        for(int i=0;i<setas.length-1;i++){
            //Atribui um formato de seta aleatorio
            setas[i]=setasImagens[randNumber.nextInt(setasImagens.length)];
            //vai buscar o ID da seta aleatoria ao drawable
            int setaId=getResources().getIdentifier(setas[i], "drawable", getPackageName());
            //vai buscar o ID da imagem que é para preencher
            int setaPos=getResources().getIdentifier("seta"+(i+1), "id", getPackageName());

            imagem=(ImageView) findViewById(setaPos);

            imagem.setBackgroundResource(setaId);

            imagem.setTag("Has an image");
        }
    }

    private boolean analysingInput(int imagePos,String seta,SensorEvent sensorEvent){
        int n=getResources().getIdentifier("seta"+imagePos,"id", getPackageName());

        if (seta.equals("right")){
            if (sensorEvent.values[0] <= xIni-AC){
                checkImage(n);
                return true;
            }
        }
        else if (seta.equals("left")){
            if (sensorEvent.values[0] >= xIni+AC){
                checkImage(n);
                return true;
            }
        }

        else if (seta.equals("up")){
            if (sensorEvent.values[1] <= yIni-AC){
                checkImage(n);
                return true;
            }
        }
        else if (seta.equals("down")){
            if (sensorEvent.values[1] >= yIni+AC){
                checkImage(n);
                return true;
            }
        }
        else if (seta.equals("rightleft")){
            if (checknextx){
                if (sensorEvent.values[0] <= xIni-AC) {
                    checkImage(n);
                    checknextx = false;
                    return true;
                }
            }
            else if (sensorEvent.values[0] >= xIni+AC){
                checknextx = true;
                return false;
            }
        }
        else if (seta.equals("updown")){
            if (checknexty){
                if (sensorEvent.values[1] >= yIni+AC){
                    checkImage(n);
                    checknexty = false;
                    return true;
                }
            }
            else if (sensorEvent.values[1] <= yIni-AC){
                checknexty = true;
                return false;
            }
        }
        else if (seta.equals("circumflex")){
            if (checknextxy){
                if (sensorEvent.values[1] >= yIni+AC && sensorEvent.values[0] <= xIni-(AC/2)){
                    checkImage(n);
                    checknextxy = false;
                    return true;
                }
            }
            else if (sensorEvent.values[1] <= yIni-AC && sensorEvent.values[0] <= xIni-(AC/2)){
                checknextxy = true;
                return false;
            }
        }
        return false;
    }

    private void checkImage(int idSeta){
        ImageView imageView=(ImageView) findViewById(idSeta);

        imageView.setBackgroundResource(R.drawable.check);

        imageView.setTag("check");
        setPosInicial();
    }

    private void crossImage(int idSeta){
        ImageView imageView=(ImageView) findViewById(idSeta);

        imageView.setBackgroundResource(R.drawable.cross);

        imageView.setTag("cross");
        setPosInicial();
    }

    private void setPosInicial(){
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
            xIni=se.values[0];
            yIni=se.values[1];
            zIni=se.values[2];
        }
    }
}

