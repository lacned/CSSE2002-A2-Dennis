package test_utils.analysers;

import engine.art.sprites.Sprite;
import engine.game.Position;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates an analyser that manages the various common ways to analyse lists of data derived from
 * the renderables log.
 */
public class RenderableAnalyser extends Analyser {

    public RenderableAnalyser(SpatialAnalyser analyser) {
        super(analyser.getId(), analyser.getFrames());
    }

    /**
     * @param id the unique id to match to.
     */
    public RenderableAnalyser(String id) {
        super(id);
    }

    /**
     * Confirm the entity was rendered on the given frame
     *
     * @param targetFrame frame we wish to check
     * @return if the entity was rendered on the given frame
     */
    public boolean wasInFrame(int targetFrame) {
        assert targetFrame >= 0;
        for (final FrameRecord frameRecord : getFrames()) {
            if (frameRecord.getFrame() == targetFrame) {
                return true;
            }
        }
        return false;
    }

    /**
     * Confirm the entity was rendered between the given frames.
     *
     * @param start starting frame to check from inclusively.
     * @param end   end frame to check from inclusively.
     * @return if the entity was rendered between the given frames.
     */
    public boolean wasWithinFrames(int start, int end) {
        assert start <= end;
        return !this.getFramesBetween(start, end).isEmpty();
    }

    /**
     * Returns if the time between every Sprite change for this Renderable was within a given acceptable range (inclusive)
     *
     * @param minFrameDelta - minimum end of the acceptable range (inclusive)
     * @param maxFrameDelta - maximum end of the acceptables range (inclusive)
     * @return if the time between every Sprite change for this Renderable was within a given acceptable range (inclusive)
     */
    public boolean allIntervalsBetweenSpriteChangesWithin(
            int minFrameDelta,
            int maxFrameDelta
    ) {
        assert minFrameDelta <= maxFrameDelta;
        if (getFrames().isEmpty()) {
            return false;
        }
        FrameRecord prevDistinctFrame = getFirstFrame();
        for (int i = 1; i < getFrames().size(); i++) {
            FrameRecord frame = getFrames().get(i);
            if (!prevDistinctFrame.getSprite().toString().equals(frame.getSprite().toString())) {
                int interval = frame.getFrame() - prevDistinctFrame.getFrame();
                if (interval < minFrameDelta || interval > maxFrameDelta) {
                    return false;
                }
                prevDistinctFrame = frame;
            }
        }
        return true;
    }

