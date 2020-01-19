package com.challengers.area_analysis;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Jin on 2016-10-26.
 */
public class DBService extends AsyncTask<String, Void, String[][]>{
    public static final String QUERY_POPULATION="Query_population";
    public static final String QUERY_SUBWAYPOP="Query_subwaypop";
    public static final String QUERY_RESTAURANT="Query_restaurant_info";
    public static final String QUERY_FAIL_RATIO="Query_fail_ratio";
    public static final String QUERY_RENT_PRICE="Query_rent_price";
    public static final String QUERY_RESTAURANT_COUNT="Query_restaurant_count";

    String[][] queryResult = null;        //쿼리 결과가 들어갈 버퍼

    @Override
    protected String[][] doInBackground(String... params) {
        String uri=null;
        String data=null;
        try {

            if (params[0].equals(QUERY_POPULATION))
            {
                data="rq_dong="+URLEncoder.encode(params[1],"UTF-8");
                uri = "http://52.78.123.217/query_population.php?"+data;
            }
            else if (params[0].equals(QUERY_SUBWAYPOP))
                uri = "http://52.78.123.217/query_subwaypop.php";
            else if (params[0].equals(QUERY_RESTAURANT))
            {
                data="rq_dong="+URLEncoder.encode(params[1], "UTF-8")+"&rq_type="+URLEncoder.encode(params[2], "UTF-8");
                uri = "http://52.78.123.217/query_restaurant_info.php?"+data;
            }
            else if (params[0].equals(QUERY_FAIL_RATIO))
            {
                data="rq_dong="+URLEncoder.encode(params[1], "UTF-8")+"&rq_type="+URLEncoder.encode(params[2], "UTF-8");
                uri = "http://52.78.123.217/query_fail_ratio.php?"+data;
            }
            else if (params[0].equals(QUERY_RENT_PRICE)) {
                data="rq_dong="+URLEncoder.encode(params[1],"UTF-8");
                uri = "http://52.78.123.217/query_rent_price.php?"+data;
            }
            else if (params[0].equals(QUERY_RESTAURANT_COUNT)) {
                data="rq_dong="+URLEncoder.encode(params[1], "UTF-8")+"&rq_type="+URLEncoder.encode(params[2], "UTF-8");
                uri = "http://52.78.123.217/query_restaurant_count_info.php?"+data;
            }

        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        OutputStreamWriter wr=null;
        BufferedReader bufferedReader=null;
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();   //연결시도
            StringBuilder sb = new StringBuilder();

            if(con!=null) {

                con.setConnectTimeout(7000);    //connection time out 7초 설정
                con.setUseCaches(false);
                con.setDoOutput(true);
                //if(con.getResponseCode()==HttpURLConnection.HTTP_OK) {
                wr = new OutputStreamWriter(con.getOutputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                //if(params[0].equals(QUERY_POPULATION))
                    //wr.write(params[1]);
                //wr.flush();       //flush랑 close해주면 에러 나는데, 왜 나는지 모르겠습니다ㅠ

                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n"); //json형태로 얻은 결과값을 String builder에 추가
                    //System.out.println("testbug96: "+sb.toString().trim());
                }

                if(params[0].equals(QUERY_POPULATION))
                    getPopulation(sb.toString().trim());
                else if(params[0].equals(QUERY_SUBWAYPOP))
                    getSubwaypop(sb.toString().trim());
                else if(params[0].equals(QUERY_RESTAURANT))
                    getRestaurant(sb.toString().trim());
                else if(params[0].equals(QUERY_FAIL_RATIO))
                    getFailRtio(sb.toString().trim());
                else if(params[0].equals(QUERY_RENT_PRICE))
                    getRentPrice(sb.toString().trim());
                else if(params[0].equals(QUERY_RESTAURANT_COUNT))
                    getRestaurantCount(sb.toString().trim());

                con.disconnect();
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try {

                bufferedReader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return queryResult;
    }

    @Override
    protected void onPostExecute(String[][] result){
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        //TODO 작업이 취소된후에 호출된다.
        super.onCancelled();
    }




    void getPopulation(final String result){
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray rows = jsonObj.getJSONArray("result");
            if(rows.length()>0)
                queryResult=new String[rows.length()][18];

            for(int i=0;i<rows.length();i++){
                JSONObject c = rows.getJSONObject(i);
                queryResult[i][0] = c.getString("SPOT_CD");
                queryResult[i][1] = c.getString("addr");
                queryResult[i][2] = ""+c.getInt("t7");
                queryResult[i][3] = c.getString("t8");
                queryResult[i][4] = c.getString("t9");
                queryResult[i][5] = c.getString("t10");
                queryResult[i][6] = c.getString("t11");
                queryResult[i][7] = c.getString("t12");
                queryResult[i][8] = c.getString("t13");
                queryResult[i][9] = c.getString("t14");
                queryResult[i][10] = c.getString("t15");
                queryResult[i][11] = c.getString("t16");
                queryResult[i][12] = c.getString("t17");
                queryResult[i][13] = c.getString("t18");
                queryResult[i][14] = c.getString("t19");
                queryResult[i][15] = c.getString("t20");
                queryResult[i][16]=c.getString("lat");
                queryResult[i][17]=c.getString("lng");

                //HashMap<String,String> persons = new HashMap<String,String>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getSubwaypop(final String result){
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray rows = jsonObj.getJSONArray("result");
            queryResult=new String[rows.length()][5];

            for(int i=0;i<rows.length();i++){
                JSONObject c = rows.getJSONObject(i);
                queryResult[i][0] = c.getString("SUB_STA_NM");
                queryResult[i][1] = c.getString("ride");
                queryResult[i][2] = c.getString("alight");
                queryResult[i][3] = c.getString("XPOINT");
                queryResult[i][4] = c.getString("YPOINT");

                //HashMap<String,String> persons = new HashMap<String,String>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getRestaurant(final String result){
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray rows = jsonObj.getJSONArray("result");
            if(rows.length()>0)
                queryResult=new String[rows.length()][6];

            System.out.println("testbug88: "+rows.length());
            for(int i=0;i<rows.length();i++){
                JSONObject c = rows.getJSONObject(i);
                queryResult[i][0] = c.getString("name");
                queryResult[i][1] = c.getString("addr");
                queryResult[i][2] = c.getString("launch_date");
                queryResult[i][3] = c.getString("type");
                queryResult[i][4] = c.getString("lat");
                queryResult[i][5] = c.getString("lng");

                //HashMap<String,String> persons = new HashMap<String,String>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getFailRtio(final String result){
        try {
            JSONObject jsonObj = new JSONObject(result);

            JSONArray rows = jsonObj.getJSONArray("result");
            if(rows.length()>0)
                queryResult=new String[rows.length()][1];

            for(int i=0;i<rows.length();i++){
                JSONObject c = rows.getJSONObject(i);
                queryResult[i][0] = c.getString("fail_ratio");

                //HashMap<String,String> persons = new HashMap<String,String>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getRentPrice(final String result){
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray rows = jsonObj.getJSONArray("result");
            if(rows.length()>0)
                queryResult=new String[rows.length()][1];

            for(int i=0;i<rows.length();i++){
                JSONObject c = rows.getJSONObject(i);
                queryResult[i][0] = c.getString("avg_unitPrice");
                //HashMap<String,String> persons = new HashMap<String,String>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getRestaurantCount(final String result){
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray rows = jsonObj.getJSONArray("result");
            if(rows.length()>0)
                queryResult=new String[rows.length()][1];

            for(int i=0;i<rows.length();i++){
                JSONObject c = rows.getJSONObject(i);
                queryResult[i][0] = c.getString("count");
                //HashMap<String,String> persons = new HashMap<String,String>();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

