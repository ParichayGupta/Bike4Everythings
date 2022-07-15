/*
 * Copyright 2015 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hvantage.b4eemp;

import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.b4elibrary.Logger;
import com.hvantage.b4eemp.tracking.WebService;
import com.hvantage.b4eemp.tracking.WebServiceCallback;
import com.hvantage.b4eemp.tracking.model.User;
import com.hvantage.b4eemp.utils.AppConstants;
import com.hvantage.b4eemp.utils.AppPreferance;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainApplication extends MultiDexApplication {

    public static final String PREFERENCE_AUTHENTICATED = "authenticated";



    public interface GetServiceCallback {
        void onServiceReady(OkHttpClient client, Retrofit retrofit, WebService service);
        boolean onFailure();
    }


    private OkHttpClient client;
    private WebService service;
    private Retrofit retrofit;
    private User user;

    private final List<GetServiceCallback> callbacks = new LinkedList<>();

    public void getServiceAsync(GetServiceCallback callback) {
        if (service != null) {
            callback.onServiceReady(client, retrofit, service);
        } else {
            if (callbacks.isEmpty()) {
                initService();
            }
            callbacks.add(callback);
        }
    }

    public WebService getService() { return service; }

    public User getUser() { return user; }

    public void removeService() {
        service = null;
        user = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void initService() {
        final String url = AppConstants.TRACKING_SERVER;


        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .cookieJar(new JavaNetCookieJar(cookieManager)).build();

        try {
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(url)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            for (GetServiceCallback callback : callbacks) {
                callback.onFailure();
            }
            callbacks.clear();
        }

        final WebService service = retrofit.create(WebService.class);

        String token = AppPreferance.getToken(this);

        //token = "3VHRPVcubVteE8WJheTl1da99QcOrf3o";

        service.addSession(token).enqueue(new WebServiceCallback<User>(this) {
            @Override
            public void onSuccess(Response<User> response) {
                MainApplication.this.service = service;
                MainApplication.this.user = response.body();
                for (GetServiceCallback callback : callbacks) {
                    callback.onServiceReady(client, retrofit, service);
                }
                callbacks.clear();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Logger.log("onFailure", call.request().url().toString());
                boolean handled = false;
                for (GetServiceCallback callback : callbacks) {
                    handled = callback.onFailure();
                }
                callbacks.clear();
                if (!handled) {
                    super.onFailure(call, t);
                }
            }
        });
    }

}
