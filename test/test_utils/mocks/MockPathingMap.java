package test_utils.mocks;

import engine.Engine;
import engine.game.GridPosition;
import engine.pathing.PathingMap;
import engine.game.Position;
import engine.game.ScreenPosition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockPathingMap implements PathingMap {
    private Position destination = new GridPosition(5, 14).screen();
    private List<ScreenPosition> walkableNodes = new ArrayList<>();

    public MockPathingMap() {
        walkableNodes.add(new GridPosition(5, 0).screen());
        walkableNodes.add(new GridPosition(5, 1).screen());
        walkableNodes.add(new GridPosition(5, 2).screen());
        walkableNodes.add(new GridPosition(5, 3).screen());
        walkableNodes.add(new GridPosition(5, 4).screen());
        walkableNodes.add(new GridPosition(5, 5).screen());
        walkableNodes.add(new GridPosition(5, 6).screen());
        walkableNodes.add(new GridPosition(5, 7).screen());
        walkableNodes.add(new GridPosition(5, 8).screen());
        walkableNodes.add(new GridPosition(5, 9).screen());
        walkableNodes.add(new GridPosition(5, 10).screen());
        walkableNodes.add(new GridPosition(5, 11).screen());
        walkableNodes.add(new GridPosition(5, 12).screen());
        walkableNodes.add(new GridPosition(5, 13).screen());
        walkableNodes.add(new GridPosition(5, 14).screen());
        walkableNodes.add(new GridPosition(5, 14).screen());
    }

    public void setDestination(Position endNode) {
        final int calcedCellSize = Engine.getSpatial().windowSize() / Engine.getSpatial().gridCellSize();
        assert endNode.grid().getX() < Engine.getSpatial().cellsPerRow();
        assert endNode.grid().getY() < Engine.getSpatial().cellsPerRow();
        this.destination = endNode;
    }

    public void setPathableNodes(List<Position> positions) {
        walkableNodes.clear();
        for (Position position : positions) {
            walkableNodes.add(new GridPosition(position).screen());
        }
    }

    /**
     * Return the {@link Position} of the End Node.
     *
     * @return the {@link Position} of the End Node.
     */
    @Override
    public Position getDestination() {
        return destination;
    }

    /**
     * Returns a list of {@link List<ScreenPosition>}s that can be pathed / walked through.
     *
     * @return a list of {@link List<ScreenPosition>}s that can be pathed / walked through.
     */
    @Override
    public List<ScreenPosition> walkablePositions() {
        return walkableNodes;
    }
}
