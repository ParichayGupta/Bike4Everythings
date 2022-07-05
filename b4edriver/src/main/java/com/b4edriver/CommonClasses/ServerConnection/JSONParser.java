package com.b4edriver.CommonClasses.ServerConnection;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.b4edriver.AppController;
import com.b4edriver.Citrus.PaytmEnvironment;
import com.b4edriver.CommonClasses.Services.FusedLocationService;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class JSONParser {

    final DialogManagerDriver myDialogManager = new DialogManagerDriver();
    Context cx;

    // constructor
    public JSONParser(Context cx) {
        this.cx = cx;

        if (FusedLocationService.mGoogleApiClient == null) {
            //new FusedLocationService(cx);
            FusedLocationService locationService =  FusedLocationService.getInstance(cx);
            locationService.googleClientReConnect();
        } else {
            if (!FusedLocationService.mGoogleApiClient.isConnected() || FusedLocationService.mGoogleApiClient.isConnecting()) {
                FusedLocationService.mGoogleApiClient.connect();
            }
        }
    }

/*

    Handler listenForNetworkAvailability = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 1) { // code if not connected
                Log.i("NetworkCheck", "not connected");

                if (retryConnectionNumber <= CONNECTION_RETRY_MAX) {
                    Log.i("NetworkCheck", "checking for connectivity");
                    //Here you could disable & re-enable your WIFI/3G connection before retry

                    // Start the ping process again with delay
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isNetworkAvailable(listenForNetworkAvailability, REQUEST_TIMEOUT);
                        }
                    }, 5000);
                    retryConnectionNumber++;
                } else {
                    Log.i("NetworkCheck", "failed to establish an connection");
                    // code if not connected
                }
            } else {
                Log.i("NetworkCheck", "connected");
                retryConnectionNumber = 0;
                // code if connected

            }
        }
    };
*/

    public void parseVollyJSONObject(final String url, final int method, final JSONObject jsonObject, final String diologTxt, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {

        Logger.log("Request ", url + "\n" + jsonObject.toString());

        myDialogManager.showProcessDialog(cx, diologTxt, false);

        if (!Function.isConnectingToInternet(cx)) {
            try {
                new SweetAlertDialog(cx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(cx.getString(R.string.error_check_internet_connection))
                        .setConfirmText("Close")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            } catch (RuntimeException e) {
            }
            myDialogManager.stopProcessDialog();
        } else if (method == 0 || method == 1) {
            requestData(url, method, jsonObject, diologTxt, successCallBack, errorCallBack);
        } else {
            Logger.log("", "Invalid Request Method");
        }
    }

    public void requestData(final String url, int method, final JSONObject jsonObject, String diologTxt, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {
        final String VOLLYSTATUS = "vollyStatus";
        final String VOLLYMSG = "vollymsg";
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (method, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        AppController.getInstance(cx).removeEequest();
                        Logger.log("Response ", url + "\n" + jsonObject.toString() + "\n" + response.toString());
                        if (response != null) {
                            try {
                                response.put(VOLLYSTATUS, "200");
                                response.put(VOLLYMSG, "Success");
                                successCallBack.success(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Logger.log("", "Something went wrong.!>>");
                            try {
                                response.put(VOLLYSTATUS, "300");
                                response.put(VOLLYMSG, "Something went wrong.!");
                                errorCallBack.error(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            myDialogManager.stopProcessDialog();
                        } catch (IllegalArgumentException e) {
                        }
                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        myDialogManager.stopProcessDialog();
                        AppController.getInstance(cx).removeEequest();
                        String err = (error.getMessage() == null) ? "Data Send Fail" : error.getMessage() + "nj error";

                        Logger.log("Response: ", "Something went wrong.!" + err + ">>" + error.toString());
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("status", "100");
                            jsonObject.put(VOLLYSTATUS, "400");
                            jsonObject.put("error", err);
                            jsonObject.put(VOLLYMSG, cx.getString(R.string.error_retry_after_sometime));
                            errorCallBack.error(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-type", "application/json; charset=UTF-8");
                return headers;
            }


        };
       /* jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        AppController.getInstance(cx).addToRequestQueue(jsObjRequest);
    }

    public void parseVollyJSONObject(final String url, final int method, final JSONObject jsonObject, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {

        Logger.log("Request ", url + "\n" + jsonObject.toString());

        if (!Function.isConnectingToInternet(cx)) {
            try {
                new SweetAlertDialog(cx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(cx.getString(R.string.error_check_internet_connection))
                        .setConfirmText("Close")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            } catch (RuntimeException e) {
            }
        } else if (method == 0 || method == 1) {
            requestData(url, method, jsonObject, successCallBack, errorCallBack);
        } else {
            Logger.log("", "Invalid Request Method");
        }
    }

    public void requestData(final String url, int method, final JSONObject jsonObject, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {
        final String VOLLYSTATUS = "vollyStatus";
        final String VOLLYMSG = "vollymsg";
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (method, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        AppController.getInstance(cx).removeEequest();
                        Logger.log("Response ", url + "\n" + jsonObject.toString() + "\n" + response.toString());
                        if (response != null) {
                            try {
                                response.put(VOLLYSTATUS, "200");
                                response.put(VOLLYMSG, "Success");
                                successCallBack.success(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Logger.log("", "Something went wrong.!>>");
                            try {
                                response.put(VOLLYSTATUS, "300");
                                response.put(VOLLYMSG, "Something went wrong.!");
                                errorCallBack.error(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppController.getInstance(cx).removeEequest();
                        String err = (error.getMessage() == null) ? "Data Send Fail" : error.getMessage() + "nj error";

                        Logger.log("Response: ", "Something went wrong.!" + err + ">>" + error.toString());
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("status", "100");
                            jsonObject.put(VOLLYSTATUS, "400");
                            jsonObject.put(VOLLYMSG, cx.getString(R.string.error_retry_after_sometime));
                            errorCallBack.error(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-type", "application/json; charset=UTF-8");
                return headers;
            }


        };
       /* jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        AppController.getInstance(cx).addToRequestQueue(jsObjRequest);
    }

    /*.....................*/

    public void parseVollyObject(final String url, final int method, final JSONObject jsonObject, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {

        Logger.log("Request ", url + "\n" + jsonObject.toString());

        if (!Function.isConnectingToInternet(cx)) {
            try {
                new SweetAlertDialog(cx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(cx.getString(R.string.error_check_internet_connection))
                        .setConfirmText("Close")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            } catch (RuntimeException e) {
            }

        } else if (method == 0 || method == 1) {
            requestDataObject(url, method, jsonObject, successCallBack, errorCallBack);
        } else {
            Logger.log("", "Invalid Request Method");
        }
    }

    public void requestDataObject(final String url, int method, final JSONObject jsonObject, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {
        final String VOLLYSTATUS = "vollyStatus";
        final String VOLLYMSG = "vollymsg";
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (method, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        AppController.getInstance(cx).removeEequest();
                        Logger.log("Response ", url + "\n" + jsonObject.toString() + "\n" + response.toString());
                        if (response != null) {
                            try {
                                response.put(VOLLYSTATUS, "200");
                                response.put(VOLLYMSG, "Success");
                                successCallBack.success(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Logger.log("", "Something went wrong.!>>");
                            try {
                                response.put(VOLLYSTATUS, "300");
                                response.put(VOLLYMSG, "Something went wrong.!  nj error");
                                errorCallBack.error(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppController.getInstance(cx).removeEequest();
                        String err = (error.getMessage() == null) ? "Data Send Fail" : error.getMessage() + "nj error";

                        Logger.log("Response: ", "Something went wrong.!" + err);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("status", "100");
                            jsonObject.put(VOLLYSTATUS, "400");
                            jsonObject.put(VOLLYMSG, cx.getString(R.string.error_retry_after_sometime));
                            errorCallBack.error(jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Encoding", "UTF-8");
                headers.put("Content-type", "application/json; charset=UTF-8");
                return headers;
            }
        };
       /* jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        AppController.getInstance(cx).addToRequestQueue(jsObjRequest);
    }

    /*.....................*/

    public void parseVollyForLog(final String url, final int method, final JSONObject jsonObject, VolleyCallBack successCallBack) {

        Logger.log("Request ", url + "\n" + jsonObject.toString());

        Intent intent2 = new Intent("Netindecator");

        if (!Function.isConnectingToInternet(cx)) {
            try {
                intent2.putExtra("status", "300");
                intent2.putExtra("msg", "Check your Internet connection, Your location is not getting updated.");
                LocalBroadcastManager.getInstance(cx).sendBroadcast(intent2);
               /* new SweetAlertDialog(cx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(cx.getString(R.string.error_check_internet_connection))
                        .setConfirmText("Close")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();*/
            } catch (RuntimeException e) {
            }

        } else if (method == 0 || method == 1) {

            requestLogData(url, method, jsonObject, intent2, successCallBack);

        } else {
            Logger.log("", "Invalid Request Method");
        }
    }

    public void requestLogData(final String url, int method, final JSONObject jsonObject, final Intent intent2, final VolleyCallBack successCallBack) {

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (method, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        AppController.getInstance(cx).removeEequest();
                        Logger.log("Response ", url + "\n" + jsonObject.toString() + "\n" + response.toString());
                        if (response != null) {
                            intent2.putExtra("status", "200");
                            intent2.putExtra("msg", "Your internet is working");
                            LocalBroadcastManager.getInstance(cx).sendBroadcast(intent2);
                        } else {
                            intent2.putExtra("status", "300");
                            intent2.putExtra("msg", "Check your Internet connection, Your location is not getting updated.");
                            //  LocalBroadcastManager.getInstance(cx).sendBroadcast(intent2);
                        }

                        successCallBack.success(response.toString());

                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppController.getInstance(cx).removeEequest();
                        String err = (error.getMessage() == null) ? "Data Send Fail" : error.getMessage() + "nj error";

                        Logger.log("Response: ", "Something went wrong.!TRIPLOG" + err);
                        intent2.putExtra("status", "300");
                        intent2.putExtra("msg", "Your location is not updating on server (internet issues).");
                        // LocalBroadcastManager.getInstance(cx).sendBroadcast(intent2);
                        successCallBack.success(err.toString());
                    }


                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Encoding", "UTF-8");
                headers.put("Content-type", "application/json; charset=UTF-8");
                return headers;
            }

        };


        AppController.getInstance(cx).addToRequestQueue(jsObjRequest);

    }

    public void parseVollyJSONArray(String url, int method, final JSONArray jsonArray, String diologTxt, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {
        final String VOLLYSTATUS = "vollyStatus";
        final String VOLLYMSG = "vollymsg";
        Logger.log("Request ", url + "\n" + jsonArray.toString());
        if (method == 0 || method == 1) {
            final JsonArrayRequest jsObjRequest = new JsonArrayRequest
                    (method, url, jsonArray, new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            Logger.log("Response ", response.toString());
                            if (response != null) {

                                successCallBack.success(response.toString());


                            } else {
                                Logger.log("", "Something went wrong.!>>");

                                errorCallBack.error(response.toString());

                            }
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String err = (error.getMessage() == null) ? "Data Send Fail" : error.getMessage();
                            Logger.log("Response: ", "Something went wrong.!" + err);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("status", "100");
                                jsonObject.put(VOLLYSTATUS, "400");
                                jsonObject.put(VOLLYMSG, "Server not response");
                                errorCallBack.error(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

          /*  jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
            AppController.getInstance(cx).addToRequestQueue(jsObjRequest);
        } else {
            Logger.log("", "Invalid Request Method");
        }
    }


    public void getGoogleAddress(String url, final VolleyCallBack successCallBack, final ServerErrorCallBack serverErrorCallBack) {
        Logger.log("addressURL", url);

        //final DialogManagerDriver myDialogManager = new DialogManagerDriver();
        // myDialogManager.showProcessDialog(cx,"",false);
        final JSONObject object = new JSONObject();
        if (!Function.isConnectingToInternet(cx)) {

            try {
                object.put("status", "100");
                object.put("msg", cx.getString(R.string.error_check_internet_connection));
                serverErrorCallBack.error(object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // myDialogManager.stopProcessDialog();
        } else {
            final StringRequest jsObjRequest = new StringRequest
                    (url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            AppController.getInstance(cx).removeEequest();
                            successCallBack.success(response);
                            //          myDialogManager.stopProcessDialog();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppController.getInstance(cx).removeEequest();
                            try {
                                object.put("status", "200");
                                object.put("msg", error.getMessage());
                                serverErrorCallBack.error(object.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // serverErrorCallBack.error(error.getMessage());
                            //            myDialogManager.stopProcessDialog();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
         /*   jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
            //jsObjRequest.setShouldCache(true);
            // Adding request to request queue
            AppController.getInstance(cx).addToRequestQueue(jsObjRequest);

        }


    }


    public void parseVollyGet(String url, final VolleyCallBack successCallBack, final ServerErrorCallBack serverErrorCallBack) {
        Logger.log("Request", url);
        final StringRequest jsObjRequest = new StringRequest
                (url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        successCallBack.success(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverErrorCallBack.error(error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

       // jsObjRequest.setShouldCache(true);
      /*  jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        // Adding request to request queue
        AppController.getInstance(cx).addToRequestQueue(jsObjRequest);

    }

    public void parseJSONObjectwithoutProgress(final String url, final int method, final JSONObject jsonObject, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {

        Logger.log("Request ", url + "\n" + jsonObject.toString());

        if (!Function.isConnectingToInternet(cx)) {

        } else if (method == 0 || method == 1) {
            final String VOLLYSTATUS = "vollyStatus";
            final String VOLLYMSG = "vollymsg";
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (method, url, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            AppController.getInstance(cx).removeEequest();
                            Logger.log("Response ", url + "\n" + jsonObject.toString() + "\n" + response.toString());
                            if (response != null) {
                                try {
                                    response.put(VOLLYSTATUS, "200");
                                    response.put(VOLLYMSG, "Success");
                                    successCallBack.success(response.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Logger.log("", "Something went wrong.!>>");
                                try {
                                    response.put(VOLLYSTATUS, "300");
                                    response.put(VOLLYMSG, "Something went wrong.!  nj error");
                                    errorCallBack.error(response.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {

                            } catch (IllegalArgumentException e) {
                            }
                        }


                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppController.getInstance(cx).removeEequest();
                            String err = (error.getMessage() == null) ? "Data Send Fail" : error.getMessage() + "nj error";

                            Logger.log("Response: ", "Something went wrong.!" + err + ">>");
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("status", "100");
                                jsonObject.put(VOLLYSTATUS, "400");
                                jsonObject.put(VOLLYMSG, cx.getString(R.string.error_retry_after_sometime));
                                errorCallBack.error(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Encoding", "UTF-8");
                    headers.put("Accept", "application/json");
                    headers.put("Content-type", "application/json; charset=UTF-8");
                    return headers;
                }


            };

          /*  jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
            AppController.getInstance(cx).addToRequestQueue(jsObjRequest);
        } else {
            Logger.log("", "Invalid Request Method");
        }
    }


    public void parseJSONObjectforPaytm(final String url, final int method, final JSONObject jsonObject, final VolleyCallBack successCallBack, final ServerErrorCallBack errorCallBack) {

        Logger.log("Request ", url + "\n" + jsonObject.toString());

        if (!Function.isConnectingToInternet(cx)) {

        } else if (method == 0 || method == 1) {
            final String VOLLYSTATUS = "vollyStatus";
            final String VOLLYMSG = "vollymsg";
            final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (method, url, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            AppController.getInstance(cx).removeEequest();
                            Logger.log("Response ", url + "\n" + jsonObject.toString() + "\n" + response.toString());
                            if (response != null) {
                                try {
                                    response.put(VOLLYSTATUS, "200");
                                    response.put(VOLLYMSG, "Success");
                                    successCallBack.success(response.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Logger.log("", "Something went wrong.!>>");
                                try {
                                    response.put(VOLLYSTATUS, "300");
                                    response.put(VOLLYMSG, "Something went wrong.!  nj error");
                                    errorCallBack.error(response.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {

                            } catch (IllegalArgumentException e) {
                            }
                        }


                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AppController.getInstance(cx).removeEequest();
                            String err = (error.getMessage() == null) ? "Data Send Fail" : error.getMessage() + "nj error";

                            Logger.log("Response: ", "Something went wrong.!" + err + ">>");
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("status", "100");
                                jsonObject.put(VOLLYSTATUS, "400");
                                jsonObject.put(VOLLYMSG, cx.getString(R.string.error_retry_after_sometime));
                                errorCallBack.error(jsonObject.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }


                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Encoding", "UTF-8");
                    headers.put("Accept", "application/json");
                    headers.put("Content-type", "application/json; charset=UTF-8");
                    //headers.put("authorization", "Basic bWVyY2hhbnQtbmJhLXRlY2gtc3RhZ2luZzoxMjUwZTg4NS0xNGY1LTQyODAtOWViYS02YjhkZWE4ZGQ3ZTk=");

                    headers.put("authorization", PaytmEnvironment.LIVE.getAuth());
                    return headers;
                }


            };
           /* jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
*/
            AppController.getInstance(cx).addToRequestQueue(jsObjRequest);
        } else {
            Logger.log("", "Invalid Request Method");
        }
    }


    public void parsePaytmValidateToken(String url, final String token, final VolleyCallBack successCallBack, final ServerErrorCallBack serverErrorCallBack) {
        Logger.log("Request", url);
        final StringRequest jsObjRequest = new StringRequest
                (url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        successCallBack.success(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        serverErrorCallBack.error(error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("session_token", token);
                return headers;
            }
        };

       // jsObjRequest.setShouldCache(true);
        // Adding request to request queue
     /*   jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
        AppController.getInstance(cx).addToRequestQueue(jsObjRequest);

    }

    public void requestBackGroundData(final String url, int method, final JSONObject jsonObject) {

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (method, url, jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        AppController.getInstance(cx).removeEequest();
                        Logger.log("Response ", url + "\n" + jsonObject.toString() + "\n" + response.toString());

                    }


                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppController.getInstance(cx).removeEequest();
                        String err = (error.getMessage() == null) ? "Data Send Fail" : error.getMessage() + "nj error";
                        Logger.log("Response: ", "Something went wrong.!" + err + ">>");

                    }


                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Encoding", "UTF-8");
                headers.put("Accept", "application/json");
                headers.put("Content-type", "application/json; charset=UTF-8");
                return headers;
            }


        };
    /*    jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/

        AppController.getInstance(cx).addToRequestQueue(jsObjRequest);
    }
}
