package towers;

import engine.Engine;
import engine.game.EventDrivenGame;
import engine.game.Game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import towers.events.EventsManager;
import towers.events.ListenerManager;
import towers.tiles.*;
import towers.world.WorldLoadException;

/**
 * A main class to run the FireWall game.
 */
public class Main {
    /**
     * Start the game.
     *
     * @param args Command line arguments, unused in this method.
     * @throws IOException        If the map file cannot be found or read from.
     * @throws WorldLoadException If the map file is incorrectly formatted.
     */
    public static void main(String[] args) throws IOException, WorldLoadException {
        String map = "";
        if (args.length > 0) {
            map = Files.readString(Path.of(args[0]));
        }

        final ListenerManager listeners = new ListenerManager(new EventsManager());
        final FireWall sanctum = new FireWall(map, listeners);

        // We pass the ListenerManager to the engine, registering all interactions
        final Engine unreal = new Engine(listeners);

        // The Engine includes helpful debugging features that
        // can be enabled by uncommenting the lines below.
        Engine.debug().on();
        // See the Javadoc for what each debugging feature does
        Engine.debug().enableOverlaps();
        Engine.debug().enableDistance();
        Engine.debug().enableAdjacent();
        Engine.debug().enableTextPanel();
        Engine.debug().enablePath();

        run(unreal);
    }

    /**
     * Helper method to run the game loop.
     *
     * @param engine The {@link Engine} instance to execute.
     */
    private static void run(Engine engine) {
        if (engine == null) {
            return;
        }
        while (engine.isRunning()) {
            if (engine.isTimeForNextTick()) {
                engine.tick();
            }
        }
    }
}
