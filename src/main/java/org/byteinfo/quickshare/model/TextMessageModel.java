package org.byteinfo.quickshare.model;

public class TextMessageModel extends MessageModel {
	private String content;

	public TextMessageModel(String id, long time, int status, boolean incoming, String content) {
		super(id, time, status, incoming);
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
