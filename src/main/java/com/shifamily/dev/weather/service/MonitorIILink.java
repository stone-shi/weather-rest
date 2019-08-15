package com.shifamily.dev.weather.service;

import com.shifamily.dev.weather.config.VantageLinkProperties;
import com.shifamily.dev.weather.domain.StationData;
import com.shifamily.dev.weather.domain.StationInfo;
import com.shifamily.dev.weather.exception.StationException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;

@Service
@Slf4j
public class MonitorIILink implements StationLink  {

    @Value("${cachetimems:1000}")
    private int cacheTime;

    private final VantageLinkProperties vantageLinkProperties;
    private Socket socket = null;

    private static final char ACK = 6;
    private static final char NO_ACK = 33;

    private StationData cachedStationData = null;


    private DataInputStream  input ;
    private DataOutputStream out ;


    public MonitorIILink(VantageLinkProperties vantageLinkProperties) {
        log.info("Monitor II Link with configure: {} ", vantageLinkProperties );
        this.vantageLinkProperties = vantageLinkProperties;
    }

    private void connect() throws IOException {
        log.info("Connecting to {} ...",  vantageLinkProperties.getIp());
        String address = vantageLinkProperties.getIp();
        int port = vantageLinkProperties.getPort();
        socket = new Socket(address, port);
        input  = new DataInputStream(socket.getInputStream());
        out    = new DataOutputStream(socket.getOutputStream());
        log.info("Connected");
    }

    private void disconnect() throws IOException {
        input.close();
        out.close();
        socket.close();
        socket = null;
        log.info("Disconnected");
    }

    private byte[] sendCommand(byte[] cmd, int outLenExpected) throws IOException, StationException {
        log.debug("sendCommand() : {}", Hex.encodeHexString(cmd));
        out.write(cmd);
        out.flush();
        int ack = input.read();
        if (ack == NO_ACK) {
            log.warn("Invalid command {} detected. Station return NO_ACK",  Hex.encodeHexString(cmd));
            throw new StationException("CMD is not a valid command");
        }

        if (ack != ACK) {
            log.warn("Invalid response [{}] detected. Station return neither NO_ACK nor ACK", ack);
            throw new StationException("Unknown protocol detected, first response is not ACK (0x06)");
        }

        byte[] buf = new byte[outLenExpected];
        int ct = 0;
        while (ct < outLenExpected){
            int rd = input.read(buf, ct, outLenExpected - ct);
            if (rd <= 0)
                break;
            ct += rd;
        }
        byte[] res = new byte[ct];
        System.arraycopy(buf, 0, res, 0, ct);
        log.debug("Got response {} bytes : [{}]", ct, Hex.encodeHexString(buf));
        return res;

    }

    @Override
    @Synchronized
    public StationInfo ping() throws StationException {
        log.info("Ping command");
        return getStationInfo();
    }

    @Override
    @Synchronized
    public StationData getStationDataLive()  throws StationException {
        log.info("Get LOOP data command");
        if (cachedStationData != null && cacheTime > 0
                && System.currentTimeMillis() -  cachedStationData.getTimeStamp() < cacheTime )
            return cachedStationData;


        try {
            connect();
            byte[] cmd = {'L', 'O', 'O', 'P',(byte)0xff, (byte)0xff, 13};
            byte[] rs = sendCommand(cmd, 18);
            disconnect();
            cachedStationData = new  StationData(rs);
            return cachedStationData;

        } catch (IOException e) {
            log.error("IO Error while getStationDataLive", e);
            throw new StationException("IO Error while getStationDataLive()", e);

        }
    }

    @Override
    @Synchronized
    public StationInfo getStationInfo() throws StationException {
        log.info("Get station info command");
        try {
            connect();
            byte[] cmd = {'W', 'R', 'D', 18, 77, 13};
            byte[] rs = sendCommand(cmd, 1);
            disconnect();

            if (rs[0] != 16 && rs[0] > 6 )
                throw new StationException("Unknown protocol detected");

            String[] mode = {"Wizard III", "Wizard II", "Monitor", "Perception", "GroWeather", "Energy EnviroMontor",
                    "Health Enviromonitor", "Vantage Pro, Vantage Pro 2"};

            return StationInfo.builder().model(mode[rs[0]]).pingOK(true).build();

        } catch (IOException e) {
            log.error("IO Error while getStationInfo()", e);
            throw new StationException("IO Error while getStationInfo()");
        }
    }

}
