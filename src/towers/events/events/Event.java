package towers.events.events;

import engine.EngineState;

public class Event {
    private final EngineState engine;

    public Event(EngineState engine) {
        this.engine = engine;
    }

    public EngineState getEngine() {
        return engine;
    }
}
