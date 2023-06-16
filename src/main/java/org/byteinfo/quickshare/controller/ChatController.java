package org.byteinfo.quickshare.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.byteinfo.quickshare.model.ContactModel;
import org.byteinfo.quickshare.model.DataMessageModel;
import org.byteinfo.quickshare.model.MessageModel;
import org.byteinfo.quickshare.model.TextMessageModel;
import org.byteinfo.quickshare.service.DataService;
import org.byteinfo.quickshare.util.Constant;
import org.byteinfo.quickshare.util.StyleUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

public class ChatController extends BaseController {
	@FXML
	private TextField searchBox;
	@FXML
	private VBox container;

	@Inject
	private Provider<ContentController> contentControllerProvider;

	// address -> item controller
	private final SequencedMap<String, SidebarItemController> items = new LinkedHashMap<>();

	public ChatController() {
		super("sidebar.fxml");
	}

	@PostConstruct
	void init() {
		searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
			items.forEach((address, controller) -> {
				var lower = newValue.toLowerCase();
				var visible = controller.getTitle().toLowerCase().contains(lower);
				if (!visible) {
					var chat = DataService.getChat(address);
					if (chat != null) {
						for (MessageModel message : chat.values()) {
							if (message instanceof TextMessageModel model && model.getContent().toLowerCase().contains(lower)) {
								visible = true;
								break;
							}
							if (message instanceof DataMessageModel model && model.getName().toLowerCase().contains(lower)) {
								visible = true;
								break;
							}
						}
					}
				}
				controller.getRoot().setVisible(visible);
			});
		});
	}

	public void addMessage(ContactModel contact, MessageModel message) {
		var item = items.get(contact.address());
		if (item == null) {
			item = new SidebarItemController();
			item.setIconImage(contact.getIconImage());
			item.setTitle(contact.name());
			var controller = item;
			controller.getRoot().setOnMouseClicked(event -> {
				for (SidebarItemController itemController : items.values()) {
					if (controller == itemController) {
						StyleUtil.addClass(itemController.getRoot(), Constant.CLASS_ACTIVE);
					} else {
						StyleUtil.removeClass(itemController.getRoot(), Constant.CLASS_ACTIVE);
					}
				}
				controller.removePendingCount();
				contentControllerProvider.get().showChat(contact);
			});
		} else {
			container.getChildren().remove(item.getRoot());
		}
		item.setSubtitle(formatMessage(message));
		item.setMessageTime(message.getTime());
		if (message.isIncoming() && !contact.equals(contentControllerProvider.get().getCurrentContact())) {
			item.increasePendingCount();
		}
		container.getChildren().addFirst(item.getRoot());
		items.putFirst(contact.address(), item);
	}

	private String formatMessage(MessageModel message) {
		if (message instanceof TextMessageModel text) {
			return text.getContent();
		}
		if (message instanceof DataMessageModel data) {
			return "[" + data.getName() + "]";
		}
		throw new IllegalArgumentException("Unsupported message: " + message);
	}
}