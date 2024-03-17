package com.github.berrycontrol.drivers.dmpa6.commands;

import com.github.berrycontrol.drivers.dmpa6.DmpA6DeviceCommand;

public class DmpA6GenericCommand extends DmpA6DeviceCommand {
    private final String resourcePath;
    private final String commandUri;

    public DmpA6GenericCommand(Command cmd, String title, String resourcePath, String commandUri) {
        super(cmd.ordinal(), title);

        this.resourcePath = resourcePath;
        this.commandUri = commandUri;
    }

    @Override
    public String getIconResourcePath() {
        return resourcePath;
    }

    @Override
    public void execute(String deviceId) {
        this.execute(commandUri, deviceId);
    }
}
