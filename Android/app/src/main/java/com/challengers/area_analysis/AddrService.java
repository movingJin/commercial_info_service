package com.challengers.area_analysis;

import android.os.AsyncTask;
import android.provider.DocumentsContract;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by Jin on 2016-10-30.
 */
public class AddrService extends AsyncTask<String, Void, String[][]>{
    String[][] addrResult = null;        //우편주소, 상세주소가 들어갈 버퍼

    @Override
    protected String[][] doInBackground(String... params) {

        try {
            String urlEncoding= URLEncoder.encode(params[0], "UTF-8");
            Document doc = Jsoup.connect("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll?ServiceKey=%2BwTHU7mm%2BwmCDLUuOFJ1Aew%2B6DdtgQo5a%2BN31NlEaj%2FaeQPw1tHf0KmgTkt%2FxRwZIf0A5E%2FDcI40hvk0b8Iipw%3D%3D&countPerPage=50&currentPage=2&srchwrd="+ urlEncoding).get();
            Elements[] postal=new Elements[3];
            postal[0] = doc.select("zipNo");   //우편번호
            postal[1] = doc.select("lnmAdres");     //도로명주소
            postal[2] = doc.select("rnAdres");     //지번주소

            if (postal[1].size() > 0) addrResult = new String[postal[1].size()][3];
            for (int i = 0; i < postal[1].size(); i++) {
                addrResult[i][0] = postal[0].get(i).text().substring(0, 3) + "-" + postal[0].get(i).text().substring(3);
                addrResult[i][1] = postal[1].get(i).text();
                addrResult[i][2] = postal[2].get(i).text();
            }

        } catch (java.lang.IndexOutOfBoundsException | IOException e)
        {
            e.printStackTrace();
            //일치하는 주소 목록이 없습니다.
        }

        return addrResult;
    }

    @Override
    protected void onPostExecute(String[][] addrResult){
        super.onPostExecute(addrResult);
    }
}
