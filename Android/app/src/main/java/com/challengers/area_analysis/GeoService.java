package com.challengers.area_analysis;


import android.content.Intent;
import android.os.AsyncTask;


import org.jsoup.Jsoup;
import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.net.URLEncoder;

/**
 * Created by Jin on 2016-10-30.
 */
public class GeoService extends AsyncTask<String, Void, String[]> {
    String[] geoResult=null;
    @Override
    protected String[] doInBackground(String... params) {

        try {
            String urlEncoding= URLEncoder.encode(params[0].split("\\(")[0].trim(), "UTF-8");
            Document doc = Jsoup.connect("http://maps.googleapis.com/maps/api/geocode/xml?address=" + urlEncoding + "&sensor=false").get();
            Elements select_lat = doc.select("location lat");
            Elements select_lng = doc.select("location lng");
            select_lat.get(0).text();	//에러를 걸러내기 위한 선언

            geoResult= new String[5];
            String rq_addr=params[0].split("\\(")[0].trim();     //요청할 주소정보만 파싱
            String rq_dong=params[0].split("\\(")[1].substring(0, 2).trim();	//형정구역의 '동'
            String rq_type=params[1];		//선택한 업종
            String rq_lat=select_lat.get(0).text();     //위도
            String rq_lng=select_lng.get(0).text();     //경도
            System.out.println("testbug 90: "+rq_addr+",  "+rq_dong);
            geoResult[0]=rq_addr;
            geoResult[1]=rq_dong;
            geoResult[2]=rq_type;
            geoResult[3]=rq_lat;
            geoResult[4]=rq_lng;

        } catch (IOException e) {
            System.out.println("기기가 네트워크에 연결되지 않았습니다.");
        }catch(java.lang.IndexOutOfBoundsException e){
            e.printStackTrace();
            System.out.println("존재하지 않는 주소정보 입니다.");
        }
        return geoResult;
    }

    @Override
    protected void onPostExecute(String[] v){
        super.onPostExecute(v);
    }
}
