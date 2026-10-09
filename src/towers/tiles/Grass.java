package towers.tiles;

import engine.art.sprites.SpriteGroup;
import engine.game.Position;
import engine.input.MouseState;
import towers.SpriteGallery;
import towers.buildings.Tower;
import towers.events.ListenerManager;
import towers.events.events.BuildEvent;
import towers.events.events.Event;


/**
 * A grass tile, representing ground that enemies can path through.
 * A {@link Tower} can be built on a grass tile by clicking it, which then blocks that path.
 *
 * <p>A grass tile is displayed as the "default" identifier in {@link SpriteGallery#grass}.
 *
 * @invariant A grass tile holds at most one tower.
 */
public class Grass extends AbstractTile {
    private static final SpriteGroup art = SpriteGallery.grass;
    private ListenerManager listen;

    /**
     * Construct a grass tile at the given position.
     *
     * @param position The position to place the tile.
     * @ensures {@link #getBuildings()} is empty.
     */
    public Grass(Position position, ListenerManager listen) {
        super(position, listen);
        setSprite(art.getSprite("default"));
        listen.forBuild(this::build);
        listen.forEndGame(this::end);
        this.listen = listen;
    }

    private void build(BuildEvent event) {
        final MouseState mouse = event.getEngine().getMouse();
        if (
                mouse.isLeftPressed()
                        && getBuildings().isEmpty()
                        && mouse.screenPosition().grid().overlaps(this)
        ) {
            addBuilding(new Tower(this, listen, Tower.TOWER));
        } else if (
                mouse.isRightPressed()
                        && getBuildings().isEmpty()
                        && mouse.screenPosition().grid().overlaps(this)
        ) {
            addBuilding(new Tower(this, listen, Tower.VORTEX_TOWER));
        } else {
            //do nothing
        }
    }

    private void end(Event event) {
        setSprite(art.getSprite("gameover"));
    }
}