    /**
     * Return if the given {@link Sprite} was rendered by this entity at any time.
     *
     * @param sprite the sprite we wish to check for.
     * @return if the given {@link Sprite} was rendered by this entity at any time.
     */
    public boolean hasSprite(Sprite sprite) {
        assert sprite != null;
        for (final FrameRecord frameData : getFrames()) {
            if (frameData.getSprite().getLabel().equals(sprite.getLabel())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns how many frames the given sprite was rendered for by this entity.
     *
     * @param sprite {@link Sprite} we wish to check
     * @return how many frames the given sprite was rendered for by this entity.
     */
    public int lifespanOfSprite(Sprite sprite) {
        assert sprite != null;
        int frameCount = 0;
        for (FrameRecord frameData : getFrames()) {
            if (frameData.getSprite().equals(sprite)) {
                frameCount += 1;
            }
        }
        return frameCount;
    }

    /**
     * Return if the given {@link Sprite} was rendered by this entity between the given start and
     * end time (inclusive).
     *
     * @param start  starting frame to check from inclusively.
     * @param end    end frame to check too inclusively.
     * @param sprite the sprite we wish to check for.
     * @return if the given {@link Sprite} was rendered by this entity between the given start and
     * end time (inclusive).
     */
    public boolean hasSpriteBetween(Sprite sprite, int start, int end) {
        assert sprite != null;
        assert start <= end;
        for (FrameRecord frameData : getFramesBetween(start, end)) {
            if (frameData.getSprite().getLabel().equals(sprite.getLabel())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if a frame was within a given radial distance of a given x,y coordinate pair between
     * (inclusively) a given pair of starting/ending frames.
     *
     * @param x        xCoordinate
     * @param y        yCoordinate
     * @param distance radial distance
     * @param start    starting frame to check from inclusively.
     * @param end      end frame to check too inclusively.
     * @return if a frame was within a given radial distance of a given x,y coordinate pair between
     * (inclusively) a given pair of starting/ending frames.
     */
    public boolean wasWithinDistanceofBetweenFrames(
            int x, int y, int distance, int start, int end) {
        assert start <= end;
        for (final FrameRecord frameData : getFramesBetween(start, end)) {
            final int actualDistance = distanceFrom(frameData, x, y);
            if (actualDistance <= distance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if a frame was within a given radial distance of a given x,y coordinate pair between
     * (inclusively) a given pair of starting/ending frames.
     *
     * @param pos      Position of radial center to measure from
     * @param distance radial distance
     * @param start    starting frame to check from inclusively.
     * @param end      end frame to check too inclusively.
     * @return if a frame was within a given radial distance of a given x,y coordinate pair between
     * (inclusively) a given pair of starting/ending frames.
     */
    public boolean wasWithinDistanceofBetweenFrames(
            Position pos, int distance, int start, int end) {
        assert start <= end;
        return wasWithinDistanceofBetweenFrames(pos.screen().getX(), pos.screen().getY(), distance, start, end);
    }

    /**
     * Confirm the entity rendered with the given sprite between the given frames.
     *
     * @param sprite sprite to check for.
     * @param start  starting frame to check from inclusively.
     * @param end    end frame to check too inclusively.
     * @return if the entity was rendered between the given frames.
     */
    public boolean spriteWasWithinFrames(Sprite sprite, int start, int end) {
        assert start <= end;
        for (final FrameRecord frameData : getFramesBetween(start, end)) {
            if (frameData.getSprite().equals(sprite)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a list of each distinct sprite rendered one or more times for this entity.
     *
     * @return a list of each distinct sprite rendered one or more times for this entity.
     */
    public List<Sprite> allUniqueSprites() {
        final List<Sprite> list = new ArrayList<>();
        for (final FrameRecord frameData : getFrames()) {
            if (!list.contains(
                    frameData.getSprite())) { // confirm the sprite is not already in the list
                list.add(frameData.getSprite());
            }
        }

        return list;
    }

    /**
     * Returns a String representation of which {@link engine.art.sprites.SpriteGroup} this belongs
     * to based on the first {@link Sprite} of the {@link engine.renderer.Renderable}. We assume
     * Renderables only have {@link Sprite}s from one {@link engine.art.sprites.SpriteGroup}.
     *
     * @return a String representation of which {@link engine.art.sprites.SpriteGroup} this belongs
     * to based on the first {@link Sprite} of the {@link engine.renderer.Renderable}.
     */
    public String spriteGroup() {
        if (getFrames().isEmpty()) {
            return null;
        }
        return getFirstFrame().getSprite().getGroup();
    }

    /**
     * Return a String representation of the RenderableAnalysers internal state.
     *
     * @return a String representation of the RenderableAnalysers internal state.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("EntityAnalyzer[");
        if (!this.getFrames().isEmpty()) {
            sb.append("spawned at:" + this.spawnPosition());
            sb.append(" (totalFrames:" + this.getFrames().size() + ", ");
            sb.append("starting:" + this.getFrames().getFirst().getFrame() + ", ");
            sb.append("last:" + this.getFrames().getLast().getFrame() + ") ");
            sb.append("sprites:" + this.allUniqueSprites().size() + "");
            sb.append("sprite group:" + this.spriteGroup() + "");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Method to convert this analyser to its Spatial equivalent.
     *
     * @return {@link SpatialAnalyser}
     */
    public SpatialAnalyser spatial() {
        return new SpatialAnalyser(this);
    }
}
