package com.github.berrycontrol.drivers.dmpa6;

import com.github.berrycontrol.driver.api.BerryHubDeviceInfo;

public class DmpA6DeviceInfo implements BerryHubDeviceInfo {
    private final String name;
    private final String deviceId;

    public DmpA6DeviceInfo(String name, String deviceId) {
        this.name = name;
        this.deviceId = deviceId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDeviceId() {
        return this.deviceId;
    }
}
