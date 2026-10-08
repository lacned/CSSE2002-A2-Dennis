package towers.simulations;

import engine.art.sprites.Sprite;
import engine.game.GridPosition;
import engine.game.Position;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import test_utils.Simulation;
import test_utils.analysers.*;
import test_utils.data.PathStress;
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

public class PathStressSimulationTest {
    private static final int SIZE = 800; //don't need many tick
    private static final int TILES_PER_ROW = 16;
    private static final int CELLS_SIZE = SIZE / TILES_PER_ROW;
    private static final int TICKS = 3005;
    private static AnalyserManager timeline;
    private static PathStress data;

    @AfterClass
    public static void tearDown() {
        timeline = null;
        data = null;
    }

    //For the score sim tests we will place ONE tower but otherwise just let the sim run.
    @BeforeClass
    public static void setUp() throws IOException, WorldLoadException {
        data = new PathStress();

        final ListenerManager listeners = new ListenerManager(new EventsManager());
        final FireWall sanctum = new FireWall(data.map(), listeners);
        final Simulation sim = new Simulation(listeners, TICKS);
        sim.enableHeadless();
        sim.setDefaultMouseState(new MockMouse(data.getNextForestPosition(), false));
        sim.setDefaultKeyState(new MockKeys(new ArrayList<>()));

        sim.setFrameSpecificMouse(40, new MockMouse(
                data.getNextTowerPlaceablePosition(),
                false, true
        ));
        sim.setFrameSpecificMouse(80, new MockMouse(
                data.getNextTowerPlaceablePosition(),
                true, false
        ));
        int frameCount = 200;
        sim.setFrameSpecificMouse(frameCount, new MockMouse(
                data.getNextTowerPlaceablePosition(),
                true, false
        ));
        sim.setFrameSpecificMouse(frameCount += 400, new MockMouse(
                data.getNextTowerPlaceablePosition(),
                false, true
        ));
        sim.setFrameSpecificMouse(frameCount += 700, new MockMouse(
                data.getNextTowerPlaceablePosition(),
                false, true
        ));
        sim.setFrameSpecificMouse(frameCount += 100, new MockMouse(
                data.getNextTowerPlaceablePosition(),
                true
        ));
        timeline = sim.run();
    }

    @Test
    public void sawShots() {
        final List<RenderableAnalyser> bombs = timeline.getBySpriteGroup("bomb");
        final List<RenderableAnalyser> towers = timeline.getBySpriteGroup("tower");
        final int MAX = 12;
        final int MIN = 2;
        Assert.assertTrue(
                prefix() + "should have seen less than " + MAX + " separate bombs" +
                        " spawned over the games lifespan",
                bombs.size() < MAX
        );
        Assert.assertTrue(
                prefix() + "should have seen at least " + MIN + " separate bombs" +
                        " spawned over the games lifespan, saw: " + bombs.size(),
                bombs.size() >= MIN
        );

        //lifespan checks
        timeline.each("bomb", (RenderableAnalyser bomb) -> {
            final int BOMB_COUNT = 1;
            Assert.assertEquals(
                    prefix() + "All Bombs were using " + BOMB_COUNT + " Sprite",
                    BOMB_COUNT, bomb.allUniqueSprites().size()
            );
            final int MIN_LIFESPAN = 20;
            Assert.assertTrue(
                    prefix() + "all bombs in path stress sim should have a lifespan " +
                            "over 20 frames",
                    bomb.frameLifespan() > MIN_LIFESPAN
            );
            final int MAX_LIFESPAN = 600;
            Assert.assertTrue(
                    prefix() + "all bombs in path stress sim should have a lifespan " +
                            "under 600 frames, instead bomb had " + bomb.frameLifespan(),
                    bomb.frameLifespan() <= MAX_LIFESPAN
            );
        });

        //spatial bomb checks
        timeline.each("bomb", (RenderableAnalyser bomb) -> {
            final GridPosition bombSpawnPos = bomb.spawnPosition().grid();
            boolean spawnedOnOneOfTheTowers = false;
            for (RenderableAnalyser tower : towers) {
                final Position towerSpawnPos = tower.spawnPosition();
                if (bombSpawnPos.overlaps(towerSpawnPos)) {
                    spawnedOnOneOfTheTowers = true;
                }
            }
            Assert.assertTrue(
                    prefix() + "bombs should spawn ontop of their towers",
                    spawnedOnOneOfTheTowers
            );
        });
    }

    //JB:Intended as a way to help stop abuse of the test system, if the game is built correctly
    //some enemies will be dying before they get to turn left, some will turn left.
    //So we can use that and varying lifespans as proxies to ensure
    // someone isn't just spawning knights in and removing them after x frames.
    @Test
    public void knightsDisplayedVarietyOfSpritesOverVaryingLifespans() {
        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        final HashSet<Integer> lifespans = new HashSet<>();
        final HashSet<Integer> spriteCounts = new HashSet<>();
        for (final RenderableAnalyser knight : knights) {
            lifespans.add(knight.frameLifespan());
            spriteCounts.add(knight.allUniqueSprites().size());
        }
        final int TARGET = 3;
        Assert.assertEquals(prefix() + "Not all knights should survive long enough to turn left, " +
                        "so there should be some knights with only 1 or 2 unique sprites over " +
                        "the games lifespan" +
                        "over their lifespan that got further",
                TARGET, spriteCounts.size()
        );

        final int MIN_LIFESPANS_RANGE = 5;
        Assert.assertTrue(
                prefix() + "In this sim knights should be spawned " +
                        "and getting shot at various times so knights should have " +
                        "a wide variety of unique lifespans",
                lifespans.size() > MIN_LIFESPANS_RANGE
        );
    }

