package com.mobileapp.rutgers.touch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    FrameLayout mDrawingLayout;
    LinearLayout mTextLayout;
    TextView TouchCount_text;
    TextView pointer1_text;
    TextView pointer2_text;
    TextView pointer3_text;
    TextView pointer4_text;
    TextView pointer5_text;

    int numberOfPointers = 0;
    int maxPointers = 5;
    Paint mPaint;
    int pointerIndex;
    int pointerID;
    int touchRadius = 120;

    float mX;
    float mY;
    Canvas mCanvas;

    public ArrayList<Path> pointerPath = new ArrayList<>();
    public ArrayList<Paint> pointerPaint = new ArrayList<>();
    public String[] pointerColor = { "#ff9900", "#990088", "#f00099", "#9999ff", "#888800"};

    ArrayList<pointerView> pointerViewList = new ArrayList<>();
    public Path[] circlePath = new Path[maxPointers];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawingLayout = (FrameLayout) findViewById(R.id.canvasLayout);
        mTextLayout = (LinearLayout) findViewById(R.id.textLayout);
        TouchCount_text = (TextView) findViewById(R.id.NoOfTouches);
        pointer1_text = (TextView) findViewById(R.id.touch1);
        pointer2_text = (TextView) findViewById(R.id.touch2);
        pointer3_text = (TextView) findViewById(R.id.touch3);
        pointer4_text = (TextView) findViewById(R.id.touch4);
        pointer5_text = (TextView) findViewById(R.id.touch5);


        for(int i = 0; i< maxPointers; i++)
        {
            pointerPath.add(i,new Path());
            //----------------------------------
            //----------
            //setting the path of the circle for each of the drawn strokes
            circlePath[i] = new Path();

            //Configure new paint properties for each pointer
            mPaint = new Paint();
            mPaint.setColor(Color.parseColor(pointerColor[i]));
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(8);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setDither(true);

            pointerPaint.add(i, mPaint);
        }

        mDrawingLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        //Set Text Fields to visible:
                        mTextLayout.setVisibility(View.VISIBLE);

                        pointerIndex = event.getActionIndex();

                        pointerID = event.getPointerId(pointerIndex);
                        mX = event.getX(pointerIndex);
                        mY = event.getY(pointerIndex);

                        numberOfPointers = event.getPointerCount();
                        TouchCount_text.setText("No: " +numberOfPointers);

                        pointerViewList.add(pointerID, new pointerView(getBaseContext(), pointerID));
                        pointerViewList.get(pointerID).setmX(mX);
                        pointerViewList.get(pointerID).setmY(mY);

                        if (mDrawingLayout.getParent() != null)
                            mDrawingLayout.removeView(pointerViewList.get(pointerID));

                        mDrawingLayout.addView(pointerViewList.get(pointerID));

                        pointerPath.get(pointerID).reset();
                        pointerPath.get(pointerID).moveTo(mX, mY);
                        circlePath[pointerID].reset();
                        circlePath[pointerID].moveTo(mX, mY);
                        pointerViewList.get(pointerID).invalidate();
                        break;
                    }

                    case MotionEvent.ACTION_MOVE:
                        for (int i = 0; i < event.getPointerCount(); i++) {
                            int ID = event.getPointerId(i);
                            float x = event.getX(i);
                            float y = event.getY(i);

                            if (pointerViewList.get(ID) != null) {
                                pointerPath.get(ID).lineTo(x, y);
                                pointerViewList.get(pointerID).setmX(x);
                                pointerViewList.get(pointerID).setmY(y);
                                //----------------------------------------
                                //Set the text fields on the Display

                                setTextFields(ID,x,y);

                                //-----------------------------------------
                                circlePath[ID].reset();
                                circlePath[ID].addCircle(x, y, touchRadius, Path.Direction.CW);
                                pointerViewList.get(ID).invalidate();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:

                        for (int i = 0; i < event.getPointerCount(); i++) {
                            int ID = event.getPointerId(i);
                            if (pointerViewList.get(ID) != null) {
                                circlePath[ID].reset();
                                pointerViewList.get(ID).invalidate();
                            }
                        }
                        resetTextFields();
                        mTextLayout.setVisibility(View.INVISIBLE);
                        break;
                }
                return true;
            }
        });
    }

    private void resetTextFields() {
        String pointerTextFill = " ";
        pointer1_text.setText(pointerTextFill);
        pointer2_text.setText(pointerTextFill);
        pointer3_text.setText(pointerTextFill);
        pointer4_text.setText(pointerTextFill);
        pointer5_text.setText(pointerTextFill);
    }

    private void setTextFields(int ID, float x, float y) {
        String pointerTextFill = "P: " + (ID + 1) + " (" + x+" , "+y+ ")";
        if(ID == 0){
            pointer1_text.setText(pointerTextFill);}
        if(ID == 1){
            pointer2_text.setText(pointerTextFill);}
        if(ID == 2){
            pointer3_text.setText(pointerTextFill);}
        if(ID == 3){
            pointer4_text.setText(pointerTextFill);}
        if(ID == 4){
            pointer5_text.setText(pointerTextFill);}
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    private class pointerView extends View {

        Bitmap mBitmap;
        int pID;
        float LocX;
        float LocY;

        private final Paint circlePaint = new Paint();

        public pointerView(Context context, int pointer) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);

            pID = pointer;
            circlePaint.setStyle(Paint.Style.FILL);
            circlePaint.setColor(Color.parseColor(pointerColor[pID]));
            circlePaint.setStrokeJoin(Paint.Join.ROUND);
            circlePaint.setStrokeCap(Paint.Cap.ROUND);
            circlePaint.setAntiAlias(true);
        }

        public void setmX(float x){
            this.LocX = x;
        }

        public void setmY(float y){
            this.LocY = y;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            Log.e("TOUCH", "In OnSizeChanged method");
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Log.e("TOUCH", "In onDraw method: " +pID);
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, 0f, 0f,null);
            mCanvas = canvas;
            if(numberOfPointers>=1 ) {
                canvas.drawPath(pointerPath.get(pID), pointerPaint.get(pID));
                canvas.drawPath(circlePath[pID], circlePaint);
            }
        }
    }
}


