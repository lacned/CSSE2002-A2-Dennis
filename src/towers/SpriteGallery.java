package towers;

import engine.art.ArtNotFoundException;
import engine.art.loader.ArtLoader;
import engine.art.loader.MalformedArtException;
import engine.art.sprites.SpriteGroup;
import java.io.IOException;

/**
 * A repository of sprites to use throughout the game.
 */
public class SpriteGallery {
    /**
     * Sprite group for mocks for placeholder assets.
     */
    public static final SpriteGroup mocks = load("Mocks", "mocks");
    /**
     * Sprite group for hearts.
     */
    public static final SpriteGroup heart = load("Heart", "heart");
    /**
     * Sprite group for village tiles.
     */
    public static final SpriteGroup village = load("Village", "village");
    /**
     * Sprite group for forest tiles.
     */
    public static final SpriteGroup forest = load("Forest", "forest");
    /**
     * Sprite group for explosions.
     */
    public static final SpriteGroup explosion = load("Explosion", "explosion");
    /**
     * Sprite group for gui elements.
     */
    public static final SpriteGroup gui = load("Gui", "gui");
    /**
     * Sprite group for medieval themed gui elements.
     */
    public static final SpriteGroup medievalgui = load("MedievalGui", "gui");
    /**
     * Sprite group for waves.
     */
    public static final SpriteGroup waveFlag = load("WaveFlag", "waveflag");
    /**
     * Sprite group for tower tiles.
     */
    public static final SpriteGroup tower = load("Tower", "tower");
    /**
     * Sprite group for building site tiles.
     */
    public static final SpriteGroup buildingsite = load("BuildingSite", "buildingsite");
    /**
     * Sprite group for directions.
     */
    public static final SpriteGroup direction = load("Direction", "direction");

    /**
     * Sprite group for bomb tiles.
     */
    public static final SpriteGroup bomb = load("Bomb", "bomb");

    /**
     * Sprite group for bomb tiles.
     */
    public static final SpriteGroup vortex = load("Vortex", "vortex");

    /**
     * Sprite group for grass tiles.
     */
    public static final SpriteGroup grass = load("Grass", "grass");
    /**
     * Sprite group for dirt tiles.
     */
    public static final SpriteGroup dirt = load("Dirt", "dirt");
    /**
     * Sprite group for letter/text UI elements.
     */
    public static final SpriteGroup letters = load("Letters", "letter");

    /**
     * Sprite group for knight tiles.
     */
    public static final SpriteGroup knight = load("Knight", "knight");

    /**
     * Sprite group for horse tiles.
     */
    public static final SpriteGroup horse = load("Horse", "horse");

    private SpriteGallery() {}

    /**
     * Load a sprite image from an art file at resources/art/[spriteFilename].art. The group of
     * assets under groupName are returned.
     *
     * @param spriteFilename The name of the file under resources/art/ to load.
     * @param groupName      The common prefix of sprites within the given file.
     */
    private static SpriteGroup load(String spriteFilename, String groupName) {
        try {
            return ArtLoader.load("resources/art/" + spriteFilename + ".art").lookup(groupName);
        } catch (IOException | ArtNotFoundException | MalformedArtException e) {
            // Cannot throw a checked exception when instantiating a static field.
            // Wrap up any thrown exception as RuntimeException
            // This should crash the JVM when starting up the game
            throw new RuntimeException(e);
        }
    }
}
