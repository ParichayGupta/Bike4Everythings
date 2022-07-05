package com.b4edriver.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MAX on 30-Nov-16.
 */
public class GoogleAddress {

    public List<GoogleAddress> results;

    @SerializedName("formatted_address")
    public String address;

}
