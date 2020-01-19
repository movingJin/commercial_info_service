package com.challengers.area_analysis;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Jin on 2016-11-13.
 */
public class SettingActivity extends Dialog{

    private Switch svSwitch;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    private boolean onSkyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);

        setTitle("설정");
        setContentView(R.layout.activity_settings);
        settings=this.getContext().getSharedPreferences("saved_setting", 0);
        editor=settings.edit();
        onSkyView=settings.getBoolean("onSkyView", false);   //값이 없다면 초기값을 false로 지정


        svSwitch=(Switch)findViewById(R.id.svSwitch);
        svSwitch.setChecked(onSkyView);
        svSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSkyView = isChecked;
                editor.putBoolean("onSkyView", onSkyView);
                editor.commit();
                if (onSkyView) {
                    Toast.makeText(SettingActivity.this.getContext(), "스카이뷰 모드가 활성화 됩니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingActivity.this.getContext(), "스카이뷰 모드가 비활성화 됩니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public SettingActivity(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);

    }

}
