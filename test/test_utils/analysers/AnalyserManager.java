package test_utils.analysers;

import engine.EngineState;
import engine.art.sprites.Sprite;
import engine.renderer.Renderable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Responsible for holding all the generated {@link RenderableAnalyser}s identifiable by their
 * stringified UUIDS. Holds several useful predicate driven methods like .every, .count, .filter to
 * help with common checks when interrogating overall game state in our tests.
 */
public class AnalyserManager {
    private final Map<String, RenderableAnalyser> data = new HashMap<>();

    /**
     * Constructs a new empty AnalyserManager.
     */
    public AnalyserManager() {}

    /**
     * Record the state of the given renderable during the given frame. If the renderable has not
     * previously been tracked, it will be added to the internal tracking.
     *
     * @param frame      The current frame number according to {@link EngineState#currentTick()}.
     * @param renderable The renderable we want to begin tracking or update the state of one we are
     *                   currently tracking.
     */
    public void add(int frame, Renderable renderable) {
        assert frame >= 0;
        assert renderable != null;
        if (!data.containsKey(renderable.getID())) {
            data.put(renderable.getID(), new RenderableAnalyser(renderable.getID()));
        }
        data.get(renderable.getID()).addFrameData(frame, renderable);
    }

    /**
     * Return a {@link RenderableAnalyser} that matches the given id.
     *
     * @param id id we are filtering by.
     * @return a {@link RenderableAnalyser} that matches the given id or null.
     */
    public RenderableAnalyser get(String id) {
        assert !id.isEmpty(); //no id should ever be an empty string
        return data.get(id);
    }

    /**
     * Return the first {@link RenderableAnalyser} spawned that belongs to the given spriteGroup.
     *
     * @param label label we wish to filter for spriteGroup by.
     * @return the first {@link RenderableAnalyser} spawned that belongs to the given spriteGroup.
     */
    public RenderableAnalyser getFirstSpawnedOfSpriteGroup(String label) {
        assert !label.isEmpty();
        int spawnTime = Integer.MAX_VALUE;
        RenderableAnalyser renderable = null;
        for (final RenderableAnalyser entry : this.getBySpriteGroup(label)) {
            if (entry.getFirstFrame().getFrame() < spawnTime) {
                spawnTime = entry.getFirstFrame().getFrame();
                renderable = entry;
            }
        }
        return renderable;
    }

    /**
     * Returns an {@link ArrayList} of {@link RenderableAnalyser}s filtered by the given label
     * against each {@link RenderableAnalyser}s spriteGroup.
     *
     * @param label spriteGroup label we wish to filter for.
     * @return an *unsorted* list of {@link RenderableAnalyser}s filtered by the given label against
     * each {@link RenderableAnalyser}s spriteGroup.
     */
    public List<RenderableAnalyser> getBySpriteGroup(String label) {
        assert !label.isEmpty();
        final List<RenderableAnalyser> result = new ArrayList<>();
        for (final RenderableAnalyser analyser : getAll()) {
            if (Objects.equals(analyser.spriteGroup(), label)) {
                result.add(analyser);
            }
        }
        return result;
    }


    /**
     * Returns every {@link RenderableAnalyser} stored in this {@link AnalyserManager}.
     *
     * @return every {@link RenderableAnalyser} stored in this {@link AnalyserManager}.
     */
    public List<RenderableAnalyser> getAll() {
        return new ArrayList<>(data.values());
    }

