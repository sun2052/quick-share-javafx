package org.byteinfo.quickshare.util;

public interface Constant {
	// broadcast type
	int BROADCAST_DISCOVER = 1;
	int BROADCAST_RESPONSE = 2;
	int BROADCAST_QUIT = 3;

	// message type
	int MESSAGE_UNKNOWN = 0;
	int MESSAGE_TEXT = 1;
	int MESSAGE_DATA = 2;
	int MESSAGE_DATA_REQUEST = 3;
	int MESSAGE_DATA_RESPONSE = 4;
	int MESSAGE_DATA_EXPIRED = 5;
	int MESSAGE_ACTIVATE = 6;

	// message status
	int STATUS_UNKNOWN = 0;
	int STATUS_COMPLETED = 1;
	int STATUS_EXPIRED = 2;
	int STATUS_PENDING = 3;
	int STATUS_TRANSFERRING = 4;

	// device type
	int DEVICE_UNKNOWN = 0;
	int DEVICE_DESKTOP = 1;
	int DEVICE_MOBILE = 2;

	// uuid string length
	int UUID_LENGTH = 16 * 2 + 4;

	// active class name
	String CLASS_ACTIVE = "active";
	
	// device icon url
	String IMAGE_UNKNOWN = "org/byteinfo/quickshare/controller/images/devices_48dp.png";
	String IMAGE_DESKTOP = "org/byteinfo/quickshare/controller/images/computer_48dp.png";
	String IMAGE_MOBILE = "org/byteinfo/quickshare/controller/images/smartphone_48dp.png";

	// data icon url
	String IMAGE_UPLOAD = "org/byteinfo/quickshare/controller/images/upload_48dp.png";
	String IMAGE_DOWNLOAD = "org/byteinfo/quickshare/controller/images/download_48dp.png";
}
