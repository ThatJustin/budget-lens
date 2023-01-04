package com.codenode.budgetlens.utils;

import android.os.Build;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;



//

public class HttpUtils {

    public static final String TAG = "HttpUtils";
    private static AsyncHttpClient client = new AsyncHttpClient(); // visualize the object

//    private static String imei;

    static {




        try {
            String versionCode = Build.VERSION.RELEASE; // The version of the device's system
//            String versionCode = String.valueOf(SystemUtil.getVersionCode(CarZoneApplication.getAppContext()));
            client.addHeader("version", versionCode);// system version

            client.addHeader("platform", "Android");
        } catch (Exception e) {
//            LogUtils.e(TAG, "Problem with getting the system version！");
            e.printStackTrace();
        }
        client.setTimeout(41000); // Set the maximum time period，if don't set it，then it equals to 10s

        //HTTPS
        SSLSocketFactory sf = SSLSocketFactory.getSocketFactory();
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // Allow the verification for all devices

        //SSLSocketFactory sf = MySSLSocketFactory.getSocketFactory();
        //sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // Allow the verification for all devices
        client.setSSLSocketFactory(sf);

    }


    public static void get(String token,String urlString, AsyncHttpResponseHandler res) //Obtain a string by entering the url
    {

        client.addHeader("Content-type", "text/html;charset=UTF-8");
        client.addHeader("Authorization", token);
        client.get(urlString, res);
    }

    /**
     * submit a post request
     *
     * @param urlString port address
     * @param params
     * @param res
     */
    public static void post(String token, String urlString, RequestParams params, AsyncHttpResponseHandler res) {
        Log.e("sdadsadsds", "post: "+token );
        client.addHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        client.addHeader("Authorization", token);
        params.put("token", token);
        client.post(urlString, params, res);
    }
    public static void posts(String token, String urlString, RequestParams params, AsyncHttpResponseHandler res) {
        Log.e("sdadsadsds", "post: "+urlString );
        Log.e("sdadsadsds", "params: "+params.toString() );
        client.addHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        client.addHeader("Authorization",""+ token);
         params.put("token", token);
        client.post(urlString, params, res);
    }


}
