package com.github.berrycontrol.drivers.dmpa6;

import com.github.berrycontrol.driver.api.BerryHubDeviceDriverDescriptor;
import com.github.berrycontrol.driver.api.BerryHubDeviceDriverException;
import com.github.berrycontrol.driver.api.StartPairingResult;
import com.github.berrycontrol.drivers.dmpa6.api.DmpA6ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DmpA6DeviceDriverDescriptor
    extends BerryHubDeviceDriverDescriptor<DmpA6DeviceDriver, DmpA6DeviceInfo, DmpA6DeviceCommand>
    implements ServiceListener {

    private final static Logger logger = LoggerFactory.getLogger(DmpA6DeviceDriverDescriptor.class);

    public final static UUID DRIVER_ID = UUID.fromString("3b89776a-3550-4210-8f5d-ce136b73bae0");
    private final static String DISPLAY_NAME = "Eversolo DMP-A6";
    private final static String DESCRIPTION = "Driver for controlling Eversolo DMP-A6 devices";

    private final JmDNS jmdns;
    private List<DmpA6DeviceInfo> devices = new ArrayList<>();

    public DmpA6DeviceDriverDescriptor() {
        super(DRIVER_ID, DISPLAY_NAME, DESCRIPTION);

        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            jmdns.addServiceListener("_adb._tcp.local.", this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DmpA6DeviceInfo> getDevices() throws BerryHubDeviceDriverException {
        return devices;
    }

    @Override
    public AuthenticationMethod authenticationMethod() {
        return AuthenticationMethod.NONE;
    }

    @Override
    public boolean requiresPairing() {
        return false;
    }

    @Override
    public StartPairingResult startPairing(DmpA6DeviceInfo deviceInfo, String remoteName) throws BerryHubDeviceDriverException {
        return StartPairingResult.create(UUID.randomUUID().toString(), false);
    }

    @Override
    public boolean finalizePairing(String pairingRequest, String pin, boolean deviceProvidesPin) throws BerryHubDeviceDriverException {
        return true;
    }

    @Override
    public DmpA6DeviceDriver createDriverInstance(String deviceId) {
        return new DmpA6DeviceDriver(deviceId);
    }

    @Override
    public void serviceAdded(ServiceEvent serviceEvent) {

    }

    @Override
    public void serviceRemoved(ServiceEvent serviceEvent) {
        List<String> addresses = Arrays.asList(serviceEvent.getInfo().getHostAddresses());

        devices.removeAll(
            devices
                .stream()
                .filter(dev -> addresses.stream().anyMatch(a -> dev.getDeviceId().equals(a)))
                .toList());
    }

    @Override
    public void serviceResolved(ServiceEvent serviceEvent) {
        logger.info("Device resolved: {}", serviceEvent.getName());

        if (serviceEvent.getInfo().getHostAddresses().length > 0) {
            String address = serviceEvent.getInfo().getHostAddresses()[0];

            if (devices.stream().noneMatch(dev -> dev.getDeviceId().equals(address))) {
                DmpA6ApiClient client = new DmpA6ApiClient(address);

                try {
                    Map<String, Object> deviceInfo = client.connect(this.getClass().getSimpleName(), DRIVER_ID);
                    if (deviceInfo != null) {
                        DmpA6DeviceInfo devInfo = new DmpA6DeviceInfo(
                            (String) ((Map<String, Object>) deviceInfo.get("device")).get("name"),
                            (String) deviceInfo.get("ip"));

                        logger.info("DMP-A6 device found: {}", devInfo.getName());
                        devices.add(devInfo);
                    }
                } catch (Exception ex) {
                    logger.error("Device seems not to be a DMP-A6", ex);
                }
            }
        }
    }
}
