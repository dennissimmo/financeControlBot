package com.denchik.demo.service;

import lombok.extern.log4j.Log4j2;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Log4j2
public class PingSchedule { public String getUrl() {
    return url;
}

    public void setUrl(String url) {
        this.url = url;
    }

    @Value("${pingSchedule.url}")
    private String url;

    @Scheduled(fixedRateString = "${pingSchedule.period}")
    public void pingMe() {
        try {
            URL url = new URL(getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            log.info("Ping {}, OK: response code {}", url.getHost(), connection.getResponseCode());
            connection.disconnect();
        } catch (IOException e) {
            log.error("Ping FAILED");
            e.printStackTrace();
        }

    }
}
