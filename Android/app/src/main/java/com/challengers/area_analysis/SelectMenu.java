package com.challengers.area_analysis;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
/**
 * Created by Jin on 2016-11-01.
 */
public class SelectMenu extends AppCompatActivity {
    EditText etAddress;
    ImageButton btFindAddr;
    Button btType;
    Button btInquire;
    Button btHelp;
    ListView listAdress;

    CustomAdapter adapter;

    String[][] addrData;
    String[] geoData;

    final String items[] = {"경양식","김밥(도시락)","분식","뷔페식","생선회",
            "식육","일식","중국식","통닭(치킨)","패스트푸드",
            "한식","호프","회집"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        etAddress=(EditText) findViewById(R.id.etAddress);
        btFindAddr =(ImageButton)findViewById(R.id.btFindAddr);
        btType=(Button)findViewById(R.id.btType);
        btInquire=(Button)findViewById(R.id.btInquire);
        btHelp=(Button)findViewById(R.id.btHelp);
        listAdress=(ListView)findViewById(R.id.listAddress);

        adapter = new CustomAdapter();
        listAdress.setAdapter(adapter);

        btFindAddr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                adapter.clear();
                try {
                    addrData = new AddrService().execute(etAddress.getText().toString()).get();        //우편번호 조회 API로 완전한 주소를 알아내고
                    for (int i = 0; i < addrData.length; i++) {
                        adapter.add(addrData[i]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etAddress.getWindowToken(), 0);     //검색버튼 누른후 키보드가 자동으로 내려감
            }
        });//주소검색 버튼

        btType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder alt_bld = new AlertDialog.Builder(SelectMenu.this);
                //alt_bld.setIcon(R.drawable.icon);
                alt_bld.setTitle("업종 선택");
                alt_bld.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        btType.setText(items[item]);
                        Toast.makeText(getApplicationContext(), "업종 " + items[item] + "을 선택하셨습니다", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                AlertDialog alert = alt_bld.create();
                alert.show();
            }
        });//업종 선택 버튼

        btInquire.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if(etAddress.getText().toString().length()>6 && !(btType.getText().toString().equals("업종")))    //선택한 주소의 길이가 6이상, 업종을 선택한 경우에만 Main 실행
                    {
                        geoData = new GeoService().execute(etAddress.getText().toString(), btType.getText().toString()).get();        //얻어온 주소정보를 바탕으로 위도, 경도를 얻어옵니다.
                        if(geoData[3]!=null) {      //선택한 위경도 좌표정보가 정상적으로 확보된 경우

                            Intent intent = new Intent(SelectMenu.this, MainActivity.class);
                            intent.putExtra(MainActivity.EXTRAS_GEO_INFORMATION, geoData);
                            loadSettings(intent);
                            startActivity(intent);
                        }else{          //선택한 위경도 좌표정보가 NULL인 경우
                            Toast.makeText(getApplicationContext(), "올바른 주소형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "주소 또는 업종을 올바르게 선택해주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });//조회 버튼

        btHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 생성
                new DialogueHelp(SelectMenu.this).show();
            }
        }); // 도움말 버튼
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_menu, menu);
            menu.findItem(R.id.menu_settings).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                new SettingActivity(this).show();
                break;
        }
        return true;
    }

    void loadSettings(Intent intent){
        SharedPreferences settings=SelectMenu.this.getApplicationContext().getSharedPreferences("saved_setting", 0);
        SharedPreferences.Editor editor=settings.edit();
        boolean onSkyView=settings.getBoolean("onSkyView", false);   //값이 없다면 초기값을 false로 지정
        intent.putExtra(MainActivity.EXTRAS_SETTING_SKYVIEWON,onSkyView);
    }





    private class CustomAdapter extends BaseAdapter {
        private ArrayList<String[]> m_List;
        private LayoutInflater mInflator;

        public CustomAdapter() {
            super();
            m_List = new ArrayList<String[]>();
            mInflator = SelectMenu.this.getLayoutInflater();
        }

        public void add(String [] _msg) {
            if(_msg!=null) {
                m_List.add(_msg);
            }
        }

        public void clear() {
            m_List.clear();
        }

        @Override
        public int getCount() {
            return m_List.size();
        }

        @Override
        public Object getItem(int i) {
            return m_List.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.list_item_addr, null);
                viewHolder = new ViewHolder();
                viewHolder.tvPostal = (TextView) view.findViewById(R.id.tvPostal);
                viewHolder.tvNewAddr = (TextView) view.findViewById(R.id.tvNewAddr);
                viewHolder.tvAddress = (TextView) view.findViewById(R.id.tvAddr);
                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            String[] item = m_List.get(i);
            final String postal = item[0];
            final String newAddr = item[1];
            final String address = item[2];

            viewHolder.tvPostal.setText(postal);
            viewHolder.tvNewAddr.setText(newAddr);
            viewHolder.tvAddress.setText(address);

            // 리스트 아이템을 터치 했을 때 이벤트 발생
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 터치 시 해당 아이템등록
                    etAddress.setText(newAddr);
                }
            });

            return view;
        }

        private class ViewHolder {
            TextView tvPostal;
            TextView tvNewAddr;
            TextView tvAddress;
        }
    }
}
