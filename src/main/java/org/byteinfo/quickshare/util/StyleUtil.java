package org.byteinfo.quickshare.util;

import javafx.scene.Node;

import java.util.HashSet;

public interface StyleUtil {
	static void addClass(Node node, String... classes) {
		var set = new HashSet<>(node.getStyleClass());
		for (String clazz : classes) {
			if (!set.contains(clazz)) {
				node.getStyleClass().add(clazz);
			}
		}
	}

	static void removeClass(Node node, String... classes) {
		node.getStyleClass().removeAll(classes);
	}

	static void toggleClass(Node node, String... classes) {
		var set = new HashSet<>(node.getStyleClass());
		for (String clazz : classes) {
			if (set.contains(clazz)) {
				node.getStyleClass().remove(clazz);
			} else {
				node.getStyleClass().add(clazz);
			}
		}
	}

	static boolean hasClass(Node node, String clazz) {
		return node.getStyleClass().contains(clazz);
	}
}
