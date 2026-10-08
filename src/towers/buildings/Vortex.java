package towers.buildings;

import engine.game.Position;
import engine.timing.TickTimer;
import engine.timing.RepeatingTimer;
import engine.timing.FixedTimer;
import towers.SpriteGallery;
import engine.art.sprites.SpriteGroup;
import towers.enemies.Enemy;
import towers.events.ListenerManager;
import towers.events.events.EnemyMove;
import towers.events.events.Event;

public class Vortex extends Bomb {
    public final static int RANGE = 200;
    public final static int ANIMATION_INTERVAL = 8;
    public final static int LIFESPAN = 100;
    private final TickTimer timer = new RepeatingTimer(ANIMATION_INTERVAL);
    private final TickTimer lifespan = new FixedTimer(LIFESPAN);
    private final static SpriteGroup art = SpriteGallery.vortex;
    private int index = 0;
    private final int finalAnimIndex;
    private final ListenerManager listen;

    public Vortex(Position pos, ListenerManager listen) {
        super(pos, pos, listen);
        finalAnimIndex = art.getSprites().size() - 1;
        setSprite(art.getSprite("0"));
        setRenderOrder(2);
        this.listen = listen;
    }

    private void nextAnimIndex() {
        index += 1;
        if (index > finalAnimIndex) {
            markForRemoval();
            index = finalAnimIndex;
        }
    }

    private void updateSprite() {
        nextAnimIndex();
        setSprite(art.getSprite(index + ""));
    }

    @Override
    public void enemyMove(EnemyMove event) {
        final Enemy enemy = event.getEnemy();
        if (enemy.screen().distance(this) <= RANGE) {
            enemy.markForRemoval();
            new VisualEffect(enemy, SpriteGallery.explosion, this.listen);
        }
    }

    @Override
    public void tick(Event event) {
        timer.tick();
        if (timer.isFinished()) {
            updateSprite();
        }
        if (isOutOfBounds() || lifespan.isFinished()) {
            markForRemoval();
        }
    }
}