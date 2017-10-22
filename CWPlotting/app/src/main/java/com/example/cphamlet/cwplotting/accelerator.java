package com.example.cphamlet.cwplotting;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import static android.R.id.list;

public class accelerator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_accelerator);

    }



    public void LoadBack(View v) {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

}