package org.visualizer;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class AutoController {
    @FXML
    private StackPane buttonGraphics;
    @FXML
    private HBox buttonContainer; // HBox to hold the buttons
    @FXML
    private StackPane rootPane;  // The root pane containing everything
    @FXML
    private ListView<String> listView;
    
    private final ObservableList<String> items = FXCollections.observableArrayList();

    private char selectedCircle = 'A';

    @FXML
    private void initialize() {
        // Add options to the ComboBox
        buttonContainer.setPadding(Insets.EMPTY);
        buttonContainer.setMaxWidth(Region.USE_PREF_SIZE);
        buttonContainer.setMaxHeight(Region.USE_PREF_SIZE);
        buttonGraphics.setPadding(Insets.EMPTY);
        buttonGraphics.setMaxWidth(Region.USE_PREF_SIZE);
        buttonGraphics.setMaxHeight(Region.USE_PREF_SIZE);

        ArrayList<Button> buttons = new ArrayList<Button>();
        for (int i = 0; i < 4; i++)
        {
            Button curButton = new Button("L" + String.valueOf(i + 1));
            curButton.setPrefWidth(50);
            curButton.setPrefHeight(50);
            curButton.setOnAction(event -> {
                String selectedOption = curButton.getText();
                items.add(selectedCircle + selectedOption);

                NetworkCommunicator.getInstance().updateAutoCommands(getItemsArray());

                buttonGraphics.setVisible(false);
            });
            buttons.add(curButton);
        }
        buttonContainer.getChildren().addAll(buttons);

        rootPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            // Iterate through all children of rootPane to check if the click is inside any of them
            if (!isAnyChildClicked(rootPane, event)) {
                buttonGraphics.setVisible(false);
            }
        });

        // Setup command list
        listView.setItems(items);
        listView.setCellFactory(param -> new DraggableListCell());

        ControllerHelper.addBranches(rootPane);
    }

    private boolean isAnyChildClicked(Parent parent, MouseEvent event) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (!child.isMouseTransparent() && child.isPickOnBounds() && child.getBoundsInParent().contains(event.getX(), event.getY())) {
                return true; // Clicked inside this child, so don't hide buttonGraphics
            }
        }
        return false; // Clicked outside all children
    }

    private class DraggableListCell extends ListCell<String>
    {
        private final Button removeButton = new Button("X");

        public DraggableListCell()
        {
            removeButton.setOnAction(event -> {
                items.remove(getItem());
                NetworkCommunicator.getInstance().updateAutoCommands(getItemsArray());
            });

            setOnDragDetected(event -> {
                if (getItem() == null) return;
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(getItem());
                db.setContent(content);
                event.consume();
            });

            setOnDragOver(event -> {
                if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    String draggedItem = db.getString();
                    int draggedIdx = items.indexOf(draggedItem);
                    int thisIdx = getIndex();

                    if (thisIdx >= 0 && thisIdx < items.size())
                    {
                        items.remove(draggedIdx);
                        items.add(thisIdx, draggedItem);

                        NetworkCommunicator.getInstance().updateAutoCommands(getItemsArray());
    
                        event.setDropCompleted(true);
                        getListView().getSelectionModel().select(thisIdx);
                    }
                    else if (thisIdx >= items.size())
                    {
                        items.remove(draggedIdx);
                        items.add(draggedItem);

                        NetworkCommunicator.getInstance().updateAutoCommands(getItemsArray());
    
                        event.setDropCompleted(true);
                        getListView().getSelectionModel().select(thisIdx);
                    }
                }
                event.consume();
            });
        }

        @Override
        protected void updateItem(String item, boolean empty)
        {
            super.updateItem(item, empty);
            if (empty || item == null)
            {
                setText(null);
                setGraphic(null);
            }
            else
            {
                removeButton.setText("X");
                Label itemLabel = new Label(item); // Create label for the item text

                // Spacer to push the remove button to the right
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Create an HBox to align elements properly
                HBox container = new HBox(10, itemLabel, spacer, removeButton);
                container.setAlignment(Pos.CENTER_LEFT);

                setGraphic(container); // Use setGraphic instead of setText
            }
        }
    }


    @FXML
    private void handleCircleClick(MouseEvent event)
    {
        // Make the comboBox visible and set its position based on the mouse click
        Circle clickedCircle = (Circle) event.getSource();

        double circleX = clickedCircle.getTranslateX();
        double circleY = clickedCircle.getTranslateY();
        double circleR = clickedCircle.getRadius();

        buttonGraphics.setTranslateX(circleX);
        buttonGraphics.setTranslateY(circleY + circleR + 50);

        buttonGraphics.setVisible(true);

        selectedCircle = clickedCircle.getId().charAt(0);
    }

    @FXML
    private void handleStationClick(ActionEvent event)
    {
        Button obj = (Button) event.getSource();
        String name = obj.getId();

        items.add(name);
        NetworkCommunicator.getInstance().updateAutoCommands(getItemsArray());
    }

    private String[] getItemsArray()
    {
        String[] result = new String[items.size()];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = items.get(i);
        }
        return result;
    }
}
