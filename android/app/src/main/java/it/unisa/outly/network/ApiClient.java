package it.unisa.outly.network;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

public final class ApiClient {
    // TODO: Configure this URL for the target backend; keep the local emulator URL for development.
    public static final String BASE_URL = "http://10.0.2.2:8080/";

    private static ApiService apiService;
    private static SessionCookieJar cookieJar;

    private ApiClient() {
    }

    public static synchronized ApiService getApiService(Context context) {
        if (apiService == null) {
            cookieJar = new SessionCookieJar(context);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public static synchronized SessionCookieJar getCookieJar(Context context) {
        getApiService(context);
        return cookieJar;
    }
}
