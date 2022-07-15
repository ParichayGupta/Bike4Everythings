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
package com.hvantage.b4eemp.tracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.b4elibrary.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hvantage.b4eemp.MainApplication;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.model.BookingData;
import com.hvantage.b4eemp.model.BookingModel;
import com.hvantage.b4eemp.model.googleAddress.GoogleAddress;
import com.hvantage.b4eemp.tracking.model.*;
import com.hvantage.b4eemp.utils.AppConstants;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import okhttp3.*;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import org.json.JSONObject;
import retrofit2.Retrofit;

import java.io.IOException;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hvantage.b4eemp.tracking.model.Event.*;

public class MainFragment extends Fragment implements OnMapReadyCallback {

    public static final int REQUEST_DEVICE = 1;
    public static final int RESULT_SUCCESS = 1;

    private GoogleMap map;

    private Handler handler = new Handler();
    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<Long, Device> devices = new HashMap<>();
    private Map<Long, Position> positions = new HashMap<>();
    private Map<Long, Marker> markers = new HashMap<>();

    long deviceId;

    private WebSocketCall webSocket;

    MapView mapView;
    private SlidingUpPanelLayout mLayout;


    ImageView powerSupplyImg, powerImg, ignitionImg, gpsImg;
    private TextView tvAddress, tvOdometer, tvSpeed, tvMotion, tvLDistance, tvTDistance;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //getMapAsync(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);


        tvAddress = view.findViewById(R.id.address);
        tvOdometer = view.findViewById(R.id.odometer);
        tvSpeed = view.findViewById(R.id.speed);
        tvMotion = view.findViewById(R.id.motion);
        tvLDistance = view.findViewById(R.id.lDistance);
        tvTDistance = view.findViewById(R.id.tDistance);

        powerImg = view.findViewById(R.id.powerImg);
        powerSupplyImg = view.findViewById(R.id.powerSupplyImg);
        ignitionImg = view.findViewById(R.id.ignitionImg);
        gpsImg = view.findViewById(R.id.gpsImg);

        mLayout = view.findViewById(R.id.sliding_layout);
        mLayout.setAnchorPoint(0.5f);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("SlidingUpPanelLayout", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("SlidingUpPanelLayout", "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        powerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int engine = (int) positions.get(deviceId).getAttributes().get(KEY_ENGINE);
                engineStartStop(engine);
            }
        });


        return view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_track, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_devices:
//                startActivityForResult(new Intent(getContext(), DevicesActivity.class), REQUEST_DEVICE);
//                return true;
//            case R.id.action_logout:
//                PreferenceManager.getDefaultSharedPreferences(getContext())
//                        .edit().putBoolean(MainApplication.PREFERENCE_AUTHENTICATED, false).apply();
//                ((MainApplication) getActivity().getApplication()).removeService();
//                getActivity().finish();
//                startActivity(new Intent(getContext(), LoginActivity.class));
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DEVICE && resultCode == RESULT_SUCCESS) {
            long deviceId = data.getLongExtra(DevicesFragment.EXTRA_DEVICE_ID, 0);
            Position position = positions.get(deviceId);
            if (position != null) {
                map.moveCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(position.getLatitude(), position.getLongitude())));
                markers.get(deviceId).showInfoWindow();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.view_info, null);
                ((TextView) view.findViewById(R.id.title)).setText(marker.getTitle());
                ((TextView) view.findViewById(R.id.details)).setText(marker.getSnippet());
                return view;
            }
        });

        createWebSocket();
    }

    private String formatDetails(Position position) {
        final MainApplication application = (MainApplication) getContext().getApplicationContext();
        final User user = application.getUser();

        SimpleDateFormat dateFormat;
        if(user.getTwelveHourFormat()) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        } else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        String speedUnit = getString(R.string.user_kn);
        double factor = 1;
        if (user.getSpeedUnit() != null) {
            switch (user.getSpeedUnit()) {
                case "kmh":
                    speedUnit = getString(R.string.user_kmh);
                    factor = 1.852;
                    break;
                case "mph":
                    speedUnit = getString(R.string.user_mph);
                    factor = 1.15078;
                    break;
                default:
                    speedUnit = getString(R.string.user_kn);
                    factor = 1;
                    break;
            }
        }
        double speed = position.getSpeed() * factor;

        updateView(position, speed);

        return new StringBuilder()
                .append(getString(R.string.position_time)).append(": ")
                .append(dateFormat.format(position.getFixTime())).append('\n')
                .append(getString(R.string.position_latitude)).append(": ")
                .append(String.format("%.5f", position.getLatitude())).append('\n')
                .append(getString(R.string.position_longitude)).append(": ")
                .append(String.format("%.5f", position.getLongitude())).append('\n')
                .append(getString(R.string.position_altitude)).append(": ")
                .append(String.format("%.1f", position.getAltitude())).append('\n')
                .append(getString(R.string.position_speed)).append(": ")
                .append(String.format("%.1f", speed)).append(' ')
                .append(speedUnit).append('\n')
                .append(getString(R.string.position_course)).append(": ")
                .append(String.format("%.1f", position.getCourse()))
                .toString();
    }

    private void handleMessage(String message) throws IOException {

        Update update = objectMapper.readValue(message, Update.class);

        if (update != null && update.positions != null) {
            for (Position position : update.positions) {
                deviceId = position.getDeviceId();
                if (devices.containsKey(deviceId)) {
                    if (devices.get(deviceId).getName().equalsIgnoreCase(getArguments().getString("device_name"))) {
                        LatLng location = new LatLng(position.getLatitude(), position.getLongitude());
                        Marker marker = markers.get(deviceId);
                        if (marker == null) {
                            marker = map.addMarker(new MarkerOptions()
                                    .title(devices.get(deviceId).getName()).position(location));
                            markers.put(deviceId, marker);
                        } else {
                            marker.setPosition(location);
                        }
                        marker.setSnippet(formatDetails(position));
                        map.moveCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(position.getLatitude(), position.getLongitude())));
                        positions.put(deviceId, position);

                    }
                }
            }
        }


