package towers.events;

import engine.EngineState;
import engine.game.Position;
import engine.renderer.Renderable;
import engine.renderer.RenderableGroup;
import towers.enemies.Enemy;
import towers.events.events.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles processing our {@link Event}s and given listeners from the {@link ListenerManager}.
 */
public class EventsManager implements RenderableGroup {
    private List<Renderable> renderables = new ArrayList<>();
    private final List<Enemy> enemiesThatMoved = new ArrayList<>();
    private final List<Position> requestedEnemySpawns = new ArrayList<>();
    private final List<Position> requestedBuild = new ArrayList<>();
    private boolean endGame = false;

    public void endGame() {
        endGame = true;
    }

    public void addBuildRequest(Position position) {
        requestedBuild.add(position);
    }

    public void addKnightSpawn(Position position) {
        requestedEnemySpawns.add(position);
    }

    public void addEnemyMove(Enemy enemy) {
        enemiesThatMoved.add(enemy);
    }

    public void processTick(EngineState engine, Listener<Event> listener) {
        final Event event = new Event(engine);
        listener.act(event);
    }

    public void processEndGame(EngineState engine, List<Listener<Event>> listeners) {
        if(!endGame) {
            return;
        }
        final Event endGameEvent = new Event(engine);
        for (Listener<Event> listener : listeners) {
            listener.act(endGameEvent);
        }
    }

    public void processTowerBuild(EngineState engine, List<Listener<BuildEvent>> listeners) {
        for (Position pos : requestedBuild) {
            final BuildEvent event = new BuildEvent(engine, pos);
            for (Listener<BuildEvent> listener : listeners) {
                listener.act(event);
            }
        }
    }

    public void processEnemySpawn(EngineState engine, List<Listener<SpawnEvent>> listeners) {
        for (Position pos : requestedEnemySpawns) {
            final SpawnEvent event = new SpawnEvent(engine, pos);
            for (Listener<SpawnEvent> listener : listeners) {
                listener.act(event);
            }
        }
    }

    public void processEnemyMove(EngineState engine, Listener<EnemyMove> listener) {
        for (Enemy enemyThatMoved : enemiesThatMoved) {
            listener.act(new EnemyMove(engine, enemyThatMoved));
        }
    }

    public void processRender(EngineState engine, List<Listener<RenderEvent>> listeners) {
        final RenderEvent event = new RenderEvent(engine);
        for (Listener<RenderEvent> listener : listeners) {
            listener.act(event);
        }
        renderables = new ArrayList<>(event.getRenderables());
    }

    public void processCleanup(EngineState engine, Listener<Event> listener) {
        listener.act(new Event(engine));
    }

    public void cleanup() {
        requestedEnemySpawns.clear();
        requestedBuild.clear();
        enemiesThatMoved.clear();
    }

    /**
     * Returns a list of all renderables for the current state
     */
    @Override
    public List<Renderable> render() {
        return renderables;
    }
}
