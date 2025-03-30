package org.visualizer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

// Some functions that both teleop and auto controllers have in common
public class ControllerHelper {
    public static void addBranches(Pane rootPane)
    {
        final int radius = 190; // "Radius" of hexagon is 190
        final int centerX = 0;
        final int centerY = -40;
        final int distBetweenBranches = 100;

        for (int i = 0; i < 12; i++)
        {
            double angle = -60 * (i / 2) + 90;
            double radians = Math.toRadians(angle);
            double px = centerX + Math.cos(radians) * radius;
            double py = centerY + Math.sin(radians) * radius;
            double hexSideAngle = radians + (Math.PI / 2);
            
            double offsetX = Math.cos(hexSideAngle) * (distBetweenBranches / 2);
            double offsetY = Math.sin(hexSideAngle) * (distBetweenBranches / 2);
            double branchX = px + offsetX;
            double branchY = py + offsetY;
            if (i % 2 == 1)
            {
                branchX = px - offsetX;
                branchY = py - offsetY;
            }

            char curChar = (char)('A' + i);

            Circle curCircle = (Circle)(rootPane.lookup("#" + curChar));
            curCircle.setTranslateX(branchX);
            curCircle.setTranslateY(branchY);
            
            double length = 0.7 * curCircle.getRadius() * 2;

            double halfLength = length / 2;

            Line line = (Line)(rootPane.lookup("#line" + curChar));
            line.setStartX(0 - halfLength * Math.cos(radians));
            line.setStartY(0 - halfLength * Math.sin(radians));
            line.setEndX(0 + halfLength * Math.cos(radians));
            line.setEndY(0 + halfLength * Math.sin(radians));
            line.setStrokeWidth(5);
            line.setStroke(Color.RED);

            line.setTranslateX(branchX);
            line.setTranslateY(branchY);

            line.setMouseTransparent(true);

            Text text = (Text)(rootPane.lookup("#text" + curChar));
            text.setTranslateX(branchX);
            text.setTranslateY(branchY);
        }
    }
}
