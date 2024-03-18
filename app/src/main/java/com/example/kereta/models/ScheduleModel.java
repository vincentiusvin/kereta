package com.example.kereta.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.processing.Generated;

@Generated("jsonschema2pojo")
public class ScheduleModel {
    @SerializedName("train_id")
    @Expose
    private String trainId;
    @SerializedName("ka_name")
    @Expose
    private String kaName;
    @SerializedName("route_name")
    @Expose
    private String routeName;
    @SerializedName("dest")
    @Expose
    private String dest;
    @SerializedName("time_est")
    @Expose
    private String timeEst;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("dest_time")
    @Expose
    private String destTime;

    private String station;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getKaName() {
        return kaName;
    }

    public void setKaName(String kaName) {
        this.kaName = kaName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getTimeEst() {
        return timeEst;
    }

    public void setTimeEst(String timeEst) {
        this.timeEst = timeEst;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDestTime() {
        return destTime;
    }

    public void setDestTime(String destTime) {
        this.destTime = destTime;
    }
}
