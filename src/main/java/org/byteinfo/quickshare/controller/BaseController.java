package org.byteinfo.quickshare.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public abstract class BaseController {
	private final Parent root;

	public BaseController(String view) {
		var loader = new FXMLLoader(getClass().getResource(view));
		loader.setControllerFactory(type -> this);
		try {
			root = loader.load();
			root.getStylesheets().add(getClass().getResource("styles/base.css").toExternalForm());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Parent getRoot() {
		return root;
	}
}
