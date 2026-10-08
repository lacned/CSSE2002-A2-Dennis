package test_utils.data;

import engine.game.GridPosition;
import engine.game.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PathStress extends BaseMapData implements TestMapData {
    private final List<GridPosition> towerPositions = new ArrayList<>();
    private int index = 0;

    public PathStress() throws IOException {
        super("resources/testmaps/pathStress.map");
        towerPositions.add(new GridPosition(12, 11));
        towerPositions.add(new GridPosition(4, 12));
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
        return new GridPosition(0, 0);
    }

    public Position topRightCorner() {
        return new GridPosition(15, 0);
    }
}
