package com.github.cypher.root.roomcollection.room.chat;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javax.inject.Inject;

public class ChatPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private ListView eventList;

	@FXML
	private TextArea messageBox;

	@FXML
	private void initialize() {
	}

	@FXML
	private void showRoomSettings() {
		client.showRoomSettings.set(true);
	}
}
