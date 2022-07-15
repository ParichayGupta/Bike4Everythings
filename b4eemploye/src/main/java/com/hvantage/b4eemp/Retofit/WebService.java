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
package com.hvantage.b4eemp.Retofit;

import com.google.gson.JsonObject;
import com.hvantage.b4eemp.tracking.model.Command;
import com.hvantage.b4eemp.tracking.model.CommandType;
import com.hvantage.b4eemp.tracking.model.Device;
import com.hvantage.b4eemp.tracking.model.User;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface WebService {


    @Multipart
    @POST("/administrator/user_api/Ondemand_RENTAPP/Rent_Trip_Book_Allotment_Api.php")
    Call<JsonObject> bikeIssue(@Part MultipartBody.Part userImagePart, @Part MultipartBody.Part idImagePart, @Part MultipartBody.Part videoClipPart, @Part("data") RequestBody jsonBody);

    @POST("/administrator/user_api/Ondemand_RENTAPP/Rent_Free_Bikes.php")
    Call<JsonObject> getAllFreeBikes(@Body RequestBody jsonBody);
}
