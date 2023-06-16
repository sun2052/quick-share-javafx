package org.byteinfo.quickshare.config;

public interface Config {
	String APP_TITLE = "Quick Share";
	String APP_VERSION = "1.0.0";

	double WINDOW_WIDTH = 900;
	double WINDOW_HEIGHT = 600;

	int PORT = 8972;

	String TMP_SUFFIX = ".!part";
	int CHUNK_SIZE = 1024 * 1024; // 1 MB
	int BUFFER_SIZE = 1024 * 1024; // 1 MB
}
