package com.challengers.area_analysis;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Jin on 2016-11-08.
 */
public class DialogueSubwayPop extends Dialog {

    private TextView tvSubwayName;
    private TextView tvSubwayRide;
    private TextView tvSubwayAlight;
    private TextView tvSubwayDistance;

    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.3f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialogue_subway_info);
        setCancelable(true);

        tvSubwayName=(TextView)findViewById(R.id.tvSubwayName);
        tvSubwayRide=(TextView)findViewById(R.id.tvSubwayRide);
        tvSubwayAlight=(TextView)findViewById(R.id.tvSubwayAlight);
        tvSubwayDistance=(TextView)findViewById(R.id.tvSubwayDistance);

        tvSubwayName.setText(data.split("\\,")[1].trim());
        tvSubwayRide.setText(data.split("\\,")[2].trim());
        tvSubwayAlight.setText(data.split("\\,")[3].trim());
        tvSubwayDistance.setText(String.format("%.2f", Double.parseDouble(data.split("\\,")[4].trim())));
    }

    public DialogueSubwayPop(Context context, String data) {
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
