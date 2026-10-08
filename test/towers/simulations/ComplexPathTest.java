package towers.simulations;

import engine.art.sprites.Sprite;
import engine.game.GridPosition;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test_utils.Simulation;
import test_utils.analysers.AnalyserManager;
import test_utils.analysers.RenderableAnalyser;
import test_utils.data.ComplexPath;
import test_utils.data.TestMapData;
import test_utils.mocks.MockKeys;
import test_utils.mocks.MockMouse;
import towers.FireWall;
import towers.SpriteGallery;
import towers.events.EventsManager;
import towers.events.ListenerManager;
import towers.world.WorldLoadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tests the following:
 * <ul>
 *     <li>Enemy uses Pathing Correctly.</li>
 *     <li>Enemy render Correctly based on direction going.</li>
 *     <li>Tiles change to relevant end game sprites if game is over.</li>
 *     <li>That towers are not buildable on dirt or forest tiles.</li>
 * </ul>
 */
public class ComplexPathTest {
    private static final int TICKS = 10000;
    private static AnalyserManager timeline;
    private static TestMapData data;
    private static final int TILES_PER_ROW = 16;

    @AfterClass
    public static void tearDown() {
        timeline = null;
        data = null;
    }

    @BeforeClass
    public static void setUp() throws IOException, WorldLoadException {
        data = new ComplexPath();

        final ListenerManager listeners = new ListenerManager(new EventsManager());
        final FireWall sanctum = new FireWall(data.map(), listeners);
        final Simulation sim = new Simulation(listeners, TICKS);
        sim.setTicks(TICKS);
        sim.enableHeadless();
        sim.setFps(30);
        sim.setDefaultMouseState(new MockMouse(new GridPosition(5, 5), true));

        //Ensure we click on a large number of the grid cells to ensure things are not built
        // when it should be impossible etc
        int tickTarget = 1;
        for (int row = 0; row < TILES_PER_ROW; row++) {
            for (int col = 0; col < TILES_PER_ROW; col++) {
                tickTarget += 1;
                sim.setFrameSpecificMouse(
                        tickTarget, new MockMouse(new GridPosition(col, row),
                                false, tickTarget % 2 == 0, false
                        )
                );
            }
        }
        sim.setDefaultKeyState(new MockKeys(new ArrayList<>()));
        timeline = sim.run();
    }

