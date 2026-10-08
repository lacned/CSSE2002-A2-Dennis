package test_utils.data;

import engine.game.GridPosition;
import engine.game.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VortexLose extends BaseMapData implements TestMapData {
    private final List<GridPosition> towerPositions = new ArrayList<>();
    private int index = 0;

    public VortexLose() throws IOException {
        super("resources/testmaps/vortexLose.map");

        towerPositions.add(new GridPosition(4, 12));
        towerPositions.add(new GridPosition(6, 12));
        towerPositions.add(new GridPosition(6, 14));
        towerPositions.add(new GridPosition(10, 12));
        towerPositions.add(new GridPosition(10, 14));
    }

    public Position getNextTowerPlaceablePosition() {
        Position result = towerPositions.get(index);
        index += 1;
        if (index >= towerPositions.size()) {
            index = 0;
        }
        return result;
    }

    public Position getNextForestPosition() {
        return null;
    }

    public Position topRightCorner() {
        return new GridPosition(15, 0);
    }
}
