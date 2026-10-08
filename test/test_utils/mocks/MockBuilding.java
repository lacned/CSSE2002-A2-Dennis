package test_utils.mocks;

import engine.EngineState;
import engine.art.sprites.Sprite;
import engine.game.Position;
import engine.game.ScreenPosition;
import engine.renderer.Renderable;
import towers.SpriteGallery;
import towers.buildings.Building;

import java.util.List;

public class MockBuilding implements Building, Renderable {
    private boolean removal = false;
    private int tickCallCount = 0;
    private int lastTickTickCalled = -1;
    private int gameOverCallCount = 0;
    private int gameCallCount = 0;
    private int lastTickInteractCalled = -1;
    private ScreenPosition position = new ScreenPosition(0, 0);

    public MockBuilding() {
    }

    public int getRenderOrder() {
        return 0;
    }

    public MockBuilding(Position pos) {
        position = pos.screen();
    }

    public int getTickCallCount() {
        return tickCallCount;
    }

    public int getLastTickTickCalled() {
        return lastTickTickCalled;
    }

    public int getGameCallCount() {
        return gameCallCount;
    }

    public int getLastTickInteractCalled() {
        return lastTickInteractCalled;
    }

    public int getSetGameOverCallCount() {
        return gameOverCallCount;
    }

    @Override
    public boolean isMarkedForRemoval() {
        return removal;
    }

    @Override
    public void markForRemoval() {
        removal = true;
    }

    @Override
    public ScreenPosition screen() {
        return position.screen();
    }

    /**
     * The sprite to draw to represent the renderable on the screen.
     *
     * @return A {@link Sprite} instance to draw.
     */
    @Override
    public Sprite getSprite() {
        return SpriteGallery.mocks.getSprite("building");
    }

    @Override
    public String getID() {
        return "";
    }

    public List<Renderable> render() {
        return List.of(this);
    }

}
