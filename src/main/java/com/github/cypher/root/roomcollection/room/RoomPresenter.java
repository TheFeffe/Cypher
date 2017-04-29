package com.github.cypher.root.roomcollection.room;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.root.roomcollection.room.chat.ChatView;

import com.github.cypher.root.roomcollection.room.settings.SettingsView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;

public class RoomPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private StackPane mainStackPane;


	@FXML
	private HBox chatRoot;

	@FXML
	private AnchorPane chat;

	@FXML
	private AnchorPane chatExtra;

	@FXML
	private void initialize() {

		ChatView chatView = new ChatView();
		chat.getChildren().add(chatView.getView());

		Parent settingsPane = new SettingsView().getView();
		mainStackPane.getChildren().add(settingsPane);

		chatRoot.toFront();

		client.selectedRoom.addListener(((observable, oldValue, newValue) -> {
			// If a new room is selected the RoomSettings view is hidden.
			if (oldValue != null && newValue != null && oldValue != newValue) {
				client.showRoomSettings.set(false);
			}
		}));

		client.showRoomSettings.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				settingsPane.toFront();
			} else {
				chatRoot.toFront();
			}
		});
	}
}
