package com.hvantage.b4eemp.ApiData;

import com.hvantage.b4eemp.model.BookingData;
import com.hvantage.b4eemp.model.BookingModel;
import com.hvantage.b4eemp.utils.AppConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DataService {

    @FormUrlEncoded
    @POST("/")
    Call<BookingModel> getAllotedBooking(@Field("method") String method, @Field("location_id") String location_id);
}
