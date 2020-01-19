package com.challengers.area_analysis;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Created by 이주희 on 2016-11-06.
 */

public class SlidingMenu extends Activity {

    //String values[];
    public TableLayout lvNavList;

    TextView graph_addr;
    ImageView bar[];
    TextView graph_count[];
    private ScaleAnimation scale[];
    TextView graph_total;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    void initSliding(){
        lvNavList = (TableLayout)findViewById(R.id.lv_activity_main_nav_list);
        lvNavList.setVisibility(View.INVISIBLE);
        lvNavList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lvNavList.setVisibility(View.INVISIBLE);
            }
        });
    }

    void showPopGraph(String data){
        bar = new ImageView[14];
        graph_count=new TextView[14];
        scale=new ScaleAnimation[14];

        ((Activity) this).setContentView(R.layout.activity_main);
        lvNavList = (TableLayout)findViewById(R.id.lv_activity_main_nav_list);
        lvNavList.setVisibility(View.INVISIBLE);
        lvNavList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvNavList.setVisibility(View.INVISIBLE);
            }
        });

        graph_addr=(TextView)findViewById(R.id.graph_addr);
        graph_addr.setText(data.split("\\,")[1].trim());
        bar[0] = (ImageView) findViewById(R.id.graph_bar0);
        bar[1] = (ImageView) findViewById(R.id.graph_bar1);
        bar[2] = (ImageView) findViewById(R.id.graph_bar2);
        bar[3] = (ImageView) findViewById(R.id.graph_bar3);
        bar[4] = (ImageView) findViewById(R.id.graph_bar4);
        bar[5] = (ImageView) findViewById(R.id.graph_bar5);
        bar[6] = (ImageView) findViewById(R.id.graph_bar6);
        bar[7] = (ImageView) findViewById(R.id.graph_bar7);
        bar[8] = (ImageView) findViewById(R.id.graph_bar8);
        bar[9] = (ImageView) findViewById(R.id.graph_bar9);
        bar[10] = (ImageView) findViewById(R.id.graph_bar10);
        bar[11] = (ImageView) findViewById(R.id.graph_bar11);
        bar[12] = (ImageView) findViewById(R.id.graph_bar12);
        bar[13] = (ImageView) findViewById(R.id.graph_bar13);
        ///graph_total=(TextView)findViewById(R.id.graph_total);

        graph_count[0]=(TextView) findViewById(R.id.graph_count0);
        graph_count[1]=(TextView) findViewById(R.id.graph_count1);
        graph_count[2]=(TextView) findViewById(R.id.graph_count2);
        graph_count[3]=(TextView) findViewById(R.id.graph_count3);
        graph_count[4]=(TextView) findViewById(R.id.graph_count4);
        graph_count[5]=(TextView) findViewById(R.id.graph_count5);
        graph_count[6]=(TextView) findViewById(R.id.graph_count6);
        graph_count[7]=(TextView) findViewById(R.id.graph_count7);
        graph_count[8]=(TextView) findViewById(R.id.graph_count8);
        graph_count[9]=(TextView) findViewById(R.id.graph_count9);
        graph_count[10]=(TextView) findViewById(R.id.graph_count10);
        graph_count[11]=(TextView) findViewById(R.id.graph_count11);
        graph_count[12]=(TextView) findViewById(R.id.graph_count12);
        graph_count[13]=(TextView) findViewById(R.id.graph_count13);



        double popData[]=new double[14];
        double sum=0, temp[]=new double[14];
        for(int i=0;i<14;i++){
            temp[i]=Integer.parseInt(data.split("\\,")[i+2].trim());
            sum=sum+temp[i];
        }
        //graph_total.setText(""+sum);

        for(int i=0;i<14;i++) {
            popData[i]=(temp[i]/sum)*50;
        }

        for(int i=0; i<14; i++) {
            scale[i] = new ScaleAnimation(1,(float)popData[i],1,1);
            scale[i].setDuration(4000);
            scale[i].setFillAfter(true);

            bar[i].setAnimation(scale[i]);
            System.out.println("testbug13: " + temp[i]);
            graph_count[i].setText("" + Math.round(temp[i]));
        }
    }
}
