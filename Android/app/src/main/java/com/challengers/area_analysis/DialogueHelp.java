package com.challengers.area_analysis;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by Jin on 2016-11-08.
 */
public class DialogueHelp extends Dialog {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.3f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialogue_help);
        setCancelable(true);


    }

    public DialogueHelp(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_UP)
            this.dismiss();
        return false;
    }
}
