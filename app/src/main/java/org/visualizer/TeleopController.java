package org.visualizer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

public class TeleopController {
    @FXML
    private StackPane rootPane;  // The root pane containing everything
    @FXML
    private HBox heightSelector;

    private DropShadow highlightEffect;

    private char selectedBranch = 'A';
    private int selectedStation = 1;
    private int selectedHeight = 1;

    @FXML
    private void initialize() {
        heightSelector.setPadding(Insets.EMPTY);
        heightSelector.setMaxWidth(Region.USE_PREF_SIZE);
        heightSelector.setMaxHeight(Region.USE_PREF_SIZE);

        highlightEffect = new DropShadow();
        highlightEffect.setColor(Color.ORANGE);
        highlightEffect.setRadius(30);

        ControllerHelper.addBranches(rootPane);
    }

    @FXML
    private void handleCircleClick(MouseEvent event)
    {
        Shape oldCircle = (Shape)rootPane.lookup("#" + String.valueOf(selectedBranch));
        if (oldCircle != null) oldCircle.setEffect(null);

        Shape clickedCircle = (Shape) event.getSource();
        selectedBranch = clickedCircle.getId().charAt(0);
        clickedCircle.setEffect(highlightEffect);
        clickedCircle.setPickOnBounds(false);

        NetworkCommunicator.getInstance().updateTeleopBranch(selectedBranch);
    }

    @FXML
    private void handleStationClick(ActionEvent event)
    {
        Button oldObj = (Button)rootPane.lookup("#S" + selectedStation);
        if (oldObj != null) oldObj.setEffect(null);

        Button obj = (Button) event.getSource();
        String name = obj.getId();

        obj.setEffect(highlightEffect);
        obj.setPickOnBounds(false);

        selectedStation = (int)(name.charAt(1) - '0');
        NetworkCommunicator.getInstance().updateTeleopStation(selectedStation);
    }

    @FXML
    private void handleHeightSelectionPress(ActionEvent event)
    {
        Button oldButton = (Button)rootPane.lookup("#Button" + selectedHeight);
        if (oldButton != null) oldButton.setEffect(null);

        Button source = (Button)(event.getSource());
        String id = source.getId();
        int num = id.charAt(id.length() - 1) - '0';

        source.setEffect(highlightEffect);
        source.setPickOnBounds(false);

        selectedHeight = num;

        NetworkCommunicator.getInstance().updateTeleopHeight(num);
    }
}
