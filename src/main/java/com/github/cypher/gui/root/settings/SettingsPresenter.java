package com.github.cypher.gui.root.settings;

import com.github.cypher.gui.Executor;
import com.github.cypher.model.Client;
import com.github.cypher.model.SdkException;
import com.github.cypher.settings.Settings;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.inject.Inject;
import java.util.Locale;

public class SettingsPresenter {
	@Inject
	private Client client;

	@Inject
	private Settings settings;
  
	@Inject
	private Executor executor;

	@FXML
	private ChoiceBox<String> languageChoiceBox;

	@FXML
	private CheckBox systemTrayCheckBox;

	@FXML
	private RadioButton enterRadioButton;

	@FXML
	private RadioButton ctrlEnterRadioButton;

	@FXML
	private Button logoutButton;

	@FXML
	private Label changesRequireRestartLabel;

	@FXML
	private void initialize() {
		String language = settings.getLanguage().getLanguage();
		if ("en".equals(language)) {
			languageChoiceBox.setValue("English");
		}else if ("sv".equals(language)) {
			languageChoiceBox.setValue("Svenska");
		}

		languageChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldLanguage, newLanguage) -> {
			if ("English".equals(newLanguage)) {
				settings.setLanguage(Locale.ENGLISH);
			} else if ("Svenska".equals(newLanguage)) {
				settings.setLanguage(new Locale("sv","SE"));
			}

			changesRequireRestartLabel.setVisible(true);
		});

		if (settings.getUseSystemTray()) {
			systemTrayCheckBox.setSelected(true);
		} else {
			systemTrayCheckBox.setSelected(false);
		}

		systemTrayCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				settings.setUseSystemTray(true);
			} else {
				settings.setUseSystemTray(false);
			}
			changesRequireRestartLabel.setVisible(true);
		});

		ToggleGroup sendMessageToggleGroup = new ToggleGroup();

		enterRadioButton.setToggleGroup(sendMessageToggleGroup);
		ctrlEnterRadioButton.setToggleGroup(sendMessageToggleGroup);

		if (settings.getControlEnterToSendMessage()) {
			ctrlEnterRadioButton.setSelected(true);
		} else {
			enterRadioButton.setSelected(true);
		}

		sendMessageToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == enterRadioButton) {
				settings.setControlEnterToSendMessage(false);
			} else {
				settings.setControlEnterToSendMessage(true);
			}
		});
	}

	@FXML
	private void logout() {
		logoutButton.setDisable(true);
		executor.handle(() -> {
			try {
				client.logout();
			} catch (SdkException e) {
				System.out.printf("SdkException when trying to logout - %s\n", e.getMessage());
			}
			Platform.runLater(() -> logoutButton.setDisable(false));
		});
	}
}
