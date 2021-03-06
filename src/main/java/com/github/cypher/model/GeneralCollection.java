package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class GeneralCollection implements RoomCollection {
	private static final Image GENERAL_COLLECTION_IMAGE = new Image(GeneralCollection.class.getResourceAsStream("/images/fa-users-white-40.png"));
	private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>(GENERAL_COLLECTION_IMAGE); // Should this maybe be generated on first request instead?
	private final ObservableList<Room> rooms = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	// Package private empty constructor
	GeneralCollection(){ /*Empty*/}

	@Override
	public ObservableList<Room> getRoomsProperty() {
		return rooms;
	}

	@Override
	public void addRoom(Room room) {
		if (!rooms.contains(room)){
			rooms.add(room);
		}
	}

	public void removeRoom(Room room){
		rooms.remove(room);
	}

	@Override
	public Image getImage() {
		return GENERAL_COLLECTION_IMAGE;
	}

	@Override
	public ObjectProperty<Image> getImageProperty() {
		return imageProperty;
	}
}
