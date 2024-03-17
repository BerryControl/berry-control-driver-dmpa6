package com.github.berrycontrol.drivers.dmpa6.commands;

import com.github.berrycontrol.drivers.dmpa6.DmpA6DeviceCommand;

public class DmpA6PowerToggleCommand extends DmpA6DeviceCommand {
    public DmpA6PowerToggleCommand() {
        super(Command.POWER_TOGGLE.ordinal(), "Power Toggle");
    }

    @Override
    public String getIconResourcePath() {
        return "images/power_toggle.png";
    }

    @Override
    public void execute(String deviceId) {

    }
}