    /**
     * Checks if every {@link RenderableAnalyser} in the target spriteGroup matches against the
     * given conditional function.
     *
     * @param label label
     * @param func  conditional function
     * @return if every {@link RenderableAnalyser} in the target spriteGroup matches against the
     * given conditional function.
     */
    public boolean every(String label, Predicate<RenderableAnalyser> func) {
        assert !label.isEmpty();
        assert func != null;
        for (final RenderableAnalyser analyser : getBySpriteGroup(label)) {
            if (!func.test(analyser)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks the given function against the target spriteGroup and returns how many of the {@link
     * RenderableAnalyser} fulfill that conditional function
     */
    public int count(String label, Predicate<RenderableAnalyser> func) {
        assert !label.isEmpty();
        assert func != null;
        int count = 0;
        for (final RenderableAnalyser analyser : getBySpriteGroup(label)) {
            if (func.test(analyser)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks the given function against the target spriteGroup and returns any of the {@link
     * RenderableAnalyser}s fulfill the conditional function condition.
     *
     * @param label spriteGroup label we use to help filter to what we want to filter further
     * @param func  - conditional func we use to assess whether we wish the time being checked by the
     *              func should be a part of the returned {@link ArrayList}
     * @return returns any of the {@link RenderableAnalyser}s fulfill the conditional function
     * condition.
     */
    public List<RenderableAnalyser> filter(String label, Predicate<RenderableAnalyser> func) {
        assert !label.isEmpty();
        assert func != null;
        final List<RenderableAnalyser> result = new ArrayList<>();
        for (final RenderableAnalyser analyser : getBySpriteGroup(label)) {
            if (func.test(analyser)) {
                result.add(analyser);
            }
        }
        return result;
    }

    /**
     * Return a list of RenderableAnalysers that existed during the given frame.
     * @param targetFrame
     * @return
     */
    public List<RenderableAnalyser> getFrame(int targetFrame) {
        assert targetFrame >= 0;
        final List<RenderableAnalyser> result = new ArrayList<>();
        for (final RenderableAnalyser analyser : this.getAll()) {
            if (analyser.wasInFrame(targetFrame)) {
                result.add(analyser);
            }
        }
        return result;
    }

    /**
     * <p>WARNING: this is to handle an explicit edge case where a common entity instead of being updated between frames was created a new every frame.</p>
     *
     * <p>Problem: This would mean each Renderable had its own unique UUID rather then a shared one we could use to group frames of data together.</p>
     * <p>We can solve this in some limited cases by grouping
     * together in chronological order all frame data for an entire sprite group
     * and creating a new merged RenderableAnalyser from the composite data.</p>
     * <p>This will only work as intended if:</p>
     * <ul>
     * <li>There is only one "entity" that used this sprite Group i.e a player or a score value</li>
     * <li>The Merged RenderableAnalyser will use the id from the first frames RenderableAnalyser for all frames.</li>
     * <li>
     *     Be careful and intentional using this method there are many strange edge cases you could have including:
     *     <ol>
     *         <li>Creating a RenderableAnalyser with multiple FrameRecords for the same tick.</li>
     *         <li>Creating a RenderableAnalyser with nonsensical movement patterns due
     *         to it being recorded as rendering at multiple places on the screen at once</li>
     *     </ol>
     * </li>
     * </ul>
     *
     * @return a single merged {@link RenderableAnalyser}
     */
    public RenderableAnalyser mergeBySpriteGroup(String group) {
        assert !group.isEmpty();
        final List<RenderableAnalyser> analysers = getBySpriteGroup(group);
        if (analysers.isEmpty()) {
            return null;
        }
        //we will use the first renderables id for this merged renderable analyser
        final String id = analysers.getFirst().getId();
        final RenderableAnalyser merged = new RenderableAnalyser(id);
        for (RenderableAnalyser analyser : analysers) {
            for (FrameRecord frame : analyser.getFrames()) {
                merged.addUnsafeRawFrameData(frame);
            }
        }
        return merged;
    }



    /**
     * For the given spriteGroup run the given Consumer Function against each {@link RenderableAnalyser}.
     * Intended for running simple assertions against all renderable analysers.
     * @param label
     * @param func
     */
    public void each(String label, Consumer<RenderableAnalyser> func) {
        assert !label.isEmpty();
        assert func != null;
        for (final RenderableAnalyser analyser : getBySpriteGroup(label)) {
            func.accept(analyser);
        }
    }

    /**
     * Checks the given function against the target spriteGroup and returns if any of the {@link
     * RenderableAnalyser} fulfill that conditional function.
     */
    public boolean some(String label, Predicate<RenderableAnalyser> func) {
        assert !label.isEmpty();
        assert func != null;
        return this.count(label, func) > 0;
    }

    /**
     * Returns all unique sprites rendered at least once for the given SpriteGroup.
     * @param label
     * @return
     */
    public Set<Sprite> getUniqueSpritesFor(String label) {
        assert !label.isEmpty();
        final List<RenderableAnalyser> things = this.getBySpriteGroup(label);
        final HashSet<Sprite> uniqueSprites = new HashSet<>();
        for (final RenderableAnalyser entry : things) {
            uniqueSprites.addAll(entry.allUniqueSprites());
        }
        return uniqueSprites;
    }
}
