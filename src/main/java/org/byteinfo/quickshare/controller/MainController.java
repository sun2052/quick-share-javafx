package org.byteinfo.quickshare.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.byteinfo.quickshare.App;
import org.byteinfo.quickshare.util.Constant;
import org.byteinfo.quickshare.util.StyleUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;

public class MainController extends BaseController {
	@FXML
	private Button contactButton;
	@FXML
	private Button chatButton;
	@FXML
	private Button refreshButton;
	@FXML
	private Button aboutButton;
	@FXML
	private VBox sidebarContainer;
	@FXML
	private VBox contentContainer;

	@Inject
	private Provider<ChatController> chatControllerProvider;
	@Inject
	private Provider<ContactController> contactControllerProvider;
	@Inject
	private Provider<AboutController> aboutControllerProvider;

	public MainController() {
		super("main.fxml");
	}

	@PostConstruct
	void init() {
		showContacts(null);
	}

	@FXML
	void showContacts(ActionEvent event) {
		StyleUtil.addClass(contactButton, Constant.CLASS_ACTIVE);
		StyleUtil.removeClass(chatButton, Constant.CLASS_ACTIVE);
		sidebarContainer.getChildren().clear();
		sidebarContainer.getChildren().add(contactControllerProvider.get().getRoot());
	}

	@FXML
	void showChats(ActionEvent event) {
		StyleUtil.addClass(chatButton, Constant.CLASS_ACTIVE);
		StyleUtil.removeClass(contactButton, Constant.CLASS_ACTIVE);
		sidebarContainer.getChildren().clear();
		sidebarContainer.getChildren().add(chatControllerProvider.get().getRoot());
	}

	@FXML
	void refreshContacts(ActionEvent event) {
		showContacts(null);
		contactControllerProvider.get().refreshContacts();

	}

	@FXML
	void showAbout(ActionEvent event) {
		aboutControllerProvider.get().show(App.stage);
	}

	public VBox getContentContainer() {
		return contentContainer;
	}
}