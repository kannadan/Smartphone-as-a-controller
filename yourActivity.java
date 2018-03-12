package com.example.omistaja.accel_example;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class yourActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    double ax,ay,az;   // these are the acceleration in x,y and z axis
    double ax_previous;
    double ay_previous;
    double az_previous;
    double margin = 1.10;
    double az_min = az - margin;
    double az_max = az + margin;
    double breakpoint = 0;
    double which_command;
   // double commands = [];
    double command;
    double i = 0;
    double az_value = 0;

    TcpClient mTcpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        new ConnectTask().execute("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your);
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void sendmessage(View view) {
        Log.d( "nappi", "painetu" );
        mTcpClient.sendMessage("Testi1");
        // Do something in response to button
    }

   /* public class Sensors extends AsyncTask<String, String, SensorClient> {
        private SensorClient msensor;

        @Override
        protected SensorClient doInBackground(String... message) {
            SensorManager mSensorManager = (SensorManager)
                    mActivity.getSystemService(SENSOR_SERVICE)
        ....
            return ..;
        }
    }*/

    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {

            //we create a TCPClient object
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("test", "response " + values[0]);
            //process server response here....

        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR){
            ax_previous = ax;
            ay_previous = ay;
            az_previous = az;
            ax= 180 * event.values[0] + 180;
            ay= 180 * event.values[1] + 180;
            az= 180 * event.values[2] + 180;



            create_command_with_position(100, 10, 2);



            //Log.d("Orientation", "ax:" + Double.toString(ax)+ " ay:" + Double.toString(ay) + " az:" + Double.toString(az));

            setContentView(R.layout.activity_your);
            TextView textView = (TextView) findViewById(R.id.arvot2);
            textView.setText("ax:" + String.format("%.2f", ax) +  "\n ay:" + String.format("%.2f", ay) + "\n az:" + String.format("%.2f", az));
        }
    }

    public void create_command_with_position(int az_value, int margin, int which_command){
        check_if_breakpoint(az_value, margin);
        create_command(which_command);
    }

    public void check_if_breakpoint(int az_value2, int margin2){
        az_min = az_value2 - margin2; //Asetetaan alaraja
        az_max = az_value2 + margin2; //Asetetaan yläraja
        if (az_max > 360 && az_min < 360){ //Tarkistetaan onko kierros pyörähtänyt ympäri yläkautta
            az_max = az_max - 360; //Jos kierros pyörähtänyt ympäri, vähennetään kokonainen kierros
            breakpoint = 1;
            //measure_position_breakpoint();
        }
        else if (az_max > 0 && az_min < 0){ //Tarkistetaan onko kierros pyörähtänyt ympäri alakautta
            az_min = az_min + 360; //Jos kierros pyörähtänyt ympäri, lisätään kokonainen kierros
            breakpoint = 1;
            //measure_position_breakpoint();
        }
        else {
            breakpoint = 0;
            //measure_position_create_command(); //Jos kierros ei ole pyörähtänyt, komento luodaan suoraan

        }
    }

    public void create_command(int which_command){
        if ((breakpoint == 1) && ((az_min <= az && az <= 360) || (0 <= az && az <= az_max))){ //Jos breakpoint ylitetään ja //Pilkotaan tarkasteluväli kahteen osaan. Ensimmäinen katsoo 360 asti ja toinen 0:sta eteenpäin
            Log.d("breakpoint", "breakpoint found, create a command");
            command = which_command;
            //commands[i+1] = which_command;

        }
        else if (az_min <= az && az <= az_max){
            //commands[i+1] = which_command;
            command = which_command;
            Log.d("breakpoint", "no breakpoint, create a command");
        }
    }

    /*public void measure_position_create_command(){
        if (az_min <= az <= az_max){ //Jos az arvo on haarukassa, toteutetaan komento
            create_command();
        }
    }

    public void measure_position_breakpoint(){
        if ((az_min <= az <= 360) || (0 <= az <= az_max)){ //Pilkotaan tarkasteluväli kahteen osaan. Ensimmäinen katsoo 360 asti ja toinen 0:sta eteenpäin
            create_command();
        }
    }*/



}
