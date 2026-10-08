package test_utils;

import engine.Engine;
import engine.EngineState;
import engine.game.Game;
import engine.game.GridPosition;
import engine.input.KeyState;
import engine.input.MouseState;
import engine.renderer.Renderable;
import test_utils.analysers.AnalyserManager;

import test_utils.mocks.HeadlessCore;
import test_utils.mocks.MockEngineState;
import test_utils.mocks.MockKeys;
import test_utils.mocks.MockMouse;

import java.util.ArrayList;
import java.util.HashMap;

public class Simulation {
    private boolean headless = false;
    private int fps = 800;
    private final long TARGET_TIME = 1000 / fps;
    private long lastTickTime = System.currentTimeMillis();
    private int ticks = 10000;
    private MockMouse defaultMouse = new MockMouse(new GridPosition(0, 0));
    private MockKeys defaultKeys = new MockKeys(new ArrayList<>());
    private final HashMap<Integer, MockMouse> frameSpecificMouseMocks = new HashMap<>();
    private final HashMap<Integer, MockKeys> frameSpecificKeysMocks = new HashMap<>();
    private boolean debug = false;
    private final Game runnable;

    public Simulation(Game runnable, int tickTarget) {
        this.runnable = runnable;
        this.ticks = tickTarget;
    }

    private boolean isTimeForNextTick() {
        if (headless) { //when run headless we want to tick as fast as we can
            return true;
        }
        if (fps > 2000) { //if we want a fps above 2000, it would only be
            // for an arbitrary "go fast" number, just say it's always time for next tick
            return true;
        }
        final long currentTime = System.currentTimeMillis();
        if (currentTime - lastTickTime >= TARGET_TIME) {
            lastTickTime = currentTime;
            return true;
        }
        return false;
    }

    public Simulation enableHeadless() {
        headless = true;
        return this;
    }

    public Simulation setTicks(int ticks) {
        assert ticks > 0; //ticks 0 or less would be nonsensical
        this.ticks = ticks;
        return this;
    }

    public Simulation setFps(int fps) {
        assert fps > 0; //fps 0 or less is nonsensical
        this.fps = fps;
        return this;
    }

    public Simulation setDefaultMouseState(MockMouse mouse) {
        assert mouse != null;
        defaultMouse = mouse;
        return this;
    }

    public Simulation setFrameSpecificMouse(int frameIndex, MockMouse mouse) {
        assert frameIndex >= 0; //a frameIndex less than 0 would be nonsensical
        assert mouse != null;
        frameSpecificMouseMocks.put(frameIndex, mouse);
        return this;
    }

    public void setFrameSpecificKeys(int frameIndex, MockKeys keys) {
        assert frameIndex >= 0; //a frameIndex less than 0 would be nonsensical
        assert keys != null;
        frameSpecificKeysMocks.put(frameIndex, keys);
    }

    public void setDefaultKeyState(MockKeys keys) {
        assert keys != null;
        defaultKeys = keys;
    }

    /**
     * Returns an engine instance for use by the sim, this engine instance can be initted
     * as headless or non headless.
     *
     * @return
     */
    public Engine initEngine() {
        if (!headless) {
            final Engine engine = new Engine(runnable);
            if (debug) {
                engine.debug().on();
                engine.debug().enablePath();
            }
            return engine;
        }
        final Engine engine = new Engine(runnable, new HeadlessCore());
        return engine;
    }

    public MockMouse getMouseState(int frameIndex) {
        assert frameIndex >= 0; //a frame index less is nonsensical
        if (frameSpecificMouseMocks.containsKey(frameIndex)) {
            return frameSpecificMouseMocks.get(frameIndex);
        }
        return defaultMouse;
    }

    public MockKeys getKeyState(int frameIndex) {
        assert frameIndex >= 0; //a frame index less is nonsensical
        if (frameSpecificKeysMocks.containsKey(frameIndex)) {
            return frameSpecificKeysMocks.get(frameIndex);
        }
        return defaultKeys;
    }

    public AnalyserManager run() {
        final AnalyserManager timeline = new AnalyserManager();
        final Engine engine = initEngine();
        int i = 0;
        while (i < ticks && i < 100000) {
            final MouseState mouse = getMouseState(i);
            final KeyState keys = getKeyState(i);
            final EngineState state = new MockEngineState(mouse, keys, i);
            if (isTimeForNextTick()) {
                engine.tick(state);
                for (final Renderable renderable : runnable.render()) {
                    timeline.add(i, renderable);
                }
                i += 1;
            }
        }
        return timeline;
    }

    public void enableDebug() {
        debug = true;
    }
}