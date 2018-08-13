package com.workstation.places;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ee208820 on 8/30/2017.
 */

public class ServiceOrFuelStationModel {
    private LatLng latLng;
    private String name;
    private String address;
    private String stationid;
    private Float rating;
    private String icon;

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getStationid() {
        return stationid;
    }

    public void setStationid(String stationid) {
        this.stationid = stationid;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
