package com.example.kereta.models;

import com.example.kereta.models.ScheduleContainerModel;
import com.example.kereta.models.ScheduleModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ScheduleInterface {
    @GET("schedule")
    Call<ScheduleContainerModel> getSchedules(
        @Query(value="stationid") String station,
        @Query(value="timefrom") String start,
        @Query(value="timeto") String end
    );
}
