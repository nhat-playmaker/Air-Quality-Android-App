package com.rushia.aqitracker;

public class DeviceInfo {
    private int id, deviceID;
    private String devicePassword;

    public DeviceInfo(int id, int deviceID, String devicePassword) {
        this.id = id;
        this.deviceID = deviceID;
        this.devicePassword = devicePassword;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public String getDevicePassword() {
        return devicePassword;
    }

    public void setDevicePassword(String devicePassword) {
        this.devicePassword = devicePassword;
    }
}
