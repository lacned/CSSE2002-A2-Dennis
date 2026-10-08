package towers.enemies;

import engine.game.Direction;
import engine.game.Position;
import engine.game.Removable;
import engine.pathing.PathFinder;

/**
 * Something that walks towards {@link towers.tiles.Home} and that the player must destroy
 * before it arrives.
 */
public interface Enemy extends Position, Removable {
    /**
     * Give this enemy the {@link PathFinder} it consults to choose its {@link Direction}.
     *
     * <p>An enemy computes no route itself, so replacing the path finder redirects it.
     *
     * @param route The path finder this enemy should use.
     * @ensures The enemy follows the given route from its next tick onwards.
     */
    void setRoute(PathFinder route);
}
