package towers.events.events;

import engine.EngineState;
import towers.enemies.Enemy;

public class EnemyMove extends Event {
    private final Enemy enemy;

    public EnemyMove(EngineState engine, Enemy enemy) {
        super(engine);
        this.enemy = enemy;
    }

    public Enemy getEnemy() {
        return this.enemy;
    }
}
