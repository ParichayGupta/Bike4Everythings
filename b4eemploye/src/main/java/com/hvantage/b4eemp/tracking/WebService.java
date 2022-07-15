/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
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

import com.hvantage.b4eemp.model.googleAddress.GoogleAddress;
import com.hvantage.b4eemp.tracking.model.Command;
import com.hvantage.b4eemp.tracking.model.CommandType;
import com.hvantage.b4eemp.tracking.model.Device;
import com.hvantage.b4eemp.tracking.model.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface WebService {

    @FormUrlEncoded
    @POST("/api/session")
    Call<User> addSession(@Field("email") String email, @Field("password") String password);

    @GET("/api/session")
    Call<User> addSession(@Query("token") String token);

    @GET("/api/devices")
    Call<List<Device>> getDevices();

    @GET("/api/commandtypes")
    Call<List<CommandType>> getCommandTypes(@Query("deviceId") long deviceId);

    @Headers("authorization:Basic YWRtaW46YWRtaW4=")
    @POST("/api/commands/send")
    Call<Command> sendCommand(@Body Command command);

}
