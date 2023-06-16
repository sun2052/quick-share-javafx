package org.byteinfo.quickshare.message;

public record DataMessage(String id, long time, String name, long size, long modified) {
}
