package test_utils.data;

import engine.game.GridPosition;
import engine.game.Position;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SingleLine implements TestMapData {
    public final static String path = "resources/testmaps/singleline.map";
    private String map;

    public SingleLine() throws IOException {
        map = Files.readString(Path.of(path));
    }

    public String path() {
        return path;
    }

    public String map() {
        return map;
    }

    public Position getNextTowerPlaceablePosition() {
        return new GridPosition(10, 13);
    }

    public Position getNextForestPosition() {
        return new GridPosition(0, 0);
    }

    public Position topRightCorner() {
        return new GridPosition(15, 0);
    }
}
