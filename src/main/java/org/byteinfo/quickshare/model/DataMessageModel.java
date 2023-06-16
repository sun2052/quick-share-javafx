package org.byteinfo.quickshare.model;

import javafx.scene.image.Image;
import org.byteinfo.quickshare.util.Constant;

public class DataMessageModel extends MessageModel {
	private String name;
	private long size;
	private long modified;
	private int progress;

	public DataMessageModel(String id, long time, int status, boolean incoming, String name, long size, long modified, int progress) {
		super(id, time, status, incoming);
		this.name = name;
		this.size = size;
		this.modified = modified;
		this.progress = progress;
	}

	public String getName() {
		return name;
	}

	public long getSize() {
		return size;
	}

	public long getModified() {
		return modified;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public Image getIconImage() {
		if (isIncoming()) {
			return new Image(Constant.IMAGE_DOWNLOAD);
		} else {
			return new Image(Constant.IMAGE_UPLOAD);
		}
	}
}