    @Test
    public void bombsShouldBeHittingAndRemovingKnights() {
        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        final List<RenderableAnalyser> bombs = timeline.getBySpriteGroup("bomb");
        final List<RenderableAnalyser> explosions = timeline.getBySpriteGroup("explosion");
        final List<RenderableAnalyser> killedKnights = timeline.getBySpriteGroup("knight");
        int validKillCount = 0;
        for (final RenderableAnalyser knight : knights) {
            final FrameRecord finalKnightFrame = knight.getLastFrame();
            final boolean outOfBounds = finalKnightFrame.screen().getX() <= 0
                    || finalKnightFrame.screen().getY() <= 0;
            //start with the out-of-bounds value, which is generally false.
            boolean validKillCondition = outOfBounds;
            for (final RenderableAnalyser bomb : bombs) {
                final FrameRecord finalBombFrame = bomb.getLastFrame();
                final int deltaBetweenFinalFrames = Math.abs(
                        finalKnightFrame.getFrame() - finalBombFrame.getFrame()
                );

                final GridPosition lastKnightPos = knight.getLastFrame().grid();
                final GridPosition lastBombPos = bomb.getLastFrame().grid();
                final double distanceMargin = CELLS_SIZE * 1.5;
                final boolean closeEnough = lastBombPos.screen().distance(lastKnightPos)
                        <= distanceMargin;
                final int FRAME_DELTA_MARGIN = 5;
                if (deltaBetweenFinalFrames < FRAME_DELTA_MARGIN && closeEnough) {
                    validKillCondition = true;
                }
            }
            if (validKillCondition) {
                killedKnights.add(knight);
                validKillCount += 1;
            }
        }
        final int DEADKNIGHTARGET = 2;
        Assert.assertTrue(
                prefix()
                        + "At *least* " + DEADKNIGHTARGET + " knights should have died to bombs "
                        + "that were overlapping with the knight in question on their final frame, "
                        + "instead only "
                        + killedKnights.size()
                        + " were killed.",
                killedKnights.size() >= DEADKNIGHTARGET
        );

        int explosionNearKilledKnight = 0;
        for (RenderableAnalyser knight : killedKnights) {
            for (final RenderableAnalyser explosion : explosions) {
                final GridPosition lastKnightPos = knight.getLastFrame().grid();
                final GridPosition lastExplosionPos = explosion.getLastFrame().grid();
                final double distanceMargin = CELLS_SIZE * 3.5;
                final boolean closeEnough = lastExplosionPos.screen().distance(lastKnightPos)
                        <= distanceMargin;
                final int deltaBetweenFinalFrames = Math.abs(
                        knight.getLastFrame().getFrame() - explosion.getLastFrame().getFrame()
                );
                final int FRAME_DELTA_MARGIN = 50;
                if (deltaBetweenFinalFrames < FRAME_DELTA_MARGIN && closeEnough) {
                    explosionNearKilledKnight += 1;
                }
            }
        }
        final int EXPLOSIONTARGET = 2;
        Assert.assertTrue(
                "At least " + EXPLOSIONTARGET + " explosions should have happened near killed knights," +
                        " instead only found "
                        + explosionNearKilledKnight
                        + "explosions near a killed knight within" +
                        " an acceptable distance and timeframe margin",
                explosionNearKilledKnight >= EXPLOSIONTARGET
        );
    }


    @Test
    public void knightsShouldSpawnOnTheSpawnTiles() {
        final Sprite campSprite = SpriteGallery.dirt.getSprite("camp");
        final List<RenderableAnalyser> camps = timeline
                .getBySpriteGroup("dirt")
                .stream()
                .filter(d -> {
                    return d.hasSprite(campSprite);
                }).toList();
        final boolean spawnedOnACamp = timeline.every("knight", (RenderableAnalyser knight) -> {
            for (final RenderableAnalyser camp : camps) {
                if (camp.spawnPosition().grid().overlaps(knight.spawnPosition())) {
                    return true;
                }
            }
            return false;
        });
        Assert.assertTrue(
                prefix() + "Knights should only ever spawn on spawner tiles!",
                spawnedOnACamp
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
                prefix() + "should have seen " + TARGET + " vortex towers!, " +
                        "instead saw " + vortexTowers.size(),
                TARGET, vortexTowers.size()
        );
    }

