package com.github.cypher.gui.root.settings;

import com.github.cypher.settings.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class SettingsPresenter {
	@Inject
	private Client client;

	@Inject
	private Settings settings;
}
