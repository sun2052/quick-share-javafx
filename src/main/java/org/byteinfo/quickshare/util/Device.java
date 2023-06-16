package org.byteinfo.quickshare.util;

import java.util.UUID;

public interface Device {
	String ID = UUID.randomUUID().toString();
	String NAME = System.getProperty("user.name") + "@" + System.getProperty("os.name");
	int TYPE = Constant.DEVICE_DESKTOP;
}
