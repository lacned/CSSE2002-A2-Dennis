package test_utils.mocks;

import engine.art.sprites.Sprite;
import engine.game.GridPosition;
import engine.game.Position;
import engine.game.ScreenPosition;
import engine.pathing.PathFinder;
import engine.renderer.Renderable;
import towers.SpriteGallery;
import towers.enemies.Enemy;

import java.util.ArrayList;
import java.util.List;

public class MockEnemy implements Enemy, Renderable {
    private final List<MockEnemyAction> actions = new ArrayList<>();
    private boolean removal = false;
    private Position pos;

    public MockEnemy(Position pos) {
        this.pos = pos;
    }

    public int getRenderOrder() {
        return 0;
    }

    @Override
    public void setRoute(PathFinder path) {}

    /**
     * Returns the horizontal (x-axis) coordinate of the component.
     *
     * @return The horizontal (x-axis) coordinate.
     * @ensures \result >= 0
     */
    public int getX() {
        return pos.screen().getX();
    }

    /**
     * Returns the vertical (y-axis) coordinate of the component.
     *
     * @return The vertical (y-axis) coordinate.
     * @ensures \result >= 0
     */
    public int getY() {
        return pos.screen().getY();
    }

    /**
     * @param position
     * @return
     */
    public boolean overlaps(Position position) {
        return false;
    }

    /**
     * Returns the current position converted to represent its current screen position.
     *
     * @return the current position converted to represent its current screen position.
     */
    @Override
    public ScreenPosition screen() {
        return pos.screen();
    }

    /**
     * Returns the current position converted to represent its current grid position.
     *
     * @return the current position converted to represent its current grid position.
     */
    @Override
    public GridPosition grid() {
        return pos.grid();
    }

    /**
     * The sprite to draw to represent the renderable on the screen.
     *
     * @return A {@link Sprite} instance to draw.
     */
    @Override
    public Sprite getSprite() {
        return SpriteGallery.mocks.getSprite("enemy");
    }

    /**
     * Whether this entity is marked for removal.
     *
     * @return True if this entity has been marked for removal.
     */
    @Override
    public boolean isMarkedForRemoval() {
        return removal;
    }

    /**
     * Mark this entity for removal. No action is taken to perform the removal, the owner of the
     * entity must use {@link #isMarkedForRemoval()} to prevent the entity from being rendered in
     * subsequent ticks.
     *
     * @ensures {{@link #isMarkedForRemoval()}} returns true.
     */
    @Override
    public void markForRemoval() {
        removal = true;
    }

    public List<Renderable> render() {
        return List.of(this);
    }

    @Override
    public String getID() {
        return "";
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MockEnemy[\n");
        sb.append(" id:(" + getID() + ")\n");
        sb.append(" Position [");
        sb.append(screen() +", "+ grid());
        sb.append("]\n");
        sb.append(" isMarkedForRemoval:" + removal);
        sb.append(" Mock Only Props: \n");
        sb.append("]");
        return sb.toString();
    }

    public boolean assertActionTaken(MockEnemyAction action) {
        return actions.contains(action);
    }

    public int assertActionTakenTimes(MockEnemyAction action) {
        return Math.toIntExact(actions.stream()
                .filter((a -> a == action))
                .count());
    }
}
