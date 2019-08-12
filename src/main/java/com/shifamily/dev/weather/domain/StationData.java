package com.shifamily.dev.weather.domain;

import com.shifamily.dev.weather.Utils;
import com.shifamily.dev.weather.exception.StationException;
import lombok.Data;

import java.util.Date;

@Data
public class StationData {

    private char barTrend;
    private char packetType;
    private int barometer;
    private int insideTemperature;
    private int outsideTemperature;
    private char[] extraTemperature = new char[7];
    private char[] soilTemperature = new char[4];
    private char[] leafTemperature = new char[4];
    private char[] extraHumidity = new char[7];
    private int insideHumidity;
    private int outsideHumidity;
    private int windSpeed;
    private int avg10minWindSpeed;
    private int windDirection;
    private int rainRate;
    private int uv;
    private int solarRadiation;
    private int stormRain;
    private Date startDateOfCurrentStorm;
    private int dayRain, monthRain, yearRain;
    private int dayET, monthET, yearET;
    private char[]  soilMoistures = new char[4];
    private char[]  leafMoistures = new char[4];
    private int insideAlarms;
    private int rainAlarms;
    private char[] outsideAlarms= new char[2];
    private char[] extraTempHumAlarms = new char[8];
    private char[] extraSoilLeafAlarms = new char[8];
    private int transmitterBatteryStatus;
    private double consoleBatteryVoltage;
    private int forecastIcons;
    private int forecastRuleNumber;
    private int timeOfSunrise;
    private int timeOfSunset;

    private int twoBytesToInt(char[] data, int offset){
        return ((int)data[offset] & 0xff) * 256 +  (int)data[offset + 1] & 0xff;
    }

    private Date parseDate(char[] data, int offset){
        return null;
    }

    public StationData(char[] data) throws StationException {

        String header = String.copyValueOf(data, 0, 3);
        if (!header.equals("LOO"))
            throw new StationException("LOOP package doesn't contain header");

        barTrend = data[3];
        packetType = data[4];
        barometer = twoBytesToInt(data, 7);
        insideTemperature = twoBytesToInt(data, 9);
        insideHumidity = data[11];
        outsideTemperature = twoBytesToInt(data, 12);
        windSpeed = data[14];
        avg10minWindSpeed = data[15];
        windDirection =  twoBytesToInt(data, 16);
        System.arraycopy(data, 18, extraTemperature, 0, 7);
        System.arraycopy(data, 25, soilTemperature, 0, 4);
        System.arraycopy(data, 29, leafTemperature, 0, 4);
        outsideHumidity = data[33];
        System.arraycopy(data, 34, extraHumidity, 0, 7);

        rainRate = twoBytesToInt(data, 41);
        uv = data[43];
        solarRadiation = twoBytesToInt(data, 44);
        stormRain = twoBytesToInt(data, 46);
        startDateOfCurrentStorm = parseDate(data, 48);
        dayRain = twoBytesToInt(data, 50);
        monthRain = twoBytesToInt(data, 52);
        yearRain = twoBytesToInt(data, 54);

        dayET = twoBytesToInt(data, 56);
        monthET = twoBytesToInt(data, 58);
        yearET = twoBytesToInt(data, 60);
        System.arraycopy(data, 62, soilMoistures, 0, 4);
        System.arraycopy(data, 66, leafMoistures, 0, 4);
        insideAlarms = data[70];
        rainAlarms = data[71];
        System.arraycopy(data, 72, outsideAlarms, 0, 2);
        System.arraycopy(data, 74, extraTempHumAlarms, 0, 8);
        System.arraycopy(data, 82, extraSoilLeafAlarms, 0, 4);
        transmitterBatteryStatus = data[86];
        consoleBatteryVoltage = (((double) data[83] * 300)/512)/100.0;
        forecastIcons = data[89];
        forecastRuleNumber = data[90];
        timeOfSunrise = data[91];
        timeOfSunset = data[92];

        int crc = twoBytesToInt(data, 97);
        int crcCal = Utils.crc16(data, 0, 95);


    }
}
