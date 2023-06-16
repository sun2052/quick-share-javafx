package org.byteinfo.quickshare.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.byteinfo.quickshare.App;
import org.byteinfo.quickshare.model.ContactModel;
import org.byteinfo.quickshare.model.DataMessageModel;
import org.byteinfo.quickshare.model.MessageModel;
import org.byteinfo.quickshare.model.TextMessageModel;
import org.byteinfo.quickshare.service.DataService;
import org.byteinfo.quickshare.util.Toast;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

public class ContentController extends BaseController {
	@FXML
	private ImageView chatIconImage;
	@FXML
	private Label chatTitleLabel;
	@FXML
	private Label chatSubtitleLabel;
	@FXML
	private ScrollPane messagePane;
	@FXML
	private VBox messageContainer;
	@FXML
	private TextArea messageBox;
	@FXML
	private Button sendButton;
	@FXML
	private Button uploadButton;

	@Inject
	private Provider<MainController> mainControllerProvider;
	@Inject
	private Provider<ChatController> chatControllerProvider;

	private boolean initialized;
	private ContactModel currentContact;
	private final Map<String, DataItemController> items = new HashMap<>(); // msg id -> item controller

	public ContentController() {
		super("content.fxml");
	}

	@FXML
	void sendFile(ActionEvent event) {
		var chooser = new FileChooser();
		var file = chooser.showOpenDialog(App.stage);
		if (file == null) {
			return;
		}
		var sent = DataService.sendDataMessage(currentContact.address(), file);
		if (!sent) {
			showUnavailable();
		}
	}

	@FXML
	void sendMessage(ActionEvent event) {
		var content = messageBox.getText();
		if (content.isEmpty()) {
			return;
		}
		var sent = DataService.sendTextMessage(currentContact.address(), content);
		if (sent) {
			messageBox.clear();
		} else {
			showUnavailable();
		}
	}

	public void showChat(ContactModel contactModel) {
		currentContact = contactModel;
		chatIconImage.setImage(contactModel.getIconImage());
		chatTitleLabel.setText(contactModel.name());
		chatSubtitleLabel.setText(contactModel.address());

		items.clear();
		messageContainer.getChildren().clear();
		for (MessageModel messageModel : DataService.getChat(currentContact.address()).values()) {
			renderMessage(contactModel, messageModel);
		}

		if (!initialized) {
			messagePane.vvalueProperty().bind(messageContainer.heightProperty());
			mainControllerProvider.get().getContentContainer().getChildren().add(getRoot());
			initialized = true;
		}
	}

	public void addMessage(ContactModel contactModel, MessageModel messageModel) {
		if (currentContact != null && currentContact.address().equals(contactModel.address())) {
			renderMessage(contactModel, messageModel);
		}
	}

	public void updateProgress(MessageModel messageModel) {
		var item = items.get(messageModel.getId());
		if (item != null) {
			var msg = (DataMessageModel) messageModel;
			item.setStatus(msg.getStatus(), msg.getProgress());
		}
	}

	public ContactModel getCurrentContact() {
		return currentContact;
	}

	private void renderMessage(ContactModel contactModel, MessageModel messageModel) {
		if (messageModel instanceof TextMessageModel textModel) {
			var item = new TextItemController(textModel);
			messageContainer.getChildren().add(item.getRoot());
		} else if (messageModel instanceof DataMessageModel dataModel) {
			var item = new DataItemController(contactModel, dataModel);
			items.put(dataModel.getId(), item);
			messageContainer.getChildren().add(item.getRoot());
		}
	}

	private void showUnavailable() {
		Toast.show("Contact \"" + currentContact.name() + "\" is currently unavailable.");
	}
}