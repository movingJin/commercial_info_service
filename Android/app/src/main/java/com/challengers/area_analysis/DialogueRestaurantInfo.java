package com.challengers.area_analysis;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Jin on 2016-11-08.
 */
public class DialogueRestaurantInfo extends Dialog {

    private TextView tvRestaurantName;
    private TextView tvRestaurantAddr;
    private TextView tvRestaurantSDate;
    private TextView tvRestaurantType;

    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.3f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialogue_restaurant_info);
        setCancelable(true);

        tvRestaurantName= (TextView) findViewById(R.id.tvRestaurantName);
        tvRestaurantAddr = (TextView) findViewById(R.id.tvRestaurantAddr);
        tvRestaurantSDate = (TextView) findViewById(R.id.tvRestaurantSDate);
        tvRestaurantType = (TextView) findViewById(R.id.tvRestaurantType);

        String start_date=data.split("\\,")[3].trim().substring(0,4)+".";
        start_date+=data.split("\\,")[3].trim().substring(4,6)+".";
        start_date+=data.split("\\,")[3].trim().substring(6,8);

        tvRestaurantName.setText(data.split("\\,")[1].trim());
        tvRestaurantAddr.setText(data.split("\\,")[2].trim());
        tvRestaurantSDate.setText(start_date);
        tvRestaurantType.setText(data.split("\\,")[4].trim());
    }

    public DialogueRestaurantInfo(Context context, String data) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.data = data;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_UP)
            this.dismiss();
        return false;
    }
}
