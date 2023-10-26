package com.onenet.dto;

public class DeviceParams {
    String imei;
    String version;

    @Override
    public String toString() {
        return "DeviceParams{" +
                "imei='" + imei + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