    @Test
    public void sawTowers() {
        final List<RenderableAnalyser> towers = timeline.getBySpriteGroup("tower");
        final int TARGET = 2;
        Assert.assertEquals(
                prefix() + TARGET + " towers should have been built " +
                        "for simulation",
                TARGET, towers.size()
        );
        final Sprite loadedTowerSprite = SpriteGallery.tower.getSprite("loaded");
        final Sprite reloadingTowerSprite = SpriteGallery.tower.getSprite("reloading");
        timeline.each("towers", (RenderableAnalyser tower) -> {
            final int TARGET_UNIQUE_SPRITE_COUNT = 3;
            Assert.assertEquals(
                    prefix() + "Towers in should each have had 3 unique sprites" +
                            "in this sim",
                    TARGET_UNIQUE_SPRITE_COUNT, tower.allUniqueSprites().size()
            );
            Assert.assertTrue(
                    prefix() + "Each Tower should have been using " +
                            "the loaded sprite at some points ",
                    tower.allUniqueSprites().contains(loadedTowerSprite)
            );
            Assert.assertTrue(
                    prefix() + "Each Tower should have been using " +
                            "the reloading sprite at some points ",
                    tower.allUniqueSprites().contains(reloadingTowerSprite)
            );
        });
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
        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        final Set<Sprite> uniqueSprites = new HashSet<>();
        for (RenderableAnalyser knight : knights) {
            uniqueSprites.addAll(knight.allUniqueSprites());
        }
        final int TARGET_UNIQUE_SPRITE_COUNT = 3;
        Assert.assertEquals(
                prefix() + "we should have seen 3 unique knight sprites being used",
                TARGET_UNIQUE_SPRITE_COUNT, uniqueSprites.size()
        );
    }

    @Test
    public void sawSpawnCamps() {
        final List<RenderableAnalyser> dirtTiles = timeline.getBySpriteGroup("dirt");
        final Set<Sprite> uniqueSprites = new HashSet<>();
        for (RenderableAnalyser dirtTile : dirtTiles) {
            uniqueSprites.addAll(dirtTile.allUniqueSprites());
            dirtTile.allUniqueSprites();
        }
        Assert.assertTrue(
                prefix() + "Should have seen a camp sprite!",
                uniqueSprites.contains(SpriteGallery.dirt.getSprite("camp"))
        );
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
    public void knightsWereNeverOnForests() {
        final List<RenderableAnalyser> forestTiles = timeline.getBySpriteGroup("forest");
        final Set<GridPosition> forestTilePositions = new HashSet<>();
        for (RenderableAnalyser forestTile : forestTiles) {
            forestTilePositions.add(forestTile.spawnPosition().grid());
        }

        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        final Set<GridPosition> knightTilePositions = new HashSet<>();
        for (RenderableAnalyser knightTile : knights) {
            knightTilePositions.add(knightTile.spawnPosition().grid());
        }

        for (GridPosition knight : knightTilePositions) {
            for (GridPosition forest : forestTilePositions) {
                Assert.assertFalse(
                        prefix() + "No enemy should ever have overlapped a forest tile ",
                        knight.grid().overlaps(forest.grid())
                );
            }
        }
    }


    //Confirm that the enemies are not pathing through a grass tile with a tower on it.
    @Test
    public void confirmEnemiesAvoidRightPathway() {
        final List<RenderableAnalyser> knights = timeline.getBySpriteGroup("knight");
        final List<GridPosition> cellsForTheBlockedByTowerLowerRightPathway = new ArrayList<>();
        cellsForTheBlockedByTowerLowerRightPathway.add(new GridPosition(11, 10));
        cellsForTheBlockedByTowerLowerRightPathway.add(new GridPosition(11, 11));
        cellsForTheBlockedByTowerLowerRightPathway.add(new GridPosition(11, 12));
        cellsForTheBlockedByTowerLowerRightPathway.add(new GridPosition(12, 10));
        cellsForTheBlockedByTowerLowerRightPathway.add(new GridPosition(12, 11));
        cellsForTheBlockedByTowerLowerRightPathway.add(new GridPosition(12, 12));
        for (final RenderableAnalyser knight : knights) {
            final SpatialAnalyser tester = knight.spatial();
            for (GridPosition cell : cellsForTheBlockedByTowerLowerRightPathway) {
                Assert.assertFalse(
                        prefix() + "No knights should have been able to use " +
                                "the right pathway! A tower was placed to block that route. " +
                                "hint: Check that your grass tiles correctly indicate " +
                                "they can no longer be walked " +
                                "through once a building is placed on them",
                        tester.visitedRectangularArea(
                                cell,
                                CELLS_SIZE, CELLS_SIZE
                        ));
            }
        }


    }

    private void assertSpriteSeen(Sprite spriteLookingFor, Set<Sprite> sprites, String msg) {
        final StringBuilder sb = new StringBuilder();
        sb.append(msg);
        for (Sprite sprite : sprites) {
            sb.append("Sprite:").append(sprite.getLabel()).append("\n");
            sb.append(sprite);
            sb.append("--------------\n");
        }
        Assert.assertTrue(
                sb.toString(),
                sprites.contains(spriteLookingFor)
        );
    }

    private String prefix() {
        return "SIM:" + data.path() + ": ";
    }
}
