package org.sweattypalms.dungeon_generation;

import javafx.scene.paint.Color;
import lombok.Getter;

@Getter
public enum RoomType {
    BROWN(Color.SADDLEBROWN),
    GREEN(Color.GREEN),
    RED(Color.RED),
    PINK(Color.HOTPINK),
    YELLOW(Color.YELLOW.brighter()),
    ORANGE(Color.DARKORANGE),
    PURPLE(Color.PURPLE),
    TEST(Color.ALICEBLUE);

    private final Color color;

    RoomType(Color color) {
        this.color = color;
    }
}