    @Test
    public void vortexShotCount() {
        final List<RenderableAnalyser> vortexes = timeline.getBySpriteGroup("vortex");
        Assert.assertEquals(
                "Should have seen zero vortex shots",
                0, vortexes.size()
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
        Assert.assertEquals(
                prefix() + "should NOT have seen any vortex towers placed!",
                0, vortexTowers.size()
        );
    }


    @Test
    public void horseKnightSpawnRatio() {
        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        final List<RenderableAnalyser> horses = timeline.getBySpriteGroup("horse");

        final int margin = 2;
        Assert.assertTrue(
                prefix() + "Should be 15 or so knights.",
                knights.size() >= 15 - margin
        );
        Assert.assertTrue(
                prefix() + "Should be 5 or so horses.",
                horses.size() <= 5 + margin
        );
    }

    @Test
    public void shouldHaveSeenMultipleHorseFacings() {
        final Set<Sprite> horseSprites = timeline.getUniqueSpritesFor("horse");
        final Sprite left = SpriteGallery.horse.getSprite("left");
        final Sprite right = SpriteGallery.horse.getSprite("right");
        final Sprite down = SpriteGallery.horse.getSprite("down");
        Assert.assertTrue(
                "should have seen at least 3 of the horse sprites, instead saw "
                        + horseSprites.size()
                        + "sprites.",
                horseSprites.size() >= 3
        );
        Assert.assertTrue(
                "Should have seen left facing horse sprite",
                horseSprites.contains(left)
        );
        Assert.assertTrue(
                "Should have seen right facing horse sprite",
                horseSprites.contains(right)
        );
        Assert.assertTrue(
                "Should have seen down facing horse sprite",
                horseSprites.contains(down)
        );
    }

    @Test
    public void horseShouldHaveReachedEndBeforeAnyKnightsCould() {
        final Sprite villageSprite = SpriteGallery.dirt.getSprite("village");
        final List<RenderableAnalyser> villages = timeline
                .getBySpriteGroup("dirt")
                .stream()
                .filter(d -> {
                    return d.hasSprite(villageSprite);
                }).toList();
        final RenderableAnalyser village = villages.getFirst();

        final boolean atLeastOneKnightOverlapsVillage = timeline.some("knight",
                (RenderableAnalyser knight) -> {
                    final GridPosition finalHorsePos = knight.getLastFrame().grid();
                    final GridPosition villagePos = village.spawnPosition().grid();
                    if (finalHorsePos.overlaps(villagePos)) {
                        return true;
                    }
                    return false;
                });
        Assert.assertFalse(
                prefix() + " no knights should be overlapping village at game end! " +
                        "Game should have ended due to " +
                        "a horse reaching village before a knight could.",
                atLeastOneKnightOverlapsVillage
        );

        final boolean atLeastOneHorseOverlapsVillage = timeline.some("horse",
                (RenderableAnalyser horse) -> {
                    final GridPosition finalHorsePos = horse.getLastFrame().grid();
                    final GridPosition villagePos = village.spawnPosition().grid();
                    return finalHorsePos.overlaps(villagePos);
                });
        Assert.assertTrue(
                prefix() + "At least one horse should have been overlapping " +
                        "the village when the game ended!",
                atLeastOneHorseOverlapsVillage
        );
    }

    @Test
    public void knightsShouldSpawnOnTheSpawnTiles() {
        final List<RenderableAnalyser> dirt = timeline.getBySpriteGroup("dirt");
        final Sprite campSprite = SpriteGallery.dirt.getSprite("camp");
        final List<RenderableAnalyser> camps = dirt.stream().filter(d -> {
            return d.hasSprite(campSprite);
        }).toList();
        final boolean knightsOnlySpawnedOnSpawnerTiles = timeline.every(
                "knight", (RenderableAnalyser knight) -> {
                    for (final RenderableAnalyser camp : camps) {
                        if (camp.spawnPosition().grid().overlaps(knight.spawnPosition())) {
                            return true;
                        }
                    }
                    return false;
                });
        Assert.assertTrue(
                prefix() + "Knights should only ever spawn on spawner tiles!",
                knightsOnlySpawnedOnSpawnerTiles
        );
    }

    @Test
    public void knightsWereOnlyEverOnDirt() {
        final List<RenderableAnalyser> dirtTiles = timeline.getBySpriteGroup("dirt");
        final Set<GridPosition> dirtTilePositions = new HashSet<>();
        for (RenderableAnalyser dirtTile : dirtTiles) {
            dirtTilePositions.add(dirtTile.spawnPosition().grid());
        }

        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        final Set<GridPosition> knightTilePositions = new HashSet<>();
        for (RenderableAnalyser knightTile : knights) {
            knightTilePositions.add(knightTile.spawnPosition().grid());
        }

        for (GridPosition knight : knightTilePositions) {
            boolean overlap = false;
            for (GridPosition dirt : dirtTilePositions) {
                if (knight.grid().overlaps(dirt.grid())) {
                    overlap = true;
                }
            }
            Assert.assertTrue(
                    prefix() + "knight should only have been on " +
                            "overlapping dirt/spawner/home tiles " +
                            "at every step of this program, there are no grass tiles to cross, " +
                            "and they can NOT walk through forests",
                    overlap
            );
        }
    }

    @Test
    public void sawDirtGameOver() {
        final Set<Sprite> sprites = timeline.getUniqueSpritesFor("dirt");
        final Sprite gameover = SpriteGallery.dirt.getSprite("gameover");
        Assert.assertTrue(
                prefix() + "Should have seen 'gameover' sprites from the 'dirt' " +
                        "spriteGallery saw these unique sprites from 'dirt' gallery: ",
                sprites.contains(gameover)
        );
    }

    @Test
    public void sawForestGameOver() {
        final Set<Sprite> sprites = timeline.getUniqueSpritesFor("forest");
        final Sprite gameover = SpriteGallery.forest.getSprite("gameover");
        Assert.assertTrue(
                prefix() + "Should have seen 'gameover' sprites from the 'forest'",
                sprites.contains(gameover)
        );
    }

    @Test
    public void sawHome() {
        final Sprite village = SpriteGallery.dirt.getSprite("village");
        final boolean sawVillageSprite = timeline.some("dirt", (RenderableAnalyser dirt) -> {
            return dirt.hasSprite(village);
        });
        Assert.assertTrue(
                prefix() + "Should have seen a village sprite!",
                sawVillageSprite
        );
    }

    @Test
    public void sawEnemies() {
        final Set<Sprite> knights = timeline.getUniqueSpritesFor("knight");
        Assert.assertEquals(
                prefix() + "we should have seen 3 unique knight sprites being used",
                3, knights.size()
        );
    }

    @Test
    public void sawSpawnCamps() {
        final Sprite village = SpriteGallery.dirt.getSprite("camp");
        final boolean sawCampSprite = timeline.some("dirt", (RenderableAnalyser dirt) -> {
            return dirt.hasSprite(village);
        });
        Assert.assertTrue(
                prefix() + "Should have seen a camp sprite!",
                sawCampSprite
        );
    }

    private String prefix() {
        return "SIM:" + data.path() + ": ";
    }
}
