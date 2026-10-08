package test_utils.analysers;


/**
 * Intended to be used as a very basic 2dVector referred to as XyPair to be less intimidating to
 * students who review the tests.
 */
public class XyPair {

    private final int x;
    private final int y;

    /**
     * Constructs a new {@link XyPair} using the given coordinates.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public XyPair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Returns a String representation of the internal state of this {@link XyPair}.
     *
     * @return a String representation of the internal state of this {@link XyPair}.
     */
    @Override
    public String toString() {
        return "XyPair[x: " + x + ", y: " + y + "]";
    }
}
