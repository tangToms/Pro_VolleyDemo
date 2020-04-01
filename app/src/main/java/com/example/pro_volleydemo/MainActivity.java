package com.example.pro_volleydemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import androidx.annotation.Nullable;

public class MainActivity extends Activity {
    //请求队列
    private RequestQueue mRequestQueue;
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.l_main);
        //image
        imageView=findViewById(R.id.iv_image);
        //请求队列
        mRequestQueue= Volley.newRequestQueue(this);
        //get请求
        Button btn_get=findViewById(R.id.btn_get);
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("VolleyDemo","button click");
                getRequest();
            }
        });
        //加载图片
        Button btn_image=findViewById(R.id.btn_image);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("VolleyDemo","button click");
                //iamgeRequest方式
                //imageRequest();
                //imageLoader方式
                imageLoader();
            }
        });

        handleSSLHandshake();
    }
    //get请求
    public void getRequest(){
        //url地址
        String urlStr="https://home.firefoxchina.cn/";
        Response.Listener<String> listener=new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.i("VolleyDemo","Response:"+response);
            }
        };
        Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("VolleyDemo","request failed:"+error.getMessage());
            }
        };
        StringRequest request=new StringRequest(urlStr,listener,errorListener);
        //将stringRequest添加到请求队列
        mRequestQueue.add(request);
    }
    //post请求
    public void postRequest(){
        //url地址
        String urlStr="";
        Response.Listener<String> listener=new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.i("VolleyDemo","Response:"+response);
            }
        };
        Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("VolleyDemo","request failed:"+error.getMessage());
            }
        };
        //请求方式StringRequest.Method.XXX
        StringRequest request=new StringRequest(StringRequest.Method.POST,
                urlStr,listener,errorListener){
            //重写getParams方法，post请求参数
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("name","tang");
                hashMap.put("password","123456");
                return hashMap;
            }
        };
        //将stringRequest添加到请求队列
        mRequestQueue.add(request);
    }

    //加载图片
    public  void imageRequest(){
        //url地址
        String urlStr="https://tupian.sioe.cn/uploadfile/200911/27/1721200501.jpg";
        Response.Listener<Bitmap> listener=new Response.Listener<Bitmap>(){
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }
        };
        Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("VolleyDemo","request failed:"+error.getMessage());
            }
        };
        //请求方式StringRequest.Method.XXX
        ImageRequest request=new ImageRequest(urlStr,listener,
                0,0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ARGB_8888,
                errorListener);
        //将stringRequest添加到请求队列
        mRequestQueue.add(request);
    }

    //加载图片方式2
    public void imageLoader(){
        //url地址
        String urlStr="https://tupian.sioe.cn/uploadfile/200911/27/1721200501.jpg";
        //创建IamgeCache对象，可以缓存图片
        ImageLoader.ImageCache imageCache=new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                Log.i("VolleyDemo","url:"+url);
                return null;
            }
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                Log.i("VolleyDemo","url:"+url+";bitmap:"+bitmap.toString());
            }
        };
        //创建ImageLoader对象
        ImageLoader loader=new ImageLoader(mRequestQueue,imageCache);
        //设置ImageListener对象，加载前默认图片，加载失败图片
        ImageLoader.ImageListener listener=ImageLoader.getImageListener(imageView,R.mipmap.ic_launcher,R.mipmap.ic_launcher_round);
        loader.get(urlStr,listener);
    }

    //允许https请求，信任所有请求
    public static void handleSSLHandshake(){
        TrustManager[] trustManagers=new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        try {
            SSLContext sslContext=SSLContext.getInstance("TLS");
            //信任所有证书
            sslContext.init(null,trustManagers,new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    //任何hostname都验证通过
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
