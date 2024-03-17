package com.github.berrycontrol.drivers.dmpa6;

import com.github.berrycontrol.driver.api.BerryHubDeviceDriver;
import com.github.berrycontrol.drivers.dmpa6.DmpA6DeviceCommand.Command;
import com.github.berrycontrol.drivers.dmpa6.api.DmpA6ApiClient;
import com.github.berrycontrol.drivers.dmpa6.commands.DmpA6GenericCommand;
import com.github.berrycontrol.drivers.dmpa6.commands.DmpA6PowerToggleCommand;

import java.util.List;
import java.util.Map;

import static com.github.berrycontrol.drivers.dmpa6.DmpA6DeviceDriverDescriptor.DRIVER_ID;

public class DmpA6DeviceDriver implements BerryHubDeviceDriver<DmpA6DeviceCommand> {
    private static final List<DmpA6DeviceCommand> COMMANDS = List.of(
        new DmpA6PowerToggleCommand(),
        // ***** INPUTS *****
        new DmpA6GenericCommand(
            Command.INPUT_INTERNAL_PLAYER,
            "Input: Internal Player",
            "images/input_int_player.png",
            "/ZidooMusicControl/v2/setInputList?tag=XMOS"),
        new DmpA6GenericCommand(
            Command.INPUT_BLUETOOTH,
            "Input: Bluetooth",
            "images/input_bt.png",
            "/ZidooMusicControl/v2/setInputList?tag=BT"),
        new DmpA6GenericCommand(
            Command.INPUT_USB_C,
            "Input: USB-C",
            "images/input_usb.png",
            "/ZidooMusicControl/v2/setInputList?tag=USB"),
        new DmpA6GenericCommand(
            Command.INPUT_OPTICAL,
            "Input: Optical",
            "images/input_optical.png",
            "/ZidooMusicControl/v2/setInputList?tag=SPDIF"),
        new DmpA6GenericCommand(
            Command.INPUT_COAX,
            "Input: Coaxial",
            "images/input_coax.png",
            "/ZidooMusicControl/v2/setInputList?tag=RCA"),
        // ***** OUTPUTS *****
        new DmpA6GenericCommand(
            Command.OUTPUT_BAL_XLR,
            "Output: Balanced XLR",
            "images/out_balxlr.png",
            "/ZidooMusicControl/v2/setOutInputList?tag=XLR"),
        new DmpA6GenericCommand(
            Command.OUTPUT_ANALOG_RCA,
            "Output: Analog RCA",
            "images/out_analog_rca.png",
            "/ZidooMusicControl/v2/setOutInputList?tag=RCA"),
        new DmpA6GenericCommand(
            Command.OUTPUT_XLR_RCA,
            "Output: Analog XLR/RCA",
            "images/out_xlr_rca.png",
            "/ZidooMusicControl/v2/setOutInputList?tag=XLRRCA"),
        new DmpA6GenericCommand(
            Command.OUTPUT_HDMI,
            "Output: HDMI",
            "images/out_hdmi.png",
            "/ZidooMusicControl/v2/setOutInputList?tag=HDMI"),
        new DmpA6GenericCommand(
            Command.OUTPUT_SPDIF,
            "Output: SPDIF",
            "images/out_spdif.png",
            "/ZidooMusicControl/v2/setOutInputList?tag=SPDIF"),
        new DmpA6GenericCommand(
            Command.OUTPUT_USB_DAC,
            "Output: USB-DAC",
            "images/out_usb_dac.png",
            "/ZidooMusicControl/v2/setOutInputList?tag=USB"),
        new DmpA6GenericCommand(
            Command.PLAY_PAUSE,
            "Play/Pause",
            "images/play_pause.png",
            "/ZidooMusicControl/v2/playOrPause"),
        new DmpA6GenericCommand(
            Command.VOLUME_DOWN,
            "Volume Down",
            "images/volume_down.png",
            "/ZidooControlCenter/RemoteControl/sendkey?key=Key.VolumeDown"),
        new DmpA6GenericCommand(
            Command.VOLUME_UP,
            "Volume UP",
            "images/volume_up.png",
            "/ZidooControlCenter/RemoteControl/sendkey?key=Key.VolumeUp"),
        new DmpA6GenericCommand(
            Command.TOGGLE_DISPLAY,
            "Toggle Display",
            "images/display_toggle.png",
            "/ZidooMusicControl/v2/setPowerOption?tag=screen"),
        new DmpA6GenericCommand(
            Command.TOGGLE_VU,
            "Toggle VU",
            "images/vu_meter.png",
            "/ZidooMusicControl/v2/changVUDisplay"),
        new DmpA6GenericCommand(
            Command.SKIP_LAST,
            "Previous",
            "images/previous.png",
            "/ZidooMusicControl/v2/playLast"),
        new DmpA6GenericCommand(
            Command.SKIP_NEXT,
            "next",
            "images/next.png",
            "/ZidooMusicControl/v2/playNext")
    );

    private static final int REMOTE_LAYOUT_HEIGHT = 6;
    private static final int REMOTE_LAYOUT_WIDTH = 6;

    private static final int[][] REMOTE_LAYOUT = new int[][]{
        {
            Command.POWER_TOGGLE.ordinal(),
            -1,
            -1,
            -1,
            Command.TOGGLE_VU.ordinal(),
            Command.TOGGLE_DISPLAY.ordinal() },
        { -1, -1, -1, -1, -1, -1 },
        {
            Command.SKIP_LAST.ordinal(),
            Command.PLAY_PAUSE.ordinal(),
            Command.SKIP_NEXT.ordinal(),
            -1,
            Command.VOLUME_DOWN.ordinal(),
            Command.VOLUME_UP.ordinal()
        },
        { -1, -1, -1, -1, -1, -1 },
        {
            -1,
            Command.INPUT_INTERNAL_PLAYER.ordinal(),
            Command.INPUT_BLUETOOTH.ordinal(),
            Command.INPUT_USB_C.ordinal(),
            Command.INPUT_OPTICAL.ordinal(),
            Command.INPUT_COAX.ordinal()
        },
        {
            Command.OUTPUT_BAL_XLR.ordinal(),
            Command.OUTPUT_ANALOG_RCA.ordinal(),
            Command.OUTPUT_XLR_RCA.ordinal(),
            Command.OUTPUT_HDMI.ordinal(),
            Command.OUTPUT_SPDIF.ordinal(),
            Command.OUTPUT_USB_DAC.ordinal()
        }
    };

    private final String address;

    public DmpA6DeviceDriver(String address) {
        this.address = address;
    }

    @Override
    public List<DmpA6DeviceCommand> getCommands() {
        return COMMANDS;
    }

    @Override
    public int getRemoteLayoutHeight() {
        return REMOTE_LAYOUT_HEIGHT;
    }

    @Override
    public int getRemoteLayoutWidth() {
        return REMOTE_LAYOUT_WIDTH;
    }

    @Override
    public int[][] getRemoteLayout() {
        return REMOTE_LAYOUT;
    }

    @Override
    public void execute(DmpA6DeviceCommand command) {
        command.execute(this.address);
    }

    @Override
    public boolean isDeviceReady() {
        boolean result;

        try {
            DmpA6ApiClient client = new DmpA6ApiClient(this.address);
            Map<String, Object> deviceInfo = client.connect(this.getClass().getSimpleName(), DRIVER_ID);

            result = (deviceInfo != null);
        } catch (Exception ex) {
            result = false;
        }


        return result;
    }
}
