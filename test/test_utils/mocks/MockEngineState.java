package test_utils.mocks;

import engine.Engine;
import engine.EngineState;
import engine.input.KeyState;
import engine.input.MouseState;
import engine.renderer.Spatial;

import java.util.ArrayList;
import java.util.List;


public class MockEngineState implements EngineState {
    private MouseState mouse;
    private KeyState keys;

    private int frame;

    public MockEngineState() {
        this.mouse = new MockMouse(2, 2, false, false, false);
        this.keys = new MockKeys(List.of());
        this.frame = 0;
    }

    public MockEngineState(KeyState keys) {
        this();
        this.keys = keys;
    }

    public MockEngineState(KeyState keys, int frame) {
        this(keys);
        this.frame = frame;
    }

    public MockEngineState(int frame) {
        this();
        this.frame = frame;
    }

    public MockEngineState(MouseState mouse) {
        this();
        this.mouse = mouse;
    }

    public MockEngineState(MouseState mouse, KeyState keys) {
        this();
        this.mouse = mouse;
        this.keys = keys;
    }

    public MockEngineState(MouseState mouse, KeyState keys, int frame) {
        this();
        this.mouse = mouse;
        this.keys = keys;
        this.frame = frame;
    }

    public MockEngineState withFrame(int frame) {
        return new MockEngineState(mouse, keys, frame);
    }

    public MockEngineState press(char key) {
        final List<Character> keys = new ArrayList<>();
        keys.add(key);
        return new MockEngineState(mouse, new MockKeys(keys));
    }

    public MockEngineState leftClick() {
        final MouseState newMouse =
                new MockMouse(
                        mouse.getX(),
                        mouse.getY(),
                        true,
                        mouse.isRightPressed(),
                        mouse.isMiddlePressed());
        return new MockEngineState(newMouse, keys);
    }

    @Override
    public Spatial getSpatial() {
        return Engine.getSpatial();
    }

    @Override
    public MouseState getMouse() {
        return mouse;
    }

    @Override
    public KeyState getKeys() {
        return keys;
    }

    @Override
    public int currentTick() {
        return frame;
    }
}