//        if (update != null && update.positions != null) {
//            for (Position position : update.positions) {
//                long deviceId = position.getDeviceId();
//                if (devices.containsKey(deviceId)) {
//                    LatLng location = new LatLng(position.getLatitude(), position.getLongitude());
//                    Marker marker = markers.get(deviceId);
//                    if (marker == null) {
//                        marker = map.addMarker(new MarkerOptions()
//                                .title(devices.get(deviceId).getName()).position(location));
//                        markers.put(deviceId, marker);
//                    } else {
//                        marker.setPosition(location);
//                    }
//                    marker.setSnippet(formatDetails(position));
//                    positions.put(deviceId, position);
//                }
//            }
//        }
    }

    private void updateView(Position position, double speed) {

        try {
            boolean charge = (boolean) position.getAttributes().get(KEY_CHARGE);
            powerSupplyImg.setImageResource(charge ? R.drawable.ic_power_gray_24dp : R.drawable.ic_power_green_24dp);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        try {
            boolean ignition = (boolean) position.getAttributes().get(KEY_IGNITION);
            ignitionImg.setImageResource(ignition ? R.drawable.ic_key_green_24dp : R.drawable.ic_key_gray_24dp);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        try {
            boolean gps = position.getValid();
            gpsImg.setImageResource(gps ? R.drawable.ic_gps_green_24dp : R.drawable.ic_gps_gray_24dp);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        try {
            int odometer = (int) position.getAttributes().get(KEY_ODOMETER);
            tvOdometer.setText(odometer+"");
        }catch (NullPointerException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }


        tvSpeed.setText(String.format("%.1f", speed) + " km/h");
        tvLDistance.setText(String.format("%.2f", position.getAttributes().get(KEY_DISTANCE)) + " km");
        tvTDistance.setText(String.format("%.2f", position.getAttributes().get(KEY_TOTAL_DISTANCE)) + " km");
        tvMotion.setText(((boolean)position.getAttributes().get(KEY_MOTION)) ? "Running" : "Stop");

        powerImg.setTag(speed);
        try {
            int engine = (int) position.getAttributes().get(KEY_ENGINE);
            if(engine == 0){
                powerImg.setImageResource(R.drawable.ic_engine_on);
            }else {
                powerImg.setImageResource(R.drawable.ic_engine_off);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            powerImg.setImageResource(R.drawable.ic_engine_off);
        }catch (Exception e){
            e.printStackTrace();
        }
        tvAddress.setText(position.getAddress()+"");
        //new GetAddress(position.getLatitude()+","+position.getLongitude()).execute();

    }

    private class GetAddress extends AsyncTask<String, String, String> {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?key="+AppConstants.GOOGLE_API_KEY;

        public GetAddress(String latlng) {
            url = url+"&latlng="+latlng;
        }

        @Override
        protected String doInBackground(String... strings) {
            //Log.e("Request_Response", url+ "\n");

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();
               // Log.e("Request_Response", result.toString());

                return result;

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!TextUtils.isEmpty(s)){
                GoogleAddress  googleAddress = new Gson().fromJson(s, GoogleAddress.class);
                Logger.log("getAddress", new Gson().toJson(googleAddress));
                if(googleAddress.getStatus().equalsIgnoreCase("OK")) {
                    tvAddress.setText(googleAddress.getResults().get(0).getFormatted_address());
                }
            }
        }
    }

    private void engineStartStop(final int engine){
        final double speed  = (double) powerImg.getTag();
        String message = "Current speed is : "+ speed + " km/h ";
        new AlertDialog.Builder(getActivity())
                .setTitle("Engine Start/Stop")
                .setMessage(message)
                .setPositiveButton(engine == 1 ? "Start" : "Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(speed < 30){

                            Command command = new Command();
                            command.setDescription("test");
                            command.setDeviceId(deviceId);
                            command.setType(engine == 1 ? Command.TYPE_ENGINE_RESUME : Command.TYPE_ENGINE_STOP);

                            final MainApplication application = (MainApplication) getActivity().getApplication();
                            final WebService service = application.getService();
                            service.sendCommand(command).enqueue(new WebServiceCallback<Command>(getContext()) {
                                @Override
                                public void onSuccess(retrofit2.Response<Command> response) {
                                    Toast.makeText(getContext(), R.string.command_sent, Toast.LENGTH_LONG).show();
                                    powerImg.setImageResource(engine == 1 ? R.drawable.ic_engine_on : R.drawable.ic_engine_off);
                                }

                                @Override
                                public void onResponse(retrofit2.Call<Command> call, retrofit2.Response<Command> response) {
                                    super.onResponse(call, response);
                                    Logger.log("sendCommand", call.request().url().toString()+"\n"+response.isSuccessful());
                                }

                                @Override
                                public void onFailure(retrofit2.Call<Command> call, Throwable t) {
                                    super.onFailure(call, t);
                                    t.printStackTrace();
                                    Logger.log("sendCommand", call.request().url().toString()+"\n"+t.getMessage());
                                }
                            });
                        }else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Engine Start/Stop")
                                    .setMessage("Not allow to stop engine more than speed 10 km/h")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        }
                    }
                })
                .setNegativeButton("cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (webSocket != null) {
            webSocket.cancel();
        }
    }

    private void reconnectWebSocket() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    createWebSocket();
                }
            }
        });
    }

    private void createWebSocket() {
        final MainApplication application = (MainApplication) getActivity().getApplication();
        application.getServiceAsync(new MainApplication.GetServiceCallback() {
            @Override
            public void onServiceReady(final OkHttpClient client, final Retrofit retrofit, WebService service) {
                User user = application.getUser();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(user.getLatitude(), user.getLongitude()), user.getZoom()));
                service.getDevices().enqueue(new WebServiceCallback<List<Device>>(getContext()) {
                    @Override
                    public void onSuccess(retrofit2.Response<List<Device>> response) {
                        for (Device device : response.body()) {
                            if (device != null) {
                                devices.put(device.getId(), device);
                            }
                        }

                        Request request = new Request.Builder().url(retrofit.baseUrl().url().toString() + "api/socket").build();
                        webSocket = WebSocketCall.create(client, request);
                        webSocket.enqueue(new WebSocketListener() {
                            @Override
                            public void onOpen(WebSocket webSocket, Response response) {
                            }

                            @Override
                            public void onFailure(IOException e, Response response) {
                                reconnectWebSocket();
                            }

                            @Override
                            public void onMessage(ResponseBody message) throws IOException {
                                final String data = message.string();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                        Logger.log("webSocket", data);
                                            handleMessage(data);
                                        } catch (IOException e) {
                                            Log.w(MainFragment.class.getSimpleName(), e);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onPong(okio.Buffer payload) {

                            }


                            @Override
                            public void onClose(int code, String reason) {
                                reconnectWebSocket();
                            }
                        });
                    }
                });
            }

            @Override
            public boolean onFailure() {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
