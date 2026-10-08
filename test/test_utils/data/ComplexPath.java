package test_utils.data;

import engine.game.GridPosition;
import engine.game.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComplexPath extends BaseMapData implements TestMapData {
    public ComplexPath() throws IOException {
        super("resources/testmaps/complexPath.map");
    }
}
