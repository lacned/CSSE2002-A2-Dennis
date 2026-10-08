package towers.simulations;

import engine.art.sprites.Sprite;
import engine.game.ScreenPosition;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test_utils.Simulation;
import test_utils.analysers.AnalyserManager;
import test_utils.analysers.RenderableAnalyser;
import test_utils.data.VortexWin;
import test_utils.mocks.MockMouse;
import towers.FireWall;
import towers.SpriteGallery;
import towers.events.EventsManager;
import towers.events.ListenerManager;
import towers.world.WorldLoadException;

import java.io.IOException;
import java.util.List;

/**
 * This test confirms that the Vortex variation of the Tower successfully works
 * enough to kill enemies within expected ranges and at the expected cadence.
 */
public class VortexWinSimulationTest {
    private static final int TICKS = 3000;
    private static AnalyserManager timeline;
    private static VortexWin data;

    @AfterClass
    public static void tearDown() {
        timeline = null;
        data = null;
    }

    @BeforeClass
    public static void setUp() throws IOException, WorldLoadException {
        data = new VortexWin();
        final ListenerManager listeners = new ListenerManager(new EventsManager());
        final FireWall sanctum = new FireWall(data.map(), listeners);
        final Simulation sim = new Simulation(listeners, TICKS);
        sim.enableHeadless();
        sim.setTicks(TICKS);
        sim.setDefaultMouseState(
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false, false)
        );
        int frameCount = 100;
        sim.setFrameSpecificMouse(
                100,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 100,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 300,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 300,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 300,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 300,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 700,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 300,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 300,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );
        sim.setFrameSpecificMouse(
                frameCount += 200,
                new MockMouse(data.getNextTowerPlaceablePosition(),
                        false,
                        true
                )
        );

        timeline = sim.run();
    }

    @Test
    public void sawVortexEffect() {
        final List<RenderableAnalyser> vortexes = timeline.getBySpriteGroup("vortex");
        final int TARGET = 4;
        Assert.assertTrue(
                prefix() + "Should have seen at least "
                        + TARGET
                        + "vortex 'projectiles' , instead saw: "
                        + vortexes.size(),
                vortexes.size() >= TARGET
        );
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
        final int TARGET = 4;
        Assert.assertEquals(
                prefix() + "should have seen exactly " + TARGET + " vortex towers placed "
                        + "over the sim lifespan",
                TARGET, vortexTowers.size()
        );
    }

    @Test
    public void sawVortexTowerSprites() {
        final Sprite vortex = SpriteGallery.tower.getSprite("vortex");
        final Sprite reloadingvortex = SpriteGallery.tower.getSprite("reloadingvortex");

        final boolean OnlyHasVortexTowerSprites = timeline.every("tower",
                (RenderableAnalyser tower) -> {
                    return tower.hasSprite(vortex) || tower.hasSprite(reloadingvortex);
                });

        Assert.assertTrue(
                prefix() + "All towers constructed should have been Vortex towers " +
                        "using relevant vortex tower sprites, not normal towers",
                OnlyHasVortexTowerSprites
        );
    }

    @Test
    public void explosionAnimationHasSuitableIntervalBetweenSpriteChange() {
        final int targetInterval = 8;
        final int margin = 1;
        timeline.each("explosion", (RenderableAnalyser explosion) -> {
            Assert.assertTrue(
                    "Explosion should have changed its Sprite  roughly ever "
                            + targetInterval
                            + "frames",
                    explosion.allIntervalsBetweenSpriteChangesWithin(
                            targetInterval - margin, targetInterval + margin
                    )
            );
        });
    }

    @Test
    public void explosionsAnimateThroughSprites() {
        final int explosionSpriteCount = SpriteGallery.explosion.getSprites().size();
        final boolean atLeastSomeExplosionsFullyAnimated = timeline.some(
                "explosion", (RenderableAnalyser explosion) -> {
                    Assert.assertEquals(
                            prefix() + " all explosions should have animated " +
                                    "through all explosion frames, frames animated:",
                            explosionSpriteCount, explosion.allUniqueSprites().size()
                    );
                    return explosion.allUniqueSprites().size() == explosionSpriteCount;
                });
        Assert.assertTrue(atLeastSomeExplosionsFullyAnimated);
    }

    @Test
    public void sawExplosions() {
        final List<RenderableAnalyser> explosions = timeline.getBySpriteGroup("explosion");
        final int MIN_TARGET = 15;
        Assert.assertTrue(
                prefix() + "Should have seen at least "
                        + MIN_TARGET
                        + "explosions from dying enemies"
                        + "instead saw " + explosions.size(),
                explosions.size() >= MIN_TARGET
        );
    }

    @Test
    public void sawKnights() {
        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        final int MIN_TARGET = 15;
        Assert.assertTrue(
                prefix()
                        + "Should have seen at least "
                        + MIN_TARGET
                        + " knights " +
                        "over "
                        + TICKS
                        + " tick sim with one spawner, instead saw "
                        + knights.size(),
                knights.size() >= MIN_TARGET
        );
    }

    @Test
    public void sawHorses() {
        final List<RenderableAnalyser> horses = timeline.getBySpriteGroup("horse");
        final int MIN_TARGET = 6;
        Assert.assertTrue(
                prefix() + "Should have seen at least " + MIN_TARGET + " horses " +
                        "over " + TICKS + " tick sim with one spawner, instead saw " + horses.size(),
                horses.size() >= MIN_TARGET
        );
    }

    @Test
    public void sawExpectedNumberOfTowers() {
        final List<RenderableAnalyser> towers = timeline.getBySpriteGroup("tower");
        final int TARGET = 4;
        Assert.assertTrue(
                prefix()
                        + " should have created at least "
                        + TARGET
                        + " vortex towers over "
                        + "lifespan of sim, instead saw "
                        + towers.size(),
                TARGET <= towers.size()
        );
    }

    @Test
    public void shouldNotEverReachGameOver() {
        final Sprite gameover = SpriteGallery.dirt.getSprite("gameover");
        final boolean hasGameOverSprite = timeline.some(
                "dirt", (RenderableAnalyser d) -> {
                    return d.allUniqueSprites().contains(gameover);
                });
        Assert.assertFalse(
                prefix() + " VortexWinTest should be impossible for Enemies to win",
                hasGameOverSprite
        );
    }

    @Test
    public void vortexKillsEnemiesInRoughlyCorrectRange() {
        final List<RenderableAnalyser> towers = timeline.getBySpriteGroup("tower");
        final int TARGET = 200;
        final int knightsThatEndedNearAVortexTower = timeline.count("knight",
                (RenderableAnalyser knight) -> {
                    for (final RenderableAnalyser tower : towers) {
                        final ScreenPosition finalKnightPosition = knight.getLastFrame().screen();
                        if (finalKnightPosition.distance(tower.spawnPosition()) <= TARGET) {
                            return true;
                        }
                    }
                    return false;
                });
        final int MINTARGET = 15;
        Assert.assertTrue(
                "At *least* "
                        + MINTARGET
                        + " of the knights in this Sim when they ended, "
                        + "should have ended within range of a VortexTower, instead only "
                        + knightsThatEndedNearAVortexTower
                        + "knights ended their lifespan within range of a VortexTower",
                knightsThatEndedNearAVortexTower > MINTARGET
        );
    }

    private String prefix() {
        return "SIM:"
                + data.path()
                + ": ";
    }
}
