package org.byteinfo.quickshare.util;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.byteinfo.quickshare.App;
import org.byteinfo.util.function.Unchecked;

import java.util.LinkedList;
import java.util.Queue;

public class Toast {
	private static final int DURATION = 3 * 1000; // millis
	private static final Queue<Stage> queue = new LinkedList<>();

	public static void show(Stage parent, String message, int duration) {
		Stage stage = new Stage(StageStyle.TRANSPARENT);
		stage.initOwner(parent);

		Text text = new Text(message);
		text.setStyle("-fx-fill: white; -fx-font-size: 14;");

		VBox layout = new VBox(text);
		layout.setStyle("-fx-background-radius: 10; -fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10 20 10 20;");
		layout.setOnMouseClicked(event -> stage.hide());

		stage.setScene(new Scene(layout, Color.TRANSPARENT));
		stage.setOnShown(event -> {
			stage.setX(parent.getX() + parent.getWidth() / 2 - stage.getWidth() / 2);
			stage.setY(parent.getY() + parent.getHeight() / 2 - stage.getHeight() / 2);
		});

		hide();
		stage.show();
		queue.offer(stage);

		Thread.startVirtualThread(Unchecked.runnable(() -> {
			Thread.sleep(duration);
			Platform.runLater(stage::hide);
		}));
	}

	public static void show(String message) {
		show(App.stage, message, DURATION);
	}

	public static void hide() {
		Stage stage;
		while ((stage = queue.poll()) != null) {
			stage.hide();
		}
	}
}