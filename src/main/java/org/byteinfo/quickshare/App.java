package org.byteinfo.quickshare;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.byteinfo.context.Context;
import org.byteinfo.quickshare.config.Config;
import org.byteinfo.quickshare.controller.MainController;
import org.byteinfo.quickshare.service.DataService;

import java.io.IOException;

public class App extends Application {
	public static Stage stage;

	private final Context context = new Context();

	@Override
	public void start(Stage stage) throws IOException {
		App.stage = stage;
		DataService.init(context);
		Parent root = context.instance(MainController.class).getRoot();
		Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
		stage.setTitle(Config.APP_TITLE);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}