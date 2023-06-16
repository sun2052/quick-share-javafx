package org.byteinfo.quickshare.model;

import javafx.scene.image.Image;
import org.byteinfo.quickshare.util.Constant;

public record ContactModel(String address, String name, int device) {
	public Image getIconImage() {
		if (device == Constant.DEVICE_DESKTOP) {
			return new Image(Constant.IMAGE_DESKTOP);
		} else if (device == Constant.DEVICE_MOBILE) {
			return new Image(Constant.IMAGE_MOBILE);
		} else {
			return new Image(Constant.IMAGE_UNKNOWN);
		}
	}
}
