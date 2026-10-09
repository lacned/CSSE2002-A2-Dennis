package towers.buildings;

import engine.art.sprites.SpriteGroup;
import engine.game.Position;
import engine.timing.FixedTimer;
import engine.timing.TickTimer;

import towers.GameEntity;
import towers.SpriteGallery;

import towers.enemies.Enemy;
import towers.events.ListenerManager;
import towers.events.events.RenderEvent;
import towers.events.events.EnemyMove;
import towers.events.events.Event;


/**
 * A {@link Building} that shoots {@link Bomb}s at nearby enemies, alternating between loaded, where
 * it may shoot, and reloading, where it may not.
 */
public class Tower extends GameEntity implements Building {
    private static final SpriteGroup art = SpriteGallery.tower;
    private boolean disabled = false;

    public static final int COST = 1;
    public static final int VORTEX_COST = 2;
    private int fireRate;

    private final ListenerManager listen;
    public static final int TOWER = 0;
    public static final int VORTEX_TOWER = 1;

    private boolean loaded = true;
    private TickTimer timer;
    private Integer type;

    /**
     * Construct a tower at the given {@link Position}, loaded and ready to fire.
     *
     * <p>Shows the "loaded" sprite from {@link SpriteGallery#tower}, starts a {@link TickTimer}
     * with 200 duration, and raises the render order to 1 so the tower draws above the tile it
     * sits on. The {@link TickTimer} should be a {@link FixedTimer} so that the tower does not
     * continuously reload while waiting to fire.
     *
     * @param pos The position to build at, normally that of the tile being built on.
     * @ensures The tower is loaded and owns no bombs.
     */
    public Tower(Position pos, ListenerManager listen) {
        super(pos);
        setSprite(art.getSprite("loaded"));
        type = TOWER;
        this.fireRate = 200;
        resetTimer();
        setRenderOrder(1);
        trackListener(listen.forTick(this::tick));
        trackListener(listen.forRender(this::render));
        trackListener(listen.forEnemyMove(this::enemyMove));
        trackListener(listen.forEndGame(this::end));
        this.listen = listen; //we need to remember listen, so we can give it to our bombs later
    }

    public Tower(Position pos, ListenerManager listen, Integer type) {
        super(pos);
        setRenderOrder(1);
        assert type == TOWER || type == VORTEX_TOWER;
        if (type == TOWER) {
            setSprite(art.getSprite("loaded"));
            this.fireRate = 200;
            resetTimer();
        } else {
            setSprite(art.getSprite("vortex"));
            this.fireRate = 1800;
            resetTimer();
        }
        this.type = type;
        timer = new FixedTimer(this.fireRate);
        trackListener(listen.forTick(this::tick));
        trackListener(listen.forRender(this::render));
        trackListener(listen.forEnemyMove(this::enemyMove));
        trackListener(listen.forEndGame(this::end));
        this.listen = listen;
    }

    private void end(Event event) {
        disabled = true;
        setSprite(art.getSprite("gameover"));
    }

    /**
     * Whether the given {@link Position} is close enough for this tower to shoot.
     *
     * @param pos The position to test.
     * @return True if the position is within this tower's range.
     */
    private boolean isInRange(Position pos) {
        return pos.screen().distance(this) <= 200;
    }

    private void resetTimer() {
        timer = new FixedTimer(this.fireRate);
    }

    private void tick(Event event) {
        if (disabled) {
            return;
        }
        if (loaded && type == TOWER) {
            setSprite(art.getSprite("loaded"));
        } else if (loaded && type == VORTEX_TOWER) {
            setSprite(art.getSprite("vortex"));
        } else {
            //do nothing
        }
        timer.tick();
        if (timer.isFinished()) {
            loaded = true;
            resetTimer();
        }
    }

    private void enemyMove(EnemyMove event) {
        if (loaded && !disabled) {
            switch (type) {
                case TOWER -> onMoveTower(event);
                case VORTEX_TOWER -> onMoveVortexTower(event);
                case null, default -> {
                    //do nothing
                }
            }
        }
    }

    private void onMoveTower(EnemyMove event) {
        final Enemy enemy = event.getEnemy();
        if (!isInRange(enemy)) {
            return;
        }
        new Bomb(this, enemy, listen);
        beginReload();
    }

    private void onMoveVortexTower(EnemyMove event) {
        final Enemy enemy = event.getEnemy();
        if (!isInRange(enemy)) {
            return;
        }
        new Vortex(this, listen);
        beginReload();
    }


    private void beginReload() {
        if (type == TOWER) {
            setSprite(art.getSprite("reloading"));
        } else if (type == VORTEX_TOWER) {
            setSprite(art.getSprite("reloadingvortex"));
        }
        loaded = false;
        resetTimer();
    }

    private void render(RenderEvent event) {
        event.render(this);
    }
}
