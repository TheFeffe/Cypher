package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;


public class Member {
	private final User user;

	private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty();

	private final StringProperty name = new SimpleStringProperty();

	Member(com.github.cypher.sdk.Member sdkMember, Repository<User> repo) {
		this.name.set(sdkMember.getUser().getName());
		this.user = repo.get(sdkMember.getUser().getId());
	}

	public Image getImageProperty() {
		return imageProperty.get();
	}

	public ObjectProperty<Image> imagePropertyProperty() {
		return imageProperty;
	}

	public User getUser() {
		return user;
	}

	public StringProperty getName() {
		return name;
	}
}
