package test_utils.data;

import engine.game.GridPosition;
import engine.game.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VortexWin extends BaseMapData implements TestMapData {
    private final List<GridPosition> towerPositions = new ArrayList<>();
    private int index = 0;

    public VortexWin() throws IOException {
        super("resources/testmaps/vortexWin.map");
        towerPositions.add(new GridPosition(1, 8));
        towerPositions.add(new GridPosition(1, 5));
        towerPositions.add(new GridPosition(11, 4));
        towerPositions.add(new GridPosition(1, 2));
        towerPositions.add(new GridPosition(5, 4));
        towerPositions.add(new GridPosition(9, 1));
        towerPositions.add(new GridPosition(8, 13));
        towerPositions.add(new GridPosition(14, 10));
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
