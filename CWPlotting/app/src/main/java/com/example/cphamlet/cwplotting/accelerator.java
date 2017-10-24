package com.example.cphamlet.cwplotting;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class accelerator extends AppCompatActivity implements SensorEventListener{

    //  private PlotView plotView = (PlotView) findViewById(R.id.plotview);
    private PlotView plotView;
    private Sensor requestedSensor;
    private SensorManager sm;
    private int SENSOR_TYPE;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_accelerator);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SENSOR_TYPE = getIntent().getExtras().getInt("sensor_type");
        requestedSensor = sm.getDefaultSensor( SENSOR_TYPE );
        TextView maxRange = (TextView) findViewById(R.id.maxRange);
        maxRange.setText(Float.toString(requestedSensor.getMaximumRange()));

        findViewById(R.id.prox_close).setVisibility(View.INVISIBLE);
        findViewById(R.id.prox_far).setVisibility(View.INVISIBLE);
        findViewById(R.id.accell_animno).setVisibility(View.INVISIBLE);
        findViewById(R.id.accell_animyes).setVisibility(View.INVISIBLE);

        System.out.println("Registering");
        sm.registerListener(this, requestedSensor, 1000000);

    }

    public void LoadBack(View v) {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (requestedSensor != null) {
           sm.registerListener(this, requestedSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            if(SENSOR_TYPE == Sensor.TYPE_PROXIMITY){
                findViewById(R.id.accell_animno).setVisibility(View.INVISIBLE);
                findViewById(R.id.accell_animyes).setVisibility(View.INVISIBLE);
                findViewById(R.id.prox_far).setVisibility(View.VISIBLE);

            }else if(SENSOR_TYPE == Sensor.TYPE_ACCELEROMETER){
                findViewById(R.id.prox_close).setVisibility(View.INVISIBLE);
                findViewById(R.id.prox_far).setVisibility(View.INVISIBLE);
                findViewById(R.id.accell_animno).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.prox_close).setVisibility(View.INVISIBLE);
        findViewById(R.id.prox_far).setVisibility(View.INVISIBLE);
        findViewById(R.id.accell_animno).setVisibility(View.INVISIBLE);
        findViewById(R.id.accell_animyes).setVisibility(View.INVISIBLE);
        System.out.println("Paused");
        if (requestedSensor != null) {
            sm.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        plotView = (PlotView) findViewById(R.id.plotview);
        float value = 0;
        if(SENSOR_TYPE == Sensor.TYPE_ACCELEROMETER){
            System.out.println("Accel"+(i++));
            value = (float) Math.sqrt(Math.pow(sensorEvent.values[0],2) +
                                Math.pow(sensorEvent.values[1],2) +
                                Math.pow(sensorEvent.values[2],2) );
            if(value > 15){
                findViewById(R.id.accell_animno).setVisibility(View.INVISIBLE);
                findViewById(R.id.accell_animyes).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.accell_animno).setVisibility(View.VISIBLE);
                findViewById(R.id.accell_animyes).setVisibility(View.INVISIBLE);
            }

        }else if(SENSOR_TYPE == Sensor.TYPE_PROXIMITY){

            value = sensorEvent.values[0]*11;
            System.out.println("Prox"+(i++));
            System.out.println("val1: "+sensorEvent.values[0]);
            System.out.println("val1: "+sensorEvent.values[1]);
            System.out.println("val1: "+sensorEvent.values[2]);
            if(value > 50){
                findViewById(R.id.prox_close).setVisibility(View.VISIBLE);
                findViewById(R.id.prox_far).setVisibility(View.INVISIBLE);
            }else{
                findViewById(R.id.prox_close).setVisibility(View.INVISIBLE);
                findViewById(R.id.prox_far).setVisibility(View.VISIBLE);
            }
        }
        System.out.println("Added sensor change " + value);
        plotView.addPoint(value);
        TextView mean = (TextView) findViewById(R.id.meanID);
        mean.setText("Mean: " + plotView.MEAN);

        TextView variance = (TextView) findViewById(R.id.varianceID);
        variance.setText("Var: " + plotView.VARIANCE);



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}