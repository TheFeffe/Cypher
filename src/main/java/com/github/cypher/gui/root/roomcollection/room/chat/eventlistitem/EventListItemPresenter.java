package com.github.cypher.gui.root.roomcollection.room.chat.eventlistitem;

import com.github.cypher.gui.CustomListCell;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.model.Client;
import com.github.cypher.model.Event;
import com.github.cypher.model.Message;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;

public class EventListItemPresenter extends CustomListCell<Event> {

	@Inject
	private Client client;

	@FXML
	private AnchorPane root;

	@FXML
	private Label author;

	@FXML
	private TextFlow bodyContainer;

	@FXML
	private ImageView avatar;

	private boolean formatted = false;
	private ChangeListener<String> bodyChangeListener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		if(formatted) {
			generateFormattedTextObjects(newValue);
		} else {
			generateTextObjects(newValue);
		}
	};

	private static final int LIST_CELL_PADDING = 19;

	public EventListItemPresenter() {
		super.parentProperty().addListener((observable, oldParent, newParent) -> {
			if(newParent != null) {
				Parent recursiveParent = newParent;
				while(recursiveParent != null && !(recursiveParent instanceof ListView)) {
					recursiveParent = recursiveParent.getParent();
				}
				if(recursiveParent != null) {
					root.maxWidthProperty().bind(((ListView)recursiveParent).widthProperty().subtract(LIST_CELL_PADDING));
				}
			} else {
				root.maxWidthProperty().unbind();
			}
		});
	}

	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		Event event = getModelComponent();

		if(event instanceof Message) {
			Message message = (Message)event;
			author.textProperty().bind(new FXThreadedObservableValueWrapper<>(message.getSender().nameProperty()));

			if(message.getFormattedBody() != null && !message.getFormattedBody().equals("")) {
				formatted = true;
				new FXThreadedObservableValueWrapper<>(message.formattedBodyProperty()).addListener(bodyChangeListener);
			} else {
				new FXThreadedObservableValueWrapper<>(message.bodyProperty()).addListener(bodyChangeListener);
			}

			if(formatted) {
				try {
					generateFormattedTextObjects(message.getFormattedBody());
				} catch(IllegalArgumentException e) {
					generateTextObjects(message.getBody());
				}
			} else {
				generateTextObjects(message.getBody());
			}
			avatar.imageProperty().bind(new FXThreadedObservableValueWrapper<>(message.getSender().avatarProperty()));
		}
	}

	@Override
	protected void clearBindings() {
		Event event = getModelComponent();
		if(event instanceof Message) {
			Message message = (Message)event;
			author.textProperty().unbind();
			message.bodyProperty().removeListener(bodyChangeListener);
			message.formattedBodyProperty().removeListener(bodyChangeListener);
			avatar.imageProperty().unbind();
		}
	}

	private void generateTextObjects(String text) {
		bodyContainer.getChildren().clear();
		Text textObject = new Text(text);
		bodyContainer.getChildren().add(textObject);
	}

	private void generateFormattedTextObjects(String text) throws IllegalArgumentException {

		Document document = Jsoup.parseBodyFragment(text);
		document.outputSettings(new Document.OutputSettings().prettyPrint(false));
		parseFormattedMessageNode(document.body(), new LinkedList<>());
	}

	private void parseFormattedMessageNode(org.jsoup.nodes.Node node, List<Element> parents) {
		List textFlowList = bodyContainer.getChildren();

		if(node instanceof TextNode) {
			// Ignore TextNodes containing only whitespace
			if(!node.outerHtml().replace(" ", "").equals("")) {

				String text = ((TextNode) node).getWholeText();
				Text textObject = new Text(text);
				boolean pre = false;

				// Go through all parent tags and apply styling
				for(Element element : parents) {
					String tagName = element.tagName();

					if       (tagName.equals("ul")) { // Begin bullet list
					} else if(tagName.equals("ol")) { // TODO: Begin numbered list
					} else if(tagName.equals("li")) {
						// List item
						textFlowList.add(new Text(" • "));
					} else if(tagName.equals("blockquote")) {
						textObject.getStyleClass().add("block-quote");
					} else if(tagName.equals("pre")) {
						// Preceeds a <code> tag to specify a multiline block
						pre = true;
					} else if(tagName.equals("code")) {
						// Monospace and TODO: code highlighting
						if(!pre) {
							textObject.getStyleClass().add("inline-monospace");
						} else {
							textObject.getStyleClass().add("block-monospace");
						}
						break; // We don't care about anything appearing within a <code> tag
					} else {
						// Other tags are applied ass CSS classes
						textObject.getStyleClass().add(tagName);
					}
				}
				textFlowList.add(textObject);
				textObject.applyCss();
			}
		} else if(node instanceof Element) {
			parents = new LinkedList<>(parents);
			parents.add((Element)node);
		}

		// Recursively parse child tags
		for(org.jsoup.nodes.Node child: node.childNodes()) {
			parseFormattedMessageNode(child, parents);
		}
	}
}