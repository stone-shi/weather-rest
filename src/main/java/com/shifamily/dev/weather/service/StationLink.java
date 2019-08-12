package com.shifamily.dev.weather.service;

import com.shifamily.dev.weather.domain.StationData;
import com.shifamily.dev.weather.domain.StationInfo;
import com.shifamily.dev.weather.exception.StationException;

public interface StationLink {

    StationInfo getStationInfo()  throws StationException;
    StationInfo ping() throws StationException ;
    StationData getStationDataLive()  throws StationException;

}
