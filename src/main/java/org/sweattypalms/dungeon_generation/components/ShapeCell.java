package org.sweattypalms.dungeon_generation.components;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.sweattypalms.dungeon_generation.DungeonApp;
import org.sweattypalms.dungeon_generation.Point;
import org.sweattypalms.dungeon_generation.Room;
import org.sweattypalms.dungeon_generation.RoomShape;
import org.sweattypalms.dungeon_generation.RoomShapeHandler;

import java.util.List;

public class ShapeCell extends Pane {
    public ShapeCell(RoomShapeHandler shapeHandler) {
        if (shapeHandler == null || shapeHandler.getShape() == null) return;

        if (List.of(RoomShape.SINGLE, RoomShape.TWO_X_ONE, RoomShape.THREE_X_ONE, RoomShape.FOUR_X_ONE, RoomShape.SQUARE).contains(shapeHandler.getShape())) {
            drawRectangularShapes(shapeHandler);
        } else {
            drawIrregularShapes(shapeHandler);
        }
    }

    private void drawRectangularShapes(RoomShapeHandler shapeHandler) {
        List<Room> rooms = shapeHandler.getRooms();

        // Find the bounding box for the shape
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Room room : rooms) {
            Point coordinates = room.getCoordinates();
            if (coordinates.x() < minX) minX = coordinates.x();
            if (coordinates.y() < minY) minY = coordinates.y();
            if (coordinates.x() > maxX) maxX = coordinates.x();
            if (coordinates.y() > maxY) maxY = coordinates.y();
        }

        int finalMinX = minX;
        int finalMinY = minY;
        Room startRoom = shapeHandler.getRooms().stream().filter(r -> {
            Point coordinates = r.getCoordinates();
            return (coordinates.x() == finalMinX && coordinates.y() == finalMinY);
        }).findFirst().orElseThrow();

        int finalMaxX = maxX;
        int finalMaxY = maxY;
        Room endRoom = shapeHandler.getRooms().stream().filter(r -> {
            Point coordinates = r.getCoordinates();
            return (coordinates.x() == finalMaxX && coordinates.y() == finalMaxY);
        }).findFirst().orElseThrow();

        Point startPlaceCoords = RoomCell.getPlaceLocation(startRoom);
        Point endPlaceCoords = RoomCell.getPlaceLocation(endRoom);


        // Calculate the width and height of the bounding box
        int width = endPlaceCoords.x() - startPlaceCoords.x() + DungeonApp.CELL_SIZE;
        int height = endPlaceCoords.y() - startPlaceCoords.y() + DungeonApp.CELL_SIZE;

        // Draw a single rectangle that covers the entire shape
        Rectangle rect = new Rectangle(startPlaceCoords.x(), startPlaceCoords.y(), width, height);
        rect.setFill(Color.SADDLEBROWN);
        rect.setArcHeight(10);
        rect.setArcWidth(10);
        getChildren().add(rect);

/*        int rot = shapeHandler.getRot();
        Point[] rotatedPoints = shapeHandler.getShape().rotateWithRotationCount(rot);
        Text text = new Text(startPlaceCoords.x() + width / 2., startPlaceCoords.y() + height / 2., RoomShape.pointsToString(rotatedPoints));
        text.setFill(Color.WHITE);
        getChildren().add(text);*/
    }

    private void drawIrregularShapes(RoomShapeHandler shapeHandler) {
        List<Room> rooms = shapeHandler.getRooms();

        for (int i = 0; i < rooms.size(); i++) {
            Room roomA = rooms.get(i);
            Room roomB = rooms.get((i + 1) % rooms.size());

            Point a = roomA.getCoordinates(), b = roomB.getCoordinates();

            if (a.x() == b.x() || a.y() == b.y()) {
                // draw a single rectangle with min max
                Point startPlaceCoords = new Point(Math.min(a.x(), b.x()), Math.min(a.y(), b.y()));
                Point endPlaceCoords = new Point(Math.max(a.x(), b.x()), Math.max(a.y(), b.y()));

                startPlaceCoords = RoomCell.getPlaceLocation(startPlaceCoords);
                endPlaceCoords = RoomCell.getPlaceLocation(endPlaceCoords);


                int width = Math.abs(endPlaceCoords.x() - startPlaceCoords.x()) + DungeonApp.CELL_SIZE;
                int height = Math.abs(endPlaceCoords.y() - startPlaceCoords.y()) + DungeonApp.CELL_SIZE;

                Rectangle rect = new Rectangle(startPlaceCoords.x(), startPlaceCoords.y(), width, height);
                rect.setFill(Color.SADDLEBROWN);
                rect.setArcHeight(10);
                rect.setArcWidth(10);
                getChildren().add(rect);

/*                int rot = shapeHandler.getRot();
                Point[] rotatedPoints = shapeHandler.getShape().rotateWithRotationCount(rot);
                Text text = new Text(startPlaceCoords.x() + width / 2., startPlaceCoords.y() + height / 2., RoomShape.pointsToString(rotatedPoints));
                text.setFill(Color.WHITE);
                getChildren().add(text);*/
            }
        }
    }

}
