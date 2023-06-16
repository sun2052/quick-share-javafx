package org.byteinfo.quickshare.util;

import org.byteinfo.util.net.IPv4Util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface NetUtil {
	static List<InetAddress> getBroadcastAddresses() throws SocketException {
		List<InetAddress> list = new ArrayList<>();
		for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			if (iface.isUp() && !iface.isLoopback()) {
				for (InterfaceAddress address : iface.getInterfaceAddresses()) {
					var broadcast = address.getBroadcast();
					if (broadcast != null) {
						list.add(broadcast);
					}
				}
			}
		}
		return list;
	}

	static List<InetAddress> getLocalAddresses() throws SocketException {
		List<InetAddress> list = new ArrayList<>();
		for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			if (iface.isUp() && !iface.isLoopback()) {
				for (InetAddress address : Collections.list(iface.getInetAddresses())) {
					if (IPv4Util.isPrivate(address)) {
						list.add(address);
					}
				}
			}
		}
		return list;
	}
}
