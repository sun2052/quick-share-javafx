package org.byteinfo.quickshare.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.byteinfo.quickshare.model.ContactModel;
import org.byteinfo.quickshare.util.DateTime;

import java.util.HashMap;
import java.util.Map;

public class SidebarItemController extends BaseController {
	@FXML
	private ImageView iconImage;
	@FXML
	private Label titleLabel;
	@FXML
	private Label subtitleLabel;
	@FXML
	private Label messageTimeLabel;
	@FXML
	private Label pendingCountLabel;

	private ContactModel contact;
	private Map<String, Object> messages = new HashMap<>();

	public SidebarItemController() {
		super("sidebarItem.fxml");
		getRoot().managedProperty().bind(getRoot().visibleProperty());
		pendingCountLabel.managedProperty().bind(pendingCountLabel.visibleProperty());
	}

	public SidebarItemController setIconImage(Image image) {
		iconImage.setImage(image);
		return this;
	}

	public SidebarItemController setTitle(String title) {
		titleLabel.setText(title);
		return this;
	}

	public SidebarItemController setSubtitle(String subtitle) {
		subtitleLabel.setText(subtitle.replaceAll("\\r|\\n", ""));
		return this;
	}

	public SidebarItemController setMessageTime(long millis) {
		messageTimeLabel.setText(DateTime.formatShortDateOrTime(millis));
		return this;
	}

	public SidebarItemController increasePendingCount() {
		var count = 1;
		if (pendingCountLabel.isVisible()) {
			count += Integer.parseInt(pendingCountLabel.getText());
		}
		pendingCountLabel.setText(String.valueOf(count));
		pendingCountLabel.setVisible(true);
		return this;
	}

	public SidebarItemController removePendingCount() {
		pendingCountLabel.setVisible(false);
		return this;
	}

	public String getTitle() {
		return titleLabel.getText();
	}

	public String getSubtitle() {
		return subtitleLabel.getText();
	}

	public ContactModel getContact() {
		return contact;
	}

	public Map<String, Object> getMessages() {
		return messages;
	}
}