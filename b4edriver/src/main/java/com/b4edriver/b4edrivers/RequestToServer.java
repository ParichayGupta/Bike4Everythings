package com.b4edriver.b4edrivers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by manishsingh on 27/03/18.
 */

public class RequestToServer {

    CustomProgressBar progressBar = new CustomProgressBar();

    public RequestToServer() {

    }

    public static RequestToServer getInstance() {
        return new RequestToServer();
    }

    public void send(Context context, JSONObject jsonObject, String url, CallBack callBack) {
        progressBar.show(context);
        new Send(context, jsonObject, url, callBack).execute();
    }

    public interface CallBack {
        void success(String json);
    }

    private class Send extends AsyncTask<Void, Void, String> {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;
        String url;
        CallBack callBack;
        Context context;


        public Send(Context context, JSONObject jsonObject, String url, CallBack callBack) {

            this.jsonObject = jsonObject;
            this.url = url;
            this.callBack = callBack;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            Log.e("Request_Response", url + "\n" + jsonObject.toString() + "\n");
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("Request_Response", result.toString());

                return result;

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            callBack.success(s);
            progressBar.getDialog().dismiss();
        }
    }
}
