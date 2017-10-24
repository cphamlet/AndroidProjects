package com.example.cphamlet.cwplotting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.*;
import java.util.ArrayList;

/**
 * Created by cphamlet on 9/28/17.
 */

public class PlotView extends View {

    public float MEAN = 0f;
    public float VARIANCE = 0f;
    private ArrayList<Float> list = new ArrayList<Float>();
    public PlotView(Context context){
        super(context);

    }

    public PlotView(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    public PlotView(Context context, AttributeSet attrs, int def){
        super(context, attrs, def);
    }
    public PlotView(Context context, AttributeSet attrs, int def, int defArb){
        super(context, attrs, def, defArb);
    }


    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);

        paint.setARGB(255,255,255,255);

        canvas.drawLine(80, 40, 80, height-100, paint);
        canvas.drawLine(80, height-100, width-50, height-100, paint);

        int y = height-100;
        int x = 80;

        height = (height - 100 );
        float y_scale = height/100;
        int sum = 0;
        for(int i = 0; i < list.size(); i++){
            canvas.drawCircle(80+(width*i)/10, y_scale*(list.get(i))+30,  15, paint);
            sum+=list.get(i);
        }
        MEAN = (float) sum/list.size();
        int tempVar = 0;
        for(int i = 0; i < list.size(); i++){
            tempVar+= Math.pow((list.get(i) - MEAN),2);
        }
        VARIANCE = (float) tempVar/list.size();

        // Draw the shadow
      //  canvas.drawOval(new RectF(10,20,300,400), new Paint(Paint.ANTI_ALIAS_FLAG));
    }

    public void clearList(){
        list.clear();
    }
    public void addPoint(float point){
        if(list.size() >= 10){
            list.remove(0);
            list.add(point);
        }else {
            list.add(point);
        }
        this.invalidate();
    }



}
