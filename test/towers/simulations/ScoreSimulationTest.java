package towers.simulations;

import engine.art.sprites.Sprite;
import engine.game.Position;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test_utils.Simulation;
import test_utils.analysers.AnalyserManager;
import test_utils.analysers.RenderableAnalyser;
import test_utils.data.SingleLine;
import test_utils.mocks.MockKeys;
import test_utils.mocks.MockMouse;
import towers.FireWall;
import towers.SpriteGallery;
import towers.events.EventsManager;
import towers.events.ListenerManager;
import towers.world.WorldLoadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoreSimulationTest {
    private static final int TICKS = 1100;
    private static AnalyserManager timeline = new AnalyserManager();
    private static SingleLine data;

    @AfterClass
    public static void tearDown() {
        timeline = null;
        data = null;
    }

    //For the score sim tests we will place ONE tower but otherwise just let the sim run.
    @BeforeClass
    public static void setUp() throws WorldLoadException, IOException {
        data = new SingleLine();
        final ListenerManager listeners = new ListenerManager(new EventsManager());
        final FireWall sanctum = new FireWall(data.map(), listeners);
        final Simulation sim = new Simulation(listeners, TICKS);
        final Position forestPos = data.getNextForestPosition();
        final Position towerPos = data.getNextTowerPlaceablePosition();
        sim.setDefaultMouseState(new MockMouse(forestPos));
        sim.setDefaultKeyState(new MockKeys(new ArrayList<>()));
        sim.setFrameSpecificMouse(10, new MockMouse(towerPos, true));
        sim.enableHeadless();
        sim.setTicks(TICKS);
        timeline = sim.run();
    }

    //Confirm it was only ever really around the top right corner
    @Test
    public void scoreSpawnsAtTopRightCorner() {
        final RenderableAnalyser score = timeline.mergeBySpriteGroup("letter");
        Assert.assertTrue(
                prefix() + "score should be spawned in the top right corner of the screen!",
                score.spawnPosition().grid().overlaps(data.topRightCorner())
        );
    }

    @Test
    public void scoreIncreasesOverTime() {
        final Sprite fourSprite = SpriteGallery.letters.getSprite("4");
        final Sprite threeSprite = SpriteGallery.letters.getSprite("3");
        final Sprite fiveSprite = SpriteGallery.letters.getSprite("5");
        final Sprite sixSprite = SpriteGallery.letters.getSprite("6");
        final RenderableAnalyser score = timeline.mergeBySpriteGroup("letter");

        final List<Sprite> sprites = score.allUniqueSprites();

        Assert.assertEquals(
                prefix() + "over " + TICKS + " expect to see 3 unique sprites for Score",
                3, sprites.size()
        );
        Assert.assertTrue(
                prefix() + "Score should had the sprite for 4",
                score.hasSprite(fourSprite)
        );
        Assert.assertTrue(
                prefix() + "Score should have gone down to sprite for 3",
                score.hasSprite(threeSprite)
        );
        Assert.assertTrue(
                prefix() + "Score should have progressed to sprite for 5",
                score.hasSprite(fiveSprite)
        );
        Assert.assertFalse(
                prefix() + "Score should have NOT progressed to sprite for 6",
                score.hasSprite(sixSprite)
        );
    }

    private String prefix() {
        return "SIM:" + data.path + ": ";
    }
}
