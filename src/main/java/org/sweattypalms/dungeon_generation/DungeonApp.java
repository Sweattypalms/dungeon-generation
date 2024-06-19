package org.sweattypalms.dungeon_generation;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.sweattypalms.dungeon_generation.components.RoomCell;
import org.sweattypalms.dungeon_generation.components.ShapeCell;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DungeonApp extends Application {
    public static final int GRID_SIZE = 6;
    public static final int CELL_SIZE = 100;
    public static final int CONNECTION_THICKNESS = 50;
    public static final int PADDING = 10;
    public static final int GAP = 20;
    private TextField seedInput;
    private DungeonGenerator generator;
    private Pane mainPane;

    @Override
    public void start(Stage primaryStage) {
        generator = new DungeonGenerator();
        mainPane = new Pane();
        seedInput = new TextField();

        mainPane.setMouseTransparent(true);


        StackPane rootPane = new StackPane();
        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(mainPane, createControlPanel());
        rootPane.getChildren().addAll(mainLayout);
        rootPane.setStyle("-fx-background-color: #2c2f33;");

        mainPane.setPrefHeight(GRID_SIZE * (CELL_SIZE + GAP));
        mainPane.setPrefWidth(GRID_SIZE * (CELL_SIZE + GAP));

        generateDungeon();

        int SCREEN_RES = GRID_SIZE * (CELL_SIZE + GAP) + GAP + PADDING;
        Scene scene = new Scene(rootPane, SCREEN_RES, SCREEN_RES + 45);  // Set initial size for visibility
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dungeon Generator");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    private HBox createControlPanel() {
        Button regenerateButton = new Button("Regenerate");
        regenerateButton.setOnAction(_ -> generateDungeon());
        regenerateButton.setStyle("-fx-background-color: #7289da; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5;");

        Button infiniteGenButton = getInfiniteGenButton();

        seedInput.setPromptText("Seed");
        seedInput.setStyle("-fx-background-color: #23272a; -fx-text-fill: white; -fx-prompt-text-fill: white; -fx-border-color: white; -fx-border-radius: 5;");

        HBox controlPanel = new HBox(10, seedInput, regenerateButton, infiniteGenButton);
        controlPanel.setStyle("-fx-padding: 10px; -fx-alignment: center; -fx-background-color: #23272a;");
        return controlPanel;
    }

    private Button getInfiniteGenButton() {
        Button infiniteGenButton = new Button("Infinite Gen");
        infiniteGenButton.setOnAction(_ -> {
            // start a timer that regenerates the dungeon every second
            AtomicInteger counter = new AtomicInteger(0);
            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(new javafx.animation.KeyFrame(javafx.util.Duration.millis(1), event -> {
                seedInput.setText(String.valueOf(counter.getAndIncrement()));
                try {
                    generateDungeon();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    timeline.stop();
                    System.out.println("Error at " + counter.get() + " dungeons");
                }

                if (counter.get() % 100 == 0) {
                    System.out.println("Generated " + counter.get() + " dungeons");
                }
            }));

            timeline.play();
        });
        return infiniteGenButton;
    }

    private void generateDungeon() {
        long seed;
        try {
            if (seedInput.getText().isEmpty()) throw new NumberFormatException();
            seed = seedInput.getText().hashCode();
        } catch (NumberFormatException e) {
            seed = new Random().nextLong();
        }

        generator.generate(seed);
        Room[][] dungeonGrid = generator.getDungeonGrid();

        mainPane.getChildren().clear();

        mainPane.setPadding(new Insets(PADDING));

        Set<RoomShapeHandler> shapeHandlers = new HashSet<>(generator.getShapeHandlers().values());
        shapeHandlers.removeIf(shapeHandler -> shapeHandler.getShape().isAvoid());

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Room room = dungeonGrid[j][i];
                RoomShapeHandler shapeHandler = generator.getShapeHandlers().get(room);
                if (shapeHandler != null && !shapeHandler.getShape().isAvoid()) {
                    continue;
                }

                RoomCell cell = new RoomCell(room, CELL_SIZE);
                mainPane.getChildren().add(cell);
            }
        }

        shapeHandlers.forEach(shapeHandler -> {
            ShapeCell cell = new ShapeCell(shapeHandler);
            mainPane.getChildren().add(cell);

            if (shapeHandler.getRooms().stream().noneMatch(r -> r.getType() == RoomType.TEST)) {
                return;
            }

            List<Room> rooms = shapeHandler.getRooms().stream().filter(r -> r.getType() == RoomType.TEST).toList();

            rooms.forEach(r -> {
                RoomCell roomCell = new RoomCell(r, CELL_SIZE);
                mainPane.getChildren().add(roomCell);
            });
        });

        drawConnections();
    }

    private void drawConnections() {
        Room[][] dungeonGrid = generator.getDungeonGrid();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                Room room = dungeonGrid[j][i];
                for (Room connectedRoom : room.getConnectedRooms()) {
                    boolean isWitherPath = this.generator.getMainPath().contains(room) && this.generator.getMainPath().contains(connectedRoom);
                    Rectangle connection = createConnectionRectangle(room, connectedRoom, isWitherPath);
                    mainPane.getChildren().add(connection);
                }
            }
        }
    }

    private Rectangle createConnectionRectangle(Room room1, Room room2, boolean isWitherPath) {
        double d = (CELL_SIZE + GAP); // Distance between cells (cell size + gap)

        double xA = room1.getCoordinates().y() * d + PADDING;
        double yA = room1.getCoordinates().x() * d + PADDING;

        double xB = room2.getCoordinates().y() * d + PADDING;
        double yB = room2.getCoordinates().x() * d + PADDING;

        if (xA != xB && yA != yB) {
            return null;
        }

        double xS, yS;
        double width = CONNECTION_THICKNESS, height = CONNECTION_THICKNESS;

        if (xA == xB) {
            // Vertical connection
            xS = xA + CELL_SIZE / 2.0 - width / 2.;
            yS = Math.min(yA, yB) + CELL_SIZE;
            height = GAP;
        } else {
            // Horizontal connection
            xS = Math.min(xA, xB) + CELL_SIZE;
            yS = yA + CELL_SIZE / 2.0 - height / 2.;
            width = GAP;
        }
        Rectangle connection = new Rectangle(xS, yS, width, height);

        if (isWitherPath) {
            connection.setFill(Color.BLACK);
        } else {
            connection.setFill(room1.getType().getColor());
        }

        return connection;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
