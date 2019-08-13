package com.shifamily.dev.weather.domain;

import com.shifamily.dev.weather.Utils;
import com.shifamily.dev.weather.exception.StationException;
import lombok.Data;

import java.util.Date;


@Data
public class StationData {

    private int barometer;
    private int insideTemperature;
    private int outsideTemperature;
    private int insideHumidity;
    private int outsideHumidity;
    private int windSpeed;
    private int windDirection;
    private int rainRate;
    private Date date;
    private long timeStamp;

    private int twoBytesToInt(byte[] data, int offset){
        int high = ((int)data[offset + 1] & 0xff) * 256;
        int low = (int)data[offset] & 0xff;
        return high + low  ;
    }

    private int getCrc(byte[] data, int offset){
        int high = ((int)data[offset] & 0xff) * 256;
        int low = (int)data[offset + 1] & 0xff;
        return high + low  ;
    }

    public StationData(byte[] data) throws StationException {


        if (data[0] != 1)
            throw new StationException("LOOP package doesn't contain header");
        insideTemperature = twoBytesToInt(data, 1);
        outsideTemperature = twoBytesToInt(data, 3);
        windSpeed = data[5];
        windDirection =  twoBytesToInt(data, 6);
        barometer = twoBytesToInt(data, 8);
        insideHumidity = data[10];
        outsideHumidity = data[11];
        rainRate = twoBytesToInt(data, 12);

        int crc = getCrc(data, 16);
        int crcCal = Utils.crc16(data, 1, 15);

        if (crc != crcCal)
            throw new StationException("CRC16 error on LOOP package");

        date = new Date();
        timeStamp = System.currentTimeMillis();
    }
}
