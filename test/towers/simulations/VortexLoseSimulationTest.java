package towers.simulations;

import engine.art.sprites.Sprite;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test_utils.Simulation;
import test_utils.analysers.AnalyserManager;
import test_utils.analysers.RenderableAnalyser;
import test_utils.data.VortexLose;
import test_utils.mocks.MockMouse;
import towers.FireWall;
import towers.SpriteGallery;
import towers.events.EventsManager;
import towers.events.ListenerManager;
import towers.world.WorldLoadException;

import java.io.IOException;
import java.util.List;

/**
 * This test will run a map that use of JUST vortex towers should ensure a win on.
 */
public class VortexLoseSimulationTest {
    private static final int TICKS = 4000;
    private static AnalyserManager timeline;
    private static VortexLose data;

    @AfterClass
    public static void tearDown() {
        timeline = null;
        data = null;
    }

    @BeforeClass
    public static void setUp() throws IOException, WorldLoadException {
        data = new VortexLose();
        final ListenerManager listeners = new ListenerManager(new EventsManager());
        final FireWall sanctum = new FireWall(data.map(), listeners);
        final Simulation sim = new Simulation(listeners, TICKS);
        sim.enableHeadless();
        sim.setTicks(TICKS);
        sim.setFps(3000);
        sim.setDefaultMouseState(new MockMouse(
                data.getNextTowerPlaceablePosition(),
                false, true, false
        ));
        for (int i = 0; i < TICKS; i++) {
            final boolean togglingClick = i % 2 == 0;
            final MockMouse mouse = new MockMouse(
                    data.getNextTowerPlaceablePosition(),
                    false, togglingClick, false
            );
            sim.setFrameSpecificMouse(
                    i,
                    mouse
            );
        }
        timeline = sim.run();
    }

    @Test
    public void vortexTowerCount() {
        final Sprite vortexTower = SpriteGallery.tower.getSprite("vortex");
        final List<RenderableAnalyser> vortexTowers = timeline
                .getBySpriteGroup("tower")
                .stream()
                .filter(d -> {
                    return d.hasSprite(vortexTower);
                }).toList();
        final int TARGET = 2;
        Assert.assertEquals(
                prefix() + "should have seen exactly " + TARGET + " vortex towers placed,"
                        + " instead saw " + vortexTowers.size(),
                TARGET, vortexTowers.size()
        );
    }


    @Test
    public void sawVortexTowerSprites() {
        final List<RenderableAnalyser> towers = timeline.getBySpriteGroup("tower");
        final Sprite vortex = SpriteGallery.tower.getSprite("vortex");
        final Sprite reloadingvortex = SpriteGallery.tower.getSprite("reloadingvortex");
        final Sprite endgametower = SpriteGallery.tower.getSprite("gameover");

        final boolean validSpritesForVortexTowerOnly = timeline.every(
                "tower",
                (RenderableAnalyser tower) -> {
                    return tower.hasSprite(vortex)
                            || tower.hasSprite(reloadingvortex)
                            || tower.hasSprite(endgametower);
                });
        Assert.assertTrue(
                prefix() + "All towers constructed should have been Vortex towers " +
                        "using relevant vortex tower sprites, not normal towers",
                validSpritesForVortexTowerOnly
        );
    }

    @Test
    public void sawVortexEffect() {
        final List<RenderableAnalyser> vortexes = timeline.getBySpriteGroup("vortex");
        final int TARGET = 2;
        Assert.assertTrue(
                prefix() + "Should have seen at least "
                        + TARGET
                        + "vortex 'projectiles' instead saw " + vortexes.size(),
                vortexes.size() >= TARGET
        );
    }

    @Test
    public void sawGameEnd() {
        final Sprite home = SpriteGallery.dirt.getSprite("village");
        final Sprite camp = SpriteGallery.dirt.getSprite("camp");
        final Sprite gameover = SpriteGallery.dirt.getSprite("gameover");

        final boolean dirtSpritesOnlyCampHomeOrGameOver = timeline.every(
                "dirt", (RenderableAnalyser d) -> {
                    return d.hasSprite(gameover) || d.hasSprite(camp) || d.hasSprite(home);
                });

        Assert.assertTrue(
                "Every dirt tile should have EITHER been a enemy camp, a village " +
                        "or be flipped to the 'gameover' sprite " +
                        "when the enemy wins as expected!",
                dirtSpritesOnlyCampHomeOrGameOver
        );
    }

    private String prefix() {
        return "SIM:" + data.path() + ": ";
    }

}
