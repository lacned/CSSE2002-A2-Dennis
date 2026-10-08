package test_utils.data;

import engine.game.GridPosition;
import engine.game.Position;

import java.io.IOException;

public class SimpleVortex extends BaseMapData implements TestMapData {
    public SimpleVortex() throws IOException {
        super("resources/testmaps/simpleVortexTest.map");
    }

    public GridPosition getNextTowerPlaceablePosition() {
        return new GridPosition(10, 9);
    }

    public Position getNextForestPosition() {
        return null;
    }

    public Position topRightCorner() {
        return new GridPosition(15, 0);
    }
}