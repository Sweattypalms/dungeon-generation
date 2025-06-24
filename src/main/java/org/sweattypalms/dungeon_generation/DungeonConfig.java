package org.sweattypalms.dungeon_generation;

import lombok.Getter;

import java.util.Map;
import java.util.HashMap;

@Getter
public class DungeonConfig {
    @Getter
    private static final DungeonConfig instance = new DungeonConfig();

    // To change the grid size
    // e.g. 4x4 = Entrance
    //      6x6 = Floor 7
    private final int gridSize = 6;

    // The special rooms types and their number requirements
    // e.g.
    // - Floor 7:
    // 1 Yellow (mini-boss room), 1 Orange (maze), 3 Purple (puzzle)

    private final Map<RoomType, Integer> specialRoomRequirements  = new HashMap<>(
            Map.of(
                    RoomType.YELLOW, 1,
                    RoomType.ORANGE, 1,
                    RoomType.PURPLE, 3
            )
    );

    private DungeonConfig() {}
}
