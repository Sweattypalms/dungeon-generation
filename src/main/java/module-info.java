module org.sweattypalms.dungeon_generation {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens org.sweattypalms.dungeon_generation to javafx.fxml;
    exports org.sweattypalms.dungeon_generation;
    exports org.sweattypalms.dungeon_generation.components;
    opens org.sweattypalms.dungeon_generation.components to javafx.fxml;
}