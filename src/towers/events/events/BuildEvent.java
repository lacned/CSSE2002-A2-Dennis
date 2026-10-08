package towers.events.events;

import engine.EngineState;
import engine.game.Position;

public class BuildEvent extends Event {
    public BuildEvent(EngineState engine, Position pos) {
        super(engine);
    }
}
