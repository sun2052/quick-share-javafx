package org.byteinfo.quickshare.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;
import org.byteinfo.quickshare.model.TextMessageModel;
import org.byteinfo.quickshare.util.Toast;

public class TextItemController extends BaseController {
	@FXML
	private Label headerLabel;
	@FXML
	private Hyperlink copyButton;
	@FXML
	private Text contentText;

	public TextItemController(TextMessageModel messageModel) {
		super("textItem.fxml");
		setMessage(messageModel);
	}

	@FXML
	void copyContent(ActionEvent event) {
		var content = new ClipboardContent();
		content.putString(contentText.getText().strip());
		Clipboard.getSystemClipboard().setContent(content);
		Toast.show("Message Copied.");
	}

	private void setMessage(TextMessageModel messageModel) {
		headerLabel.setText(messageModel.getHeaderText());
		contentText.setText(messageModel.getContent());
	}
}
