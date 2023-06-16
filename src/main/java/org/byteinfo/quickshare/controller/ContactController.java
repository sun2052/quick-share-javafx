package org.byteinfo.quickshare.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.byteinfo.quickshare.model.ContactModel;
import org.byteinfo.quickshare.service.DataService;
import org.byteinfo.quickshare.util.Constant;
import org.byteinfo.quickshare.util.StyleUtil;
import org.byteinfo.quickshare.util.Toast;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

public class ContactController extends BaseController {
	@FXML
	private TextField searchBox;
	@FXML
	private VBox container;

	@Inject
	private Provider<ContentController> contentControllerProvider;

	// address -> item controller
	private final SequencedMap<String, SidebarItemController> items = new LinkedHashMap<>();

	public ContactController() {
		super("sidebar.fxml");
	}

	@PostConstruct
	void init() {
		searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
			var lower = newValue.toLowerCase();
			for (SidebarItemController controller : items.values()) {
				var visible = controller.getTitle().toLowerCase().contains(lower) || controller.getSubtitle().toLowerCase().contains(lower);
				controller.getRoot().setVisible(visible);
			}
		});
	}

	public void addContact(ContactModel contact) {
		if (items.containsKey(contact.address())) {
			return;
		}
		var item = new SidebarItemController();
		item.setIconImage(contact.getIconImage());
		item.setTitle(contact.name());
		item.setSubtitle(contact.address());
		item.getRoot().setOnMouseClicked(event -> {
			for (SidebarItemController itemController : items.values()) {
				if (item == itemController) {
					StyleUtil.addClass(itemController.getRoot(), Constant.CLASS_ACTIVE);
				} else {
					StyleUtil.removeClass(itemController.getRoot(), Constant.CLASS_ACTIVE);
				}
			}
			contentControllerProvider.get().showChat(contact);
		});
		items.putLast(contact.address(), item);
		container.getChildren().add(item.getRoot());
	}

	public void removeContact(String address) {
		var removed = items.remove(address);
		if (removed != null) {
			container.getChildren().remove(removed.getRoot());
		}
	}

	public void refreshContacts() {
		Toast.show("Discovering contacts...");
		container.getChildren().clear();
		items.clear();
		DataService.refreshContacts();
	}
}