package com.example.attendance;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
                                    @Header("name") String name,
                                    @Header("classId") String classId);

        @GET("get_plan")
        Call<ResponseBody> getPlan(@Header("deviceMac") String deviceMac);

        @Multipart
        @POST("send_data")
        Call<ResponseBody> sendData(@Part("description") RequestBody description,
                                    @Header("deviceMac") String deviceMac,
                                    @Part List<MultipartBody.Part> file);
    }
}
