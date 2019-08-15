package com.shifamily.dev.weather.controller;

import com.shifamily.dev.weather.domain.StationData;
import com.shifamily.dev.weather.domain.StationInfo;
import com.shifamily.dev.weather.exception.StationException;
import com.shifamily.dev.weather.service.StationLink;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Station {


    private final StationLink stationLink;

    public Station(StationLink stationLink) {
        this.stationLink = stationLink;
    }

    @GetMapping("/v1/station/info")
    public StationInfo checkStationInfo() throws StationException {

        return stationLink.getStationInfo();

    }
    @GetMapping("/v1/ping")
    public StationInfo ping() throws StationException {
        return stationLink.ping();
    }
    @GetMapping("/v1/station/data")
    public StationData liveData() throws StationException {
        return stationLink.getStationDataLive();
    }

}
