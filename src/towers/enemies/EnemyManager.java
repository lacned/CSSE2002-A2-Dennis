package towers.enemies;

import engine.game.Position;
import engine.pathing.ShortestGridPathFinder;

import java.util.List;

import towers.SelfCleaningCollection;
import towers.events.ListenerManager;
import towers.events.events.SpawnEvent;
import towers.world.World;

/**
 * Owns every {@link Enemy} alive in the game.
 */
public class EnemyManager {
    private final SelfCleaningCollection<Enemy> enemies;
    private final ListenerManager listen;
    private final World world;

    //we want to count up and every 3rd enemy make it a horse instead of a knight
    private int enemyCounter = 0;

    /**
     * Construct an empty enemy manager, tracking no enemies.
     *
     * @ensures {@link #getEnemies()} is empty.
     */
    public EnemyManager(ListenerManager listen, World world) {
        this.world = world;
        enemies = new SelfCleaningCollection<>(listen);
        listen.forEnemySpawn(this::spawnEnemy);
        this.listen = listen;
    }

    /**
     * Every {@link Enemy} currently managed, as a copy.
     *
     * @return All currently managed enemies.
     * @ensures Modifying \result does not modify this manager.
     */
    public List<Enemy> getEnemies() {
        return enemies.toList();
    }

    private void spawnEnemy(SpawnEvent event) {
        enemyCounter += 1;
        if (enemyCounter >= 4) {
            enemyCounter = 0;
            final Enemy knight = new Knight(event.getPosition(), new ShortestGridPathFinder(world), this.listen, Knight.HORSE);
            this.enemies.add(knight);
        } else {
            final Enemy knight = new Knight(event.getPosition(), new ShortestGridPathFinder(world), this.listen, Knight.KNIGHT);
            this.enemies.add(knight);
        }
    }
}
