package com.example.stageOneTask.response;

import lombok.Data;

@Data
public class IpInfo {
    private String ip;
    private String city;
    private String region;
    private String country;
    private String loc;
    private String org;
    private String timezone;
}
