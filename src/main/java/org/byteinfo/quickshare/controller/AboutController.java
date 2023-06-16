package org.byteinfo.quickshare.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.byteinfo.quickshare.config.Config;
import org.byteinfo.quickshare.util.NetUtil;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class AboutController extends BaseController {
	@FXML
	private Label titleLabel;
	@FXML
	private Hyperlink linkButton;
	@FXML
	private Label addressesLabel;
	@FXML
	private TextArea deviceInfoArea;

	private Stage stage;

	public AboutController() {
		super("about.fxml");
	}

	@FXML
	void openLink(ActionEvent event) throws URISyntaxException, IOException {
		Desktop.getDesktop().browse(new URI("https://github.com/sun2052/quick-share-javafx"));
	}

	public void show(Stage parent) {
		setTitle();
		setLocalAddresses();
		setDeviceInfo();
		if (stage == null) {
			stage = new Stage();
			stage.setTitle("About " + Config.APP_TITLE);
			stage.setResizable(false);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(new Scene(getRoot()));
			stage.setOnShown(event -> {
				stage.setX(parent.getX() + parent.getWidth() / 2 - stage.getWidth() / 2);
				stage.setY(parent.getY() + parent.getHeight() / 2 - stage.getHeight() / 2);
			});
		}
		stage.show();
	}

	public void hide() {
		stage.hide();
	}

	private void setTitle() {
		titleLabel.setText(Config.APP_TITLE + " " + Config.APP_VERSION);
	}

	private void setLocalAddresses() {
		List<String> list;
		try {
			list = NetUtil.getLocalAddresses().stream().map(InetAddress::getHostAddress).toList();
		} catch (SocketException e) {
			// ignore
			list = List.of("Failed to query local addresses.");
		}
		addressesLabel.setText(list.toString());
	}

	private void setDeviceInfo() {
		var props = new String[] {"java.version", "java.version.date", "java.home", "java.vendor", "java.vm.name", "java.vm.version", "java.vm.vendor", "os.name", "os.version", "os.arch", "user.name", "user.home", "user.language",};
		var list = Arrays.stream(props).map(key -> key + ": " + System.getProperty(key)).toList();
		deviceInfoArea.setText(String.join("\n", list));
	}
}
