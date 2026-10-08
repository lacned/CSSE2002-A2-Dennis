package test_utils.analysers;

import engine.EngineState;
import engine.art.sprites.Sprite;
import engine.game.Position;

/**
 * A frame record is a record of where a particular renderable was during an engine tick. The frame
 * record tracks the specific engine tick, the position information of a renderable, and the sprite
 * that was rendered.
 */
public interface FrameRecord extends Position {
    /**
     * Return the number of ticks that have passed when this record was created. This corresponds to
     * the {@link EngineState#currentTick()} value.
     *
     * @return How many ticks have passed since starting.
     */
    int getFrame();

    /**
     * Return the sprite that was rendered during this frame.
     *
     * @return The sprite rendered during this frame.
     */
    Sprite getSprite();
}
