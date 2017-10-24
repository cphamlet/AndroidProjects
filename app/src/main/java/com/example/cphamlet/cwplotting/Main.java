package com.example.cphamlet.cwplotting;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class Main extends AppCompatActivity {

    private SensorManager sm;
    private List<Sensor> l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sensorselector);

        sm = (SensorManager) getSystemService
                (Context.SENSOR_SERVICE);
        l = sm.getSensorList(Sensor.TYPE_ALL);
        Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor proximity = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        TextView t = (TextView) findViewById(R.id.accel_status);
        if(accelerometer != null){
            t.setText("Y; Res: "+accelerometer.getResolution() + " Rng: " +
                    accelerometer.getMaximumRange() );
        }
        t = (TextView) findViewById(R.id.prox_status);
        if(proximity != null) {
            t.setText("Y; Res: " + proximity.getResolution() + " Rng: " + proximity.getMaximumRange());
        }


    }

    public void changeViewAcc(View v){
        Intent intent = new Intent(this, accelerator.class);
        intent.putExtra("sensor_type", Sensor.TYPE_ACCELEROMETER);
        startActivity(intent);
        }
    public void changeViewProx(View v){
        Intent intent = new Intent(this, accelerator.class);
        intent.putExtra("sensor_type", Sensor.TYPE_PROXIMITY);
        startActivity(intent);
    }
}
