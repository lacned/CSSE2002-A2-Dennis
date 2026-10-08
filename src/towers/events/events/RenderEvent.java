package towers.events.events;

import engine.EngineState;
import engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.List;

public class RenderEvent extends Event {
    final List<Renderable> renderables = new ArrayList<>();

    public RenderEvent(EngineState engine) {
        super(engine);
    }

    public void render(Renderable renderable) {
        renderables.add(renderable);
    }

    public List<Renderable> getRenderables() {
        return renderables;
    }
}
