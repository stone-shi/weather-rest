package com.shifamily.dev.weather.service;

import com.shifamily.dev.weather.config.VantageLinkProperties;
import com.shifamily.dev.weather.domain.StationData;
import com.shifamily.dev.weather.domain.StationInfo;
import com.shifamily.dev.weather.exception.StationException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;

@Service
@Slf4j
public class VantageLinkIP implements StationLink {

    private static final char ACK = 6;
    private static final char NO_ACK = 33;

    @Autowired
    private VantageLinkProperties vantageLinkProperties;

    private Socket socket = null;

    @Synchronized
    private void connect() throws IOException {
        String address = vantageLinkProperties.getIp();
        int port = vantageLinkProperties.getPort();
        socket = new Socket(address, port);

    }

    @Synchronized
    private void disconnect() throws IOException {
        socket.close();
        socket = null;
    }

    @Synchronized
    private char[] sendCommand(char[] cmd, int outLenExpected) throws IOException, StationException {
        BufferedReader input ;
        PrintWriter out ;

        input  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out    = new PrintWriter(socket.getOutputStream(), true);
        out.write(cmd);
        out.flush();
        char[] ack = new char[1];
        int rd = input.read(ack);
        if (rd != 1 || ack[0] == NO_ACK)
            throw new StationException("CMD is not a valid command");

        if (ack[0] != ACK)
            throw new StationException("Unknown protocol detected, first response is not ACK (0x06)");

        char[] buf = new char[outLenExpected];
        int ct = 0;
        while (ct < outLenExpected){
            rd = input.read(buf, ct, outLenExpected - ct);
            if (rd <= 0)
                break;
            ct += rd;
        }

        char[] res = new char[ct];
        System.arraycopy(buf, 0, res, 0, ct);
        return res;

    }

    @Override
    public StationInfo ping() throws StationException {
        return getStationInfo();
    }

    @Override
    public StationData getStationDataLive()  throws StationException {
        try {
            connect();
            char[] cmd = {'L', 'O', 'O', 'P', ' ', '1', 13};
            char[] rs = sendCommand(cmd, 99);
            disconnect();
            return new StationData(rs);

        } catch (IOException e) {
            log.error("IO Error while getStationDataLive", e);
            throw new StationException("IO Error while getStationDataLive()", e);

        }
    }

    @Override
    public StationInfo getStationInfo() throws StationException {
        try {
            connect();
            char[] cmd = {'W', 'R', 'D', 18, 77, 13};
            char[] rs = sendCommand(cmd, 1);
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
