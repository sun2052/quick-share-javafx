package org.byteinfo.quickshare.model;

import org.byteinfo.quickshare.util.DateTime;

public abstract class MessageModel {
	private String id;
	private long time;
	private int status;
	private boolean incoming;

	public MessageModel(String id, long time, int status, boolean incoming) {
		this.id = id;
		this.time = time;
		this.status = status;
		this.incoming = incoming;
	}

	public String getId() {
		return id;
	}


	public long getTime() {
		return time;
	}

	public int getStatus() {
		return status;
	}

	public boolean isIncoming() {
		return incoming;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getHeaderText() {
		var dateTime = DateTime.formatDateTime(time);
		return incoming ? "🡿 " + dateTime : "🡽 " + dateTime;
	}
}
