package org.byteinfo.quickshare.message;

public record ControlMessage(int type, String id, String name, int device) {
}
