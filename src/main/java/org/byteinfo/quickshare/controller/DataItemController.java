package org.byteinfo.quickshare.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.byteinfo.quickshare.App;
import org.byteinfo.quickshare.config.Config;
import org.byteinfo.quickshare.model.ContactModel;
import org.byteinfo.quickshare.model.DataMessageModel;
import org.byteinfo.quickshare.service.DataService;
import org.byteinfo.quickshare.util.Constant;
import org.byteinfo.quickshare.util.DateTime;
import org.byteinfo.quickshare.util.Toast;
import org.byteinfo.util.text.SizeUtil;

import java.nio.file.Files;
import java.nio.file.Path;

public class DataItemController extends BaseController {
	@FXML
	private ImageView contentIcon;
	@FXML
	private Label headerLabel;
	@FXML
	private Hyperlink saveButton;
	@FXML
	private Hyperlink copyButton;
	@FXML
	private Text nameText;
	@FXML
	private Text sizeText;
	@FXML
	private Text modifiedText;
	@FXML
	private Text statusText;

	private final ContactModel contactModel;
	private final DataMessageModel messageModel;

	public DataItemController(ContactModel contactModel, DataMessageModel messageModel) {
		super("dataItem.fxml");
		this.contactModel = contactModel;
		this.messageModel = messageModel;
		setMessage(messageModel);
	}

	@FXML
	void saveContent(ActionEvent event) {
		var chooser = new FileChooser();
		chooser.setInitialFileName(messageModel.getName());
		var file = chooser.showSaveDialog(App.stage);
		if (file == null) {
			return;
		}
		if (file.exists()) {
			Toast.show("File Already Exists.");
			return;
		}
		Path tmp = file.toPath().resolveSibling(file.getName() + Config.TMP_SUFFIX);
		if (Files.exists(tmp)) {
			Toast.show("File Transfer In Progress.");
			return;
		}
		DataService.sendDataRequest(contactModel.address(), messageModel.getId(), tmp.toFile());
	}

	@FXML
	void copyContent(ActionEvent event) {
		var content = new ClipboardContent();
		content.putString(messageModel.getName());
		Clipboard.getSystemClipboard().setContent(content);
		Toast.show("Message Copied.");
	}

	public void setStatus(int status, int progress) {
		if (progress >= 100) {
			status = Constant.STATUS_COMPLETED;
		}
		var text = "Status: " + switch (status) {
			case Constant.STATUS_PENDING -> "Pending";
			case Constant.STATUS_TRANSFERRING -> "Transferring " + progress + "%";
			case Constant.STATUS_COMPLETED -> "Completed";
			default -> "Expired";
		};
		statusText.setText("Status: " + text);
	}

	private void setMessage(DataMessageModel messageModel) {
		contentIcon.setImage(messageModel.getIconImage());
		headerLabel.setText(messageModel.getHeaderText());
		nameText.setText(messageModel.getName() + "\n");
		sizeText.setText("Size: " + SizeUtil.toHumanReadable(messageModel.getSize()) + "\n");
		modifiedText.setText("Modified: " + DateTime.formatDateTime(messageModel.getModified()) + "\n");
		setStatus(messageModel.getStatus(), 0);
		if (messageModel.isIncoming()) {
			saveButton.setVisible(true);
		}
	}
}
