package test_utils.data;

import engine.game.GridPosition;
import engine.game.Position;

import java.util.ArrayList;
import java.util.List;

public class Gauntlet {
    public String path = "resources/testmaps/gauntlet.map";
    private List<GridPosition> towerPositions;
    private int index = 0;

    public Gauntlet() {
        towerPositions = new ArrayList<>();
        towerPositions.add(new GridPosition(6, 11));
        towerPositions.add(new GridPosition(2, 9));
        towerPositions.add(new GridPosition(2, 10));
        towerPositions.add(new GridPosition(6, 12));
        towerPositions.add(new GridPosition(6, 13));
        towerPositions.add(new GridPosition(6, 14));
        towerPositions.add(new GridPosition(7, 14));
        towerPositions.add(new GridPosition(8, 14));
        towerPositions.add(new GridPosition(8, 15));
        towerPositions.add(new GridPosition(8, 11));
        towerPositions.add(new GridPosition(9, 11));
        towerPositions.add(new GridPosition(10, 11));
    }

    public Position getNextTowerPlaceablePosition() {
        final Position result = towerPositions.get(index);
        index += 1;
        if (index >= towerPositions.size()) {
            index = 0;
        }
        return result;
    }

    public Position getNextForestPosition() {
        return new GridPosition(0, 0);
    }

    public static Position topRightCorner() {
        return new GridPosition(15, 0);
    }
}
