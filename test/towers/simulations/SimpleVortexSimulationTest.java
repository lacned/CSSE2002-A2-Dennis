package towers.simulations;


import engine.art.sprites.Sprite;
import engine.game.GridPosition;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test_utils.Simulation;
import test_utils.analysers.AnalyserManager;
import test_utils.analysers.FrameRecord;
import test_utils.analysers.RenderableAnalyser;
import test_utils.data.SimpleVortex;
import test_utils.mocks.MockMouse;
import towers.FireWall;
import towers.SpriteGallery;
import towers.events.EventsManager;
import towers.events.ListenerManager;
import towers.world.WorldLoadException;

import java.io.IOException;
import java.util.List;

public class SimpleVortexSimulationTest {
    private static final int TICKS = 5000;
    private static AnalyserManager timeline;
    private static SimpleVortex data;

    @AfterClass
    public static void tearDown() {
        timeline = null;
        data = null;
    }

    @BeforeClass
    public static void setUp() throws IOException, WorldLoadException {
        data = new SimpleVortex();
        final ListenerManager listeners = new ListenerManager(new EventsManager());
        final FireWall sanctum = new FireWall(data.map(), listeners);
        final Simulation sim = new Simulation(listeners, TICKS);
        sim.enableHeadless();
        sim.setTicks(TICKS);
        sim.setFrameSpecificMouse(1,
                new MockMouse(
                        data.getNextTowerPlaceablePosition(),
                        false, true, false
                ));
        timeline = sim.run();
    }

    @Test
    public void vortexTowerPlacedWhereClicked() throws IOException {
        final GridPosition towerSpawnClick = new SimpleVortex().getNextTowerPlaceablePosition().grid();
        final List<RenderableAnalyser> towers = timeline.getBySpriteGroup("tower");
        final int TARGET = 1;
        Assert.assertEquals(
                prefix() + "only " + TARGET + " Tower should have been built for the SimpleVortex test",
                towers.size(), TARGET
        );
        for (RenderableAnalyser tower : towers) {
            Assert.assertTrue(
                    prefix() + "Tower should only be spawned on the grid clicked on: "
                            + towerSpawnClick
                            + " in this simple sim, instead the tower was placed on "
                            + tower.spawnPosition().grid(),
                    tower.spawnPosition().grid().overlaps(towerSpawnClick)
            );
        }
    }

    @Test
    public void vortexTowerCostIsReducedCloseToBuildFrame() {
        final RenderableAnalyser tower = timeline.getFirstSpawnedOfSpriteGroup("tower");
        final RenderableAnalyser score = timeline.mergeBySpriteGroup("letter");
        final FrameRecord towerFirstFrame = tower.getFirstFrame();
        final int FRAMESPAWNED = towerFirstFrame.getFrame();

        final Sprite twoSprite = SpriteGallery.letters.getSprite("2");
        //jb: we include a small margin for error to give room for slightly
        // different implementations and ordering.
        final int MARGIN = 1;
        Assert.assertTrue(
                "Resource should have been reduced to 2 from 4 when the Tower is spawned",
                score.hasSpriteBetween(
                        twoSprite,
                        FRAMESPAWNED - MARGIN,
                        FRAMESPAWNED + MARGIN
                )
        );
    }

    @Test
    public void vortexTowerCostTwo() {
        final Sprite twoSprite = SpriteGallery.letters.getSprite("3");
        final Sprite threeSprite = SpriteGallery.letters.getSprite("3");
        final RenderableAnalyser score = timeline.mergeBySpriteGroup("letter");

        Assert.assertTrue(
                "Resource after being reduced to 2 due to the purchase of a vortex tower," +
                        "should then increase to 3",
                score.hasSprite(threeSprite));
        Assert.assertTrue(
                "Resource Should have been reduced by 2," +
                        "given a vortex tower that costs 2 is immediately built",
                score.hasSprite(twoSprite));
    }

    @Test
    public void vortexShotsSpawnsOnSameGridCellAsVortexTower() {
        final List<RenderableAnalyser> towers = timeline.getBySpriteGroup("tower");
        final boolean VortexesSpawnedOnVortexTowers = timeline.every(
                "vortex", (RenderableAnalyser vortex) -> {
                    for (RenderableAnalyser tower : towers) {
                        if (tower.spawnPosition().grid().overlaps(vortex.spawnPosition())) {
                            return true;
                        }
                    }
                    return false;
                });
        Assert.assertTrue(
                "Vortex shots should be spawned on the same grid cell as the tower",
                VortexesSpawnedOnVortexTowers
        );
    }

    @Test
    public void vortexTowerChangesToEndGameSprite() {
        final RenderableAnalyser tower = timeline.getFirstSpawnedOfSpriteGroup("tower");
        final Sprite gameoverSprite = SpriteGallery.tower.getSprite("gameover");
        final int towersFinalFrameCount = tower.getLastFrame().getFrame();

        Assert.assertTrue(
                "Vortex Tower should have been flipped to its 'gameover' sprite "
                        + "when the game ended due to enemy reaching home",
                tower.hasSpriteBetween(
                        gameoverSprite, towersFinalFrameCount - 4, towersFinalFrameCount
                )
        );
    }

