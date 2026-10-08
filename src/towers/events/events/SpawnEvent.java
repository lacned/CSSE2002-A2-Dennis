package towers.events.events;

import engine.EngineState;
import engine.game.Position;

public class SpawnEvent extends Event {
    Position pos;
    public SpawnEvent(EngineState engine, Position pos) {
        super(engine);
        this.pos = pos;
    }
    public Position getPosition() {
        return pos;
    }
}
