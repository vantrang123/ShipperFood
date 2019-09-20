package com.trangdv.shipperfood.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCoordinates {

    @GET("maps/api/directions/json?key=AIzaSyBW3rhW1EhjhW36DmMyoTTBup4E6Gu1LCY&sensor=true&language=en&mode=driving")
    Call<String> getDirections(@Query("origin") String origin, @Query("destination") String destination);

}
