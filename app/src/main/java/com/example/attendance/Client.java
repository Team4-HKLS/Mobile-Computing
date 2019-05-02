package com.example.attendance;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class Client {

    private static RestCall restCall;
    private static final String serverUrl = "http://52.79.226.66:8080";
    private static OkHttpClient okHttpClient;
    private static OkHttpClient.Builder builder;

    public static RestCall getClient(){
        builder = new OkHttpClient.Builder();
        okHttpClient = builder.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        if(restCall == null){
            Retrofit retrofit = new Retrofit.Builder().baseUrl(serverUrl)
                    .client(okHttpClient)
                    .build();

            restCall = retrofit.create(RestCall.class);
        }

        return restCall;
    }

    public interface RestCall {
        @POST("register_device")
        Call<ResponseBody> register(@Header("deviceMac") String deviceMac,
                                    @Header("classId") String classId);

        @GET("get_plan")
        Call<ResponseBody> getPlan(@Header("deviceMac") String deviceMac);
    }
}
