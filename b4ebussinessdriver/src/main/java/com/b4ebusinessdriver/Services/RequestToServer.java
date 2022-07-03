package com.b4ebusinessdriver.Services;

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

public class RequestToServer  {

    public static RequestToServer getInstance(){
        return new RequestToServer();
    }

    public RequestToServer(){

    }

    public void send(JSONObject jsonObject, String url, CallBack callBack){
        new Send(jsonObject, url, callBack).execute();
    }

    private class Send extends AsyncTask<Void, Void, String>{
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject;
        String url;
        CallBack callBack;

        public Send(JSONObject jsonObject, String url, CallBack callBack){
            this.jsonObject = jsonObject;
            this.url = url;
            this.callBack = callBack;
        }
        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.e("Request_Response",  result.toString() +"\n"+jsonObject.toString() + "\n" );

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
        }
    }



    public interface CallBack{
        void success(String json);
    }
}
