package com.github.berrycontrol.drivers.dmpa6;

import com.github.berrycontrol.driver.api.BerryHubDeviceCommand;
import com.github.berrycontrol.drivers.dmpa6.api.DmpA6ApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

public abstract class DmpA6DeviceCommand extends BerryHubDeviceCommand {
    public enum Command {
        POWER_TOGGLE,
        INPUT_INTERNAL_PLAYER,
        INPUT_BLUETOOTH,
        INPUT_USB_C,
        INPUT_OPTICAL,
        INPUT_COAX,
        OUTPUT_BAL_XLR,
        OUTPUT_ANALOG_RCA,
        OUTPUT_XLR_RCA,
        OUTPUT_HDMI,
        OUTPUT_SPDIF,
        OUTPUT_USB_DAC,
        PLAY_PAUSE,
        SKIP_LAST,
        SKIP_NEXT,
        VOLUME_UP,
        VOLUME_DOWN,
        TOGGLE_VU,
        TOGGLE_DISPLAY
    }

    private final RestTemplate restTemplate = new RestTemplate();
    private byte[] icon = null;

    public DmpA6DeviceCommand(int id, String title) {
        super(id, title);
    }

    public byte[] getIcon() {
        return readIcon(getIconResourcePath());
    }

    public abstract String getIconResourcePath();

    public abstract void execute(String deviceId);

    protected void execute(String command, String deviceId) {
        try {
            String url = String.format("http://%s:%d/%s", deviceId, DmpA6ApiClient.PORT, command);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readIcon(String resourcePath) {
        if (icon != null) {
            return icon;
        }

        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            int length = is.available();
            byte[] data = new byte[length];

            is.read(data, 0, length);

            icon = data;
            return icon;
        } catch (Exception x) {
            return null;
        }
    }
}