    @Test
    public void vortexTowerAfterShootingFlipsToReloading() {
        final List<RenderableAnalyser> vortexes = timeline.getBySpriteGroup("vortex");
        vortexes.sort((RenderableAnalyser a, RenderableAnalyser b) -> {
            return a.getFirstFrame().getFrame() - b.getFirstFrame().getFrame();
        });
        final RenderableAnalyser vortex = vortexes.getFirst();
        final List<RenderableAnalyser> towers = timeline.getBySpriteGroup("tower");
        final RenderableAnalyser tower = towers.getFirst();

        final Sprite vortexTowerReloadingSprite = SpriteGallery.tower.getSprite(
                "reloadingvortex"
        );
        final int firstFrameCountForFirstVortex = vortex.getFirstFrame().getFrame();
        final int margin = 2;
        final boolean reloadsAroundCorrectTime = tower.hasSpriteBetween(
                vortexTowerReloadingSprite,
                firstFrameCountForFirstVortex - margin,
                firstFrameCountForFirstVortex + margin
        );
        Assert.assertTrue(
                prefix() + " Vortex Tower should have switched to it's" +
                        "reloading sprite after firing it's Vortex 'shot'",
                reloadsAroundCorrectTime
        );
    }

    @Test
    public void vortexShotAnimates() {
        final RenderableAnalyser vortex = timeline.getFirstSpawnedOfSpriteGroup("vortex");
        final int spriteCount = SpriteGallery.vortex.getSprites().size();

        final Sprite zero = SpriteGallery.vortex.getSprite("0");
        final Sprite one = SpriteGallery.vortex.getSprite("1");
        final Sprite two = SpriteGallery.vortex.getSprite("2");
        final Sprite three = SpriteGallery.vortex.getSprite("3");
        final Sprite four = SpriteGallery.vortex.getSprite("4");
        final Sprite five = SpriteGallery.vortex.getSprite("5");
        Assert.assertTrue(
                "Vortex should have had sprite:0 from the 'vortex' gallery",
                vortex.hasSprite(zero)
        );
        Assert.assertTrue(
                "Vortex should have had sprite:1 from the 'vortex' gallery",
                vortex.hasSprite(one)
        );
        Assert.assertTrue(
                "Vortex should have had sprite:2 from the 'vortex' gallery",
                vortex.hasSprite(two)
        );
        Assert.assertTrue(
                "Vortex should have had sprite:3 from the 'vortex' gallery",
                vortex.hasSprite(three)
        );
        Assert.assertTrue(
                "Vortex should have had sprite:4 from the 'vortex' gallery",
                vortex.hasSprite(four)
        );
        Assert.assertTrue(
                "Vortex should have had sprite:5 from the 'vortex' gallery",
                vortex.hasSprite(five)
        );
        Assert.assertEquals(
                "Vortex 'shot' should animated through all it's sprites over time",
                spriteCount, vortex.allUniqueSprites().size()
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
        final int TARGET = 1;
        Assert.assertEquals(
                "Should have seen " + TARGET + " Vortex Tower",
                TARGET, vortexTowers.size()
        );
    }

    @Test
    public void vortexShotCount() {
        final List<RenderableAnalyser> vortexes = timeline.getBySpriteGroup("vortex");
        final int TARGET = 1;
        Assert.assertEquals(
                "Should have seen " + TARGET + " vortex shot",
                TARGET, vortexes.size()
        );
    }

    @Test
    public void vortexTowerReloads() {
        final RenderableAnalyser tower = timeline.getFirstSpawnedOfSpriteGroup("tower");
        final Sprite vortexReloadingSprite = SpriteGallery.tower.getSprite("reloadingvortex");
        Assert.assertTrue(
                "Tower should have used the 'reloadingvortex' sprite at some points",
                tower.hasSprite(vortexReloadingSprite)
        );
    }

    @Test
    public void vortexTowerFiresWhenKnightInRangeNotBefore() {
        final List<RenderableAnalyser> vortexes = timeline.getBySpriteGroup("vortex");
        vortexes.sort((RenderableAnalyser a, RenderableAnalyser b) -> {
            return a.getFirstFrame().getFrame() - b.getFirstFrame().getFrame();
        });
        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        knights.sort((RenderableAnalyser a, RenderableAnalyser b) -> {
            return a.getLastFrame().getFrame() - b.getLastFrame().getFrame();
        });

        final int firstVortexFrameSpawned = vortexes.getFirst().getFirstFrame().getFrame();
        final int firstKnightFrameKilled = knights.getFirst().getLastFrame().getFrame();

        final int delta = Math.abs(firstVortexFrameSpawned - firstKnightFrameKilled);
        final int MARGIN = 3;
        Assert.assertTrue(
                prefix() + "First Knight should have been killed " +
                        "when the vortex spawned! Instead there was " + delta + " frames between " +
                        "the creation of the Vortex 'shot' and the first knight dying.",
                delta < MARGIN
        );
    }

    private String prefix() {
        return "SIM:" + data.path() + ": ";
    }
}
