package towers.events;

import engine.EngineState;
import engine.game.Game;
import engine.game.Position;
import engine.renderer.Renderable;
import towers.enemies.Enemy;
import towers.events.events.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Responsible for coordinating listeners being set up for events.
 * Provides our 'game' method hookds for the Engine to use to drive the system forward.
 */
public class ListenerManager implements Game {
    private final EventsManager events;
    private final List<Listener<Event>> ticks = new ArrayList<>();
    private final List<Listener<Event>> cleanups = new ArrayList<>();
    private final List<Listener<EnemyMove>> enemyMoves = new ArrayList<>();
    private final List<Listener<SpawnEvent>> enemySpawns = new ArrayList<>();
    private final List<Listener<BuildEvent>> build = new ArrayList<>();
    private final List<Listener<RenderEvent>> renders = new ArrayList<>();
    private final List<Listener<Event>> endGame = new ArrayList<>();

    public ListenerManager(EventsManager events) {
        this.events = events;
    }

    public void tick(EngineState engine) {
        cleanup(engine);
        events.cleanup();

        for (int i = ticks.size() - 1; i >= 0; i--) {
            events.processTick(engine, ticks.get(i));
        }

        for (int i = enemyMoves.size() - 1; i >= 0; i--) {
            events.processEnemyMove(engine, enemyMoves.get(i));
        }

        events.processEnemySpawn(engine, enemySpawns);

        events.processTowerBuild(engine, build);

        events.processEndGame(engine, endGame);

        for (int i = cleanups.size() - 1; i >= 0; i--) {
            events.processCleanup(engine, cleanups.get(i));
        }

        events.processRender(engine, renders);

    }

    /**
     * A list of renderables to draw to the screen. Immediately after {@link #tick(EngineState)}
     * occurs, the game is re-rendered using the return values of this method.
     *
     * @return A list of renderables to draw.
     */
    @Override
    public List<Renderable> render() {
        return events.render();
    }

    /**
     * Queues up a Build event to be emitted at the given Position
     *
     * @param pos
     */
    public void emitBuildRequest(Position pos) {
        events.addBuildRequest(pos);
    }

    public void emitSpawnEnemy(Position pos) {
        events.addKnightSpawn(pos);
    }

    public void emitEnemyMove(Enemy enemy) {
        events.addEnemyMove(enemy);
    }

    public void emitEndGame() {
        events.endGame();
    }

    private <T extends Event> Listener<T> forListenerType(Consumer<T> action, List<Listener<T>> record) {
        final Listener<T> listener = new Listener<>(action);
        record.add(listener);
        return listener;
    }

    public Listener<Event> forTick(Consumer<Event> action) {
        return forListenerType(action, ticks);
    }

    public Listener<BuildEvent> forBuild(Consumer<BuildEvent> action) {
        return forListenerType(action, build);
    }

    public Listener<SpawnEvent> forEnemySpawn(Consumer<SpawnEvent> action) {
        return forListenerType(action, enemySpawns);
    }

    public Listener<EnemyMove> forEnemyMove(Consumer<EnemyMove> action) {
        return forListenerType(action, enemyMoves);
    }

    public Listener<Event> forEndGame(Consumer<Event> action) {
        return forListenerType(action, endGame);
    }

    public Listener<Event> forCleanup(Consumer<Event> action) {
        return forListenerType(action, cleanups);
    }

    public Listener<RenderEvent> forRender(Consumer<RenderEvent> action) {
        return forListenerType(action, renders);
    }

    //clears up internal listeners for any marked for removal
    private void cleanup(EngineState engine) {
        ticks.removeIf(Listener::isMarkedForRemoval);
        enemyMoves.removeIf(Listener::isMarkedForRemoval);
        build.removeIf(Listener::isMarkedForRemoval);
        enemySpawns.removeIf(Listener::isMarkedForRemoval);
        cleanups.removeIf(Listener::isMarkedForRemoval);
        renders.removeIf(Listener::isMarkedForRemoval);
    }
}
