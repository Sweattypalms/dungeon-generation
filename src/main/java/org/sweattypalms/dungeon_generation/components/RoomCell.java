package org.sweattypalms.dungeon_generation.components;

import javafx.scene.shape.Rectangle;
import org.sweattypalms.dungeon_generation.DungeonApp;
import org.sweattypalms.dungeon_generation.Point;
import org.sweattypalms.dungeon_generation.Room;
import org.sweattypalms.dungeon_generation.RoomShapeHandler;

public class RoomCell extends Rectangle {

    public static Point getPlaceLocation(Room room) {
        return getPlaceLocation(room.getCoordinates());
    }

    public static Point getPlaceLocation(Point gridCoordinates) {
        return new Point(gridCoordinates.y() * (DungeonApp.CELL_SIZE + DungeonApp.GAP) + DungeonApp.PADDING,
                gridCoordinates.x() * (DungeonApp.CELL_SIZE + DungeonApp.GAP) + DungeonApp.PADDING);
    }

    public RoomCell(Room room, int size) {
        super(getPlaceLocation(room).x(), getPlaceLocation(room).y(), size, size);

        setFill(room.getType().getColor());
        setArcWidth(10);
        setArcHeight(10);
    }
}
