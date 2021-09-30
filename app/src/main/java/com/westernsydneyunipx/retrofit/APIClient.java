package com.westernsydneyunipx.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.westernsydneyunipx.voqual.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author PA1810.
 */
public class APIClient {

    private static Retrofit retrofit = null;

    public static final String BASE_URL = "http://voqualapp.com/api/";
    public static final String MEDIA_URL = "http://voqualapp.com/uploads/";

    public static Retrofit getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        httpClient.connectTimeout(2, TimeUnit.MINUTES);
        httpClient.readTimeout(2, TimeUnit.MINUTES);
        httpClient.writeTimeout(2, TimeUnit.MINUTES);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(/*BuildConfig.BASE_URL*/BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
