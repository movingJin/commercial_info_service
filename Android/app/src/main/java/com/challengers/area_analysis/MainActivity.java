package com.challengers.area_analysis;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener {

    static final String API_KEY = "90a61605108790de3d329ddd7412c849";
    public static final String EXTRAS_GEO_INFORMATION = "GEO_INFORMATION";
    public static final String EXTRAS_SETTING_SKYVIEWON = "SETTING_SKYVIEWON";

    DBService mDBService;
    SlidingMenu mSlidingMenu;

    String[][] query_populaiton;
    String[][] query_subwaypop;
    String[][] query_restaurant;
    String[][] query_fail_ratio;
    String[][] query_rent_price;
    String[][] query_restaurant_count;

    String[] geoData;
    double searched_lat;
    double searched_lng;
    private boolean onSkyView;

    MapView mapView;
    TextView tvRestaurantCount;
    TextView tvFailRatio;
    TextView tvAvgRentPrice;

    //상권 마커
    ArrayList<MapPOIItem> commercial_list = new ArrayList<MapPOIItem>();

    //지하철역 마커
    ArrayList<MapPOIItem> subway_list = new ArrayList<MapPOIItem>();

    //유동인구 분포도
    ArrayList<MapPOIItem> pop_list = new ArrayList<MapPOIItem>();
    ArrayList<MapCircle> circle_list = new ArrayList<MapCircle>();


    static final String INFO_POP = "INFO_POP";
    static final String INFO_RESTAURANT = "INFO_RESTAURANT";
    static final String INFO_SUBPOP = "INFO_SUBPOP";

    private LinearLayout lvNavList;

    TextView graph_addr;
    ImageView bar[];
    TextView graph_count[];

    TextView graph_total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvRestaurantCount = (TextView) findViewById(R.id.tvRestaurantCount);
        tvFailRatio = (TextView) findViewById(R.id.tvFailRatio);
        tvAvgRentPrice = (TextView) findViewById(R.id.tvAvgRentPrice);

        query_populaiton = null;
        final Intent intent = getIntent();
        geoData = intent.getStringArrayExtra(EXTRAS_GEO_INFORMATION);
        searched_lat = Double.parseDouble(geoData[3]);
        searched_lng = Double.parseDouble(geoData[4]);

        onSkyView = intent.getBooleanExtra(EXTRAS_SETTING_SKYVIEWON, false);


        bar = new ImageView[14];
        graph_count = new TextView[14];

        System.out.println("testbug102 addr: " + geoData[0] + ", dong: " + geoData[1] + ", type: " + geoData[2] + ", lat: " + geoData[3] + ", lng: " + geoData[4]);
        // Using TedPermission library
        load_db();
        //mSlidingMenu=new SlidingMenu();
        initSliding();

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

                if (query_restaurant_count[0][0]!= "null")
                    tvRestaurantCount.setText("" + query_restaurant_count[0][0]);
                else
                    tvRestaurantCount.setText("0");

                String round_fail_ratio;
                if (query_fail_ratio[0][0] != "null")
                    round_fail_ratio = String.format("%.2f", Double.parseDouble(query_fail_ratio[0][0]) * 100);
                else
                    round_fail_ratio = "0";
                tvFailRatio.setText(round_fail_ratio + " %");


                String round_rent_price;
                if (query_rent_price[0][0] != "null")
                    round_rent_price = String.format("%.2f", Double.parseDouble(query_rent_price[0][0]));
                else
                    round_rent_price = "0";
                tvAvgRentPrice.setText(round_rent_price + " (만)");

                // MapView 객체생성 및 API Key 설정
                mapView = new MapView(MainActivity.this);
                mapView.setDaumMapApiKey(API_KEY);
                if (onSkyView)
                    mapView.setMapType(MapView.MapType.Hybrid);
                else
                    mapView.setMapType(MapView.MapType.Standard);
                mapView.setPOIItemEventListener(MainActivity.this);

                RelativeLayout mapViewContainer = (RelativeLayout) findViewById(R.id.map_view);
                mapViewContainer.addView(mapView);

                // 중심점 변경
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(searched_lat, searched_lng), true);

                //사용자가 선택한 위치를 마커로 찍음
                pointingTarget();

                //검색된 동 이름이 포함된 주소의 상점을 담고 표시
                if (query_restaurant != null) {
                    getCommercialItem();
                    setCommercialItem();
                }
                //가장 가까운 지하철 담고 표시
                getMinSubwayItem();
                setMinSubwayItem();

                if (query_populaiton != null) {
                    getPopulationItem(query_populaiton);
                    setPopulationItem();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("지도 서비스를 사용하기 위해서는 위치 접근 권한이 필요해요")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ;


    public void pointingTarget() {
        MapPOIItem target_marker = new MapPOIItem();
        target_marker.setItemName("");
        target_marker.setTag(1);
        target_marker.setMapPoint(MapPoint.mapPointWithGeoCoord(searched_lat, searched_lng)); //좌표 입력
        target_marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        target_marker.setCustomImageResourceId(R.drawable.img_target);
        target_marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        target_marker.setCustomImageAnchor(0.5f, 1.0f);

        mapView.addPOIItem(target_marker);
    }

    //commercial_list라는 Arraylist에 마커에 찍을 상점 이름과 좌표를 담음
    public void getCommercialItem() {
        for (int i = 0; i < query_restaurant.length; i++) {
            MapPOIItem commercial_marker = new MapPOIItem();
            //commercial_marker.setItemName(query_restaurant[i][0]);
            commercial_marker.setItemName(INFO_RESTAURANT + "," + query_restaurant[i][0] + "," + query_restaurant[i][1] + "," + query_restaurant[i][2] + "," + query_restaurant[i][3]);
            commercial_marker.setTag(0);
            commercial_marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(query_restaurant[i][4]), Double.parseDouble(query_restaurant[i][5]))); //좌표 입력
            commercial_marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            commercial_marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

            commercial_list.add(commercial_marker); //각 객체를 리스트에 담음
        }
    }

    //commercial_list에 담긴 상점 정보를 지도에 뿌려줌
    public void setCommercialItem() {
        for (int i = 0; i < commercial_list.size(); i++) {
            mapView.addPOIItem(commercial_list.get(i));
        }
    }

    //circle_list라는 Arraylist에 원을 그릴 좌표를 담음
    public void getPopulationItem(String[][] data) {

        for (int i = 0; i < data.length; i++) {
            int popSum = 0;
            for (int j = 2; j < 16; j++) {
                popSum = popSum + Integer.parseInt(data[i][j]);     //해당 지점 유동인구 합계
            }

            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(data[i][16]), Double.parseDouble(data[i][17])); //좌표 입력

            MapPOIItem pop_marker = new MapPOIItem();
            pop_marker.setItemName(INFO_POP + "," + data[i][1] + "," + data[i][2] + "," + data[i][3] + "," + data[i][4] + "," + data[i][5] + "," + data[i][6] + "," + data[i][7] + "," + data[i][8] + "," + data[i][9] + "," + data[i][10] + "," + data[i][11] + "," + data[i][12] + "," + data[i][13] + "," + data[i][14] + "," + data[i][15]);     //마커에를 클릭 표현될 내용 정의
            pop_marker.setTag(i);
            pop_marker.setMapPoint(mapPoint); //좌표 입력
            pop_marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            pop_marker.setCustomImageResourceId(R.drawable.img_walker);
            pop_marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
            pop_marker.setCustomImageAnchor(0.5f, 1.0f);

            pop_list.add(pop_marker); //각 객체를 리스트에 담음


            int radius = 30;   // radius 반경
            int r = 178, g = 235, b = 244;

            if (popSum > 1300) {
                r = 209;
                g = 178;
                b = 255;
                radius = 60;
            }        //전체 유동인구 수에 따라 원의 색과 지름 다르게 설정
            if (popSum > 2500) {
                r = 183;
                g = 240;
                b = 177;
                radius = 100;
            }
            if (popSum > 3700) {
                r = 255;
                g = 224;
                b = 140;
                radius = 150;
            }
            if (popSum > 6400) {
                r = 255;
                g = 167;
                b = 167;
                radius = 200;
            }

            MapCircle circle = new MapCircle(mapPoint, radius,
                    Color.argb(200, r, g, b), // 테두리 색깔
                    Color.argb(50, r, g, b) // 원 안에 색깔
            );
            circle.setTag(i);

            circle_list.add(circle); //각 객체를 리스트에 담음
        }
    }

    //circle_list에 담긴 정보를 지도에 뿌려줌
    public void setPopulationItem() {
        for (int i = 0; i < circle_list.size(); i++) {
            mapView.addPOIItem(pop_list.get(i));
            mapView.addCircle(circle_list.get(i));
        }
    }

    public void getMinSubwayItem() {
        double min_subway_distance = calculateDistance(searched_lat, searched_lng, Double.parseDouble(query_subwaypop[0][3]), Double.parseDouble(query_subwaypop[0][4]));
        int min_subway_index = 0;
        for (int i = 0; i < query_subwaypop.length; i++) {
            if (calculateDistance(searched_lat, searched_lng, Double.parseDouble(query_subwaypop[i][3]), Double.parseDouble(query_subwaypop[i][4])) <= min_subway_distance) {
                min_subway_distance = calculateDistance(searched_lat, searched_lng, Double.parseDouble(query_subwaypop[i][3]), Double.parseDouble(query_subwaypop[i][4]));
                min_subway_index = i;
            }
        }
        MapPOIItem subway_marker = new MapPOIItem();
        subway_marker.setItemName(INFO_SUBPOP + "," + query_subwaypop[min_subway_index][0] + "," + query_subwaypop[min_subway_index][1] + "," + query_subwaypop[min_subway_index][2] + "," + min_subway_distance);
        subway_marker.setTag(1);
        subway_marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(query_subwaypop[min_subway_index][3]), Double.parseDouble(query_subwaypop[min_subway_index][4]))); //좌표 입력
        subway_marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        subway_marker.setCustomImageResourceId(R.drawable.img_metro);
        subway_marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        subway_marker.setCustomImageAnchor(0.5f, 1.0f);

        subway_list.add(subway_marker); //각 객체를 리스트에 담음
    }

    //subway_list에 담긴 지하철역 중에 가장 가까운 역을 지도에 뿌려줌
    public void setMinSubwayItem() {
        mapView.addPOIItem(subway_list.get(0));
    }

    //마커를 눌렀을 때의 이벤트 포착
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        //MapPOIItem some_marker = new MapPOIItem();
        if (mapPOIItem.getItemName().split("\\,")[0].trim().equals(INFO_POP)) {  //유동인구 정보 마커리스너
            showPopGraph(mapPOIItem.getItemName());
            lvNavList.setVisibility(View.VISIBLE);
        } else if (mapPOIItem.getItemName().split("\\,")[0].trim().equals(INFO_RESTAURANT)) {//음식점 정보 마커리스너
            new DialogueRestaurantInfo(this, mapPOIItem.getItemName()).show();
        } else if (mapPOIItem.getItemName().split("\\,")[0].trim().equals(INFO_SUBPOP)) {//지하철 유동인구 정보 마커리스너
            new DialogueSubwayPop(this, mapPOIItem.getItemName()).show();
        }

    }

    //마커의 말풍선을 눌렀을 때의 이벤트 포착
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    //거리 재는 함수
    public double calculateDistance(double lat1,double lng1,double lat2,double lng2) {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dlng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dlng / 2) * Math.sin(dlng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    public void custombm() {
        MapPOIItem mCustomBmMarker = new MapPOIItem();
        String name = "Custom Bitmap Marker";
        mCustomBmMarker.setItemName(name);
        mCustomBmMarker.setTag(2);
        mCustomBmMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(searched_lat, searched_lng));
        mCustomBmMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.custom_callout_balloon);
        mCustomBmMarker.setCustomImageBitmap(bm);
        mCustomBmMarker.setCustomImageAutoscale(false);
        mCustomBmMarker.setCustomImageAnchor(0.5f, 0.5f);
        mapView.addPOIItem(mCustomBmMarker);
        mapView.selectPOIItem(mCustomBmMarker, true);
    }


    public void load_db() {
        mDBService = new DBService();
        ////////////////////////////////////////////////유동인구
        try {
            query_populaiton = mDBService.execute(DBService.QUERY_POPULATION, geoData[1]).get();      //파싱한 동 정보로 해당 동의 유동인구가 몇인지 쿼리를 날립니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (query_populaiton == null) System.out.println("Qeury 실패");
        else
            System.out.println("SPOT_CD: " + query_populaiton[0][0] + ", addr: " + query_populaiton[0][1] + ", 7t: " + query_populaiton[0][2] + ", 8t: " + query_populaiton[0][3] + ", lat: " + query_populaiton[0][16] + ", lng: " + query_populaiton[0][17]);
        mDBService.onCancelled();
        mDBService = new DBService();

        ///////////////////////////////////////////////////////////지하철 유동인구
        try {
            query_subwaypop = mDBService.execute(DBService.QUERY_SUBWAYPOP).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (query_subwaypop == null) System.out.println("Qeury 실패");
        else
            System.out.println("STA_NM_CD: " + query_subwaypop[0][0] + ", ride: " + query_subwaypop[0][1] + ", alight: " + query_subwaypop[0][2] + ", XPOINT: " + query_subwaypop[0][3] + ", YPOINT: " + query_subwaypop[0][4]);
        mDBService.onCancelled();
        mDBService = new DBService();

        ///////////////////////////////////////////////////////////동종 사업장 정보
        try {
            query_restaurant = mDBService.execute(DBService.QUERY_RESTAURANT, geoData[1], geoData[2]).get();   //파싱한 동 정보와 사용자가 선택한 영업종으로 해당 동 주변의 사업장 정보를 알아옵니다..
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (query_restaurant == null) System.out.println("Qeury 실패");
        else
            System.out.println("name: " + query_restaurant[0][0] + ", addr: " + query_restaurant[0][1] + ", launch_date: " + query_restaurant[0][2] + ", lat: " + query_restaurant[0][3] + ", lng: " + query_restaurant[0][4]);
        mDBService.onCancelled();
        mDBService = new DBService();

        ///////////////////////////////////////////////////////////폐업률 정보
        try {
            query_fail_ratio = mDBService.execute(DBService.QUERY_FAIL_RATIO, geoData[1], geoData[2]).get();   //파싱한 동 정보와 사용자가 선택한 영업종으로 해당 동 주변의 사업장 폐업률을 알아옵니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (query_fail_ratio == null) System.out.println("Qeury 실패");
        else System.out.println("fail_ratio: " + query_fail_ratio[0][0]);
        mDBService.onCancelled();
        mDBService = new DBService();

        ///////////////////////////////////////////////////////////임대가격 정보
        try {
            query_rent_price = mDBService.execute(DBService.QUERY_RENT_PRICE, geoData[1]).get();       //파싱한 동 정보로 사용자가 해당 동의 평균 임대가격을 알아옵니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (query_rent_price == null) System.out.println("Qeury 실패");
        else System.out.println("평당 임대료 (단위:만원): " + query_rent_price[0][0]);
        mDBService.onCancelled();
        mDBService = new DBService();

        ///////////////////////////////////////////////////////////사업장 개수 정보
        try {
            query_restaurant_count = mDBService.execute(DBService.QUERY_RESTAURANT_COUNT, geoData[1], geoData[2]).get();       //파싱한 동 정보로 사용자가 해당 동의 평균 임대가격을 알아옵니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (query_restaurant_count == null) System.out.println("Qeury 실패");
        else System.out.println("사업장의 개수: " + query_restaurant_count[0][0]);
        mDBService.onCancelled();

    }


    void initSliding() {
        lvNavList = (LinearLayout) findViewById(R.id.lv_activity_main_nav_list);
        lvNavList.setVisibility(View.INVISIBLE);
        lvNavList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvNavList.setVisibility(View.INVISIBLE);
            }
        });
    }


    void showPopGraph(String data) {

        graph_addr = (TextView) findViewById(R.id.graph_addr);
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

        graph_count[0] = (TextView) findViewById(R.id.graph_count0);
        graph_count[1] = (TextView) findViewById(R.id.graph_count1);
        graph_count[2] = (TextView) findViewById(R.id.graph_count2);
        graph_count[3] = (TextView) findViewById(R.id.graph_count3);
        graph_count[4] = (TextView) findViewById(R.id.graph_count4);
        graph_count[5] = (TextView) findViewById(R.id.graph_count5);
        graph_count[6] = (TextView) findViewById(R.id.graph_count6);
        graph_count[7] = (TextView) findViewById(R.id.graph_count7);
        graph_count[8] = (TextView) findViewById(R.id.graph_count8);
        graph_count[9] = (TextView) findViewById(R.id.graph_count9);
        graph_count[10] = (TextView) findViewById(R.id.graph_count10);
        graph_count[11] = (TextView) findViewById(R.id.graph_count11);
        graph_count[12] = (TextView) findViewById(R.id.graph_count12);
        graph_count[13] = (TextView) findViewById(R.id.graph_count13);


        double popData[] = new double[14];
        double sum = 0, temp[] = new double[14];
        for (int i = 0; i < 14; i++) {
            temp[i] = Integer.parseInt(data.split("\\,")[i + 2].trim());
            sum = sum + temp[i];
        }
        //graph_total.setText(""+sum);

        for (int i = 0; i < 14; i++) {
            popData[i] = (temp[i] / sum) * 50;
        }

        for (int i = 0; i < 14; i++) {
            ScaleAnimation scale;
            scale = new ScaleAnimation(1, (float) popData[i], 1, 1);
            scale.setDuration(4000);
            scale.setFillAfter(true);

            bar[i].setAnimation(scale);
            System.out.println("testbug13: " + temp[i]);
            graph_count[i].setText("" + Math.round(temp[i]));
        }
        graph_total = (TextView) findViewById(R.id.graph_total);
        graph_total.setText("" + Math.round(sum));
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private long backKeyPressedTime = 0;
    @Override
    public void onBackPressed() {
        Toast toast=new Toast(this);
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 지도가 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            this.finish();
            toast.cancel();
        }
    }

}
