package towers.enemies;

import engine.art.sprites.SpriteGroup;
import engine.game.Direction;
import engine.game.Position;
import engine.pathing.DirectPathFinder;
import engine.pathing.PathFinder;
import towers.GameEntity;
import towers.SpriteGallery;
import towers.events.ListenerManager;
import towers.events.events.RenderEvent;
import towers.events.events.Event;

/**
 * A knight implementation of {@link Enemy}, drawn using
 * {@link SpriteGallery#knight}.
 *
 * <p>An enemy re-asks its {@link PathFinder} for a {@link Direction} every tick rather than
 * following a route computed once, so it reacts to the player walling off its path with a tower.
 *
 * <p>An enemy always has a direction, defaulting to {@link Direction#SOUTH}.
 */
public class Knight extends GameEntity implements Enemy {
    /**
     * How far an enemy moves along its {@link Direction} each tick, in screen pixels.
     */
    public static final int DEFAULT_SPEED = 1;
    public static final int HORSE_SPEED = 2;
    private int speed = DEFAULT_SPEED;
    private static final SpriteGroup art = SpriteGallery.knight;
    private static final SpriteGroup horseArt = SpriteGallery.horse;
    private Direction dir = Direction.SOUTH;
    private PathFinder route;
    private ListenerManager listen;
    private boolean gameOver = false;
    public static final int KNIGHT = 0;
    public static final int HORSE = 1;
    private int type;

    private void setType(Integer type) {
        assert type == KNIGHT || type == HORSE;
        this.type = type;
        if (type == HORSE) {
            speed = HORSE_SPEED;
        }
        updateSprite();
    }

    /**
     * Construct an enemy at the given {@link Position}, following the given route.
     *
     * <p>Starts facing {@link Direction#SOUTH} with the "down" sprite, and raises the render order
     * so the enemy draws above the tiles it walks over.
     *
     * @param pos   The position to spawn at, normally that of a
     *              {@link towers.tiles.Spawner}.
     * @param route The path finder the enemy consults each tick.
     */
    public Knight(Position pos, PathFinder route, ListenerManager listen, Integer type) {
        super(pos);
        setRoute(route);
        setRenderOrder(1);
        this.listen = listen;
        setType(type);
        trackListener(listen.forTick(this::tick));
        trackListener(listen.forRender(this::render));
        trackListener(listen.forEndGame(this::endGame));
        this.listen = listen;
    }

    /**
     * Construct an enemy at the given {@link Position}, following the given route.
     *
     * <p>Starts facing {@link Direction#SOUTH} with the "down" sprite, and raises the render order
     * so the enemy draws above the tiles it walks over.
     *
     * @param pos   The position to spawn at, normally that of a
     *              {@link towers.tiles.Spawner}.
     * @param route The path finder the enemy consults each tick.
     */

    @Override
    public void setRoute(PathFinder route) {
        this.route = route;
    }

    private void endGame(Event event) {
        gameOver = true;
    }

    private boolean doMove = false;

    private void tick(Event event) {
        if (gameOver) {
            return;
        }
        //if (doMove) {
            applySpeed();
            listen.emitEnemyMove(this);
            updateSprite();
        //}
        doMove = !doMove;
        if (isOutOfBounds()) {
            markForRemoval();
        }
    }

    private void updateSprite() {
        String spriteIdentifier = switch (dir) {
            case NORTH, SOUTH -> "down";
            case EAST -> "right";
            case WEST -> "left";
            default -> "down";
        };
        if (type == KNIGHT) {
            setSprite(art.getSprite(spriteIdentifier));
        } else if (type == HORSE) {
            setSprite(horseArt.getSprite(spriteIdentifier));
        }
    }

    private void applySpeed() {
        Direction dir = Direction.SOUTH; //default is south
        if (route != null) {
            dir = route.findPath(this).nextDirection(this);
        }
        //whenever our direction changes we should resnap the enemy to the grid!
        if (dir != this.dir) {
            setPosition(grid());
        }
        this.dir = dir;
        if (type == KNIGHT) {
            setPosition(screen().shift(dir, DEFAULT_SPEED));
        } else if (type == HORSE) {
            setPosition(screen().shift(dir, HORSE_SPEED));
        }
    }

    private void render(RenderEvent event) {
        event.render(this);
    }
}
