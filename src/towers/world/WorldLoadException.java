package towers.world;

/**
 * Signals that {@link WorldBuilder} was given a map encoding it could not make sense of.
 *
 * <p>Since the problem is always somewhere in a file, this exception carries the location of the
 * offending character as well as an explanation.
 *
 * <p>Note: Rows and columns are stored zero-based and reported one-based.
 */
public class WorldLoadException extends Exception {
    /**
     * The zero-based row of the map where loading failed, or -1 if no row is known.
     */
    private int row = -1;

    /**
     * The zero-based column of the map where loading failed, or -1 if no column is known.
     */
    private int col = -1;

    /**
     * Construct an exception describing a problem with the map as a whole.
     *
     * @param message Explanation of the problem that occurred.
     * @ensures getMessage() is the given message, unchanged.
     */
    public WorldLoadException(String message) {
        super(message);
    }

    /**
     * Construct an exception describing a problem with one row of the map, such as a line of the
     * wrong length.
     *
     * @param message Explanation of the problem that occurred.
     * @param row     The zero-based row where the error occurred, reported as line row + 1.
     * @requires row >= 0
     */
    public WorldLoadException(String message, int row) {
        super(message);
        this.row = row;
    }

    /**
     * Construct an exception describing a problem with one character of the map, such as a symbol
     * that is not a tile.
     *
     * @param message Explanation of the problem that occurred.
     * @param row     The zero-based row where the error occurred, reported as line row + 1.
     * @param column  The zero-based column where the error occurred, reported as character
     *                column + 1.
     * @requires row >= 0 and column >= 0
     */
    public WorldLoadException(String message, int row, int column) {
        super(message);
        this.row = row;
        col = column;
    }

    /**
     * Return the explanation, with the location appended if one is known.
     *
     * <p>Rows and columns are counted from zero internally but reported from one, so the numbers
     * match what a text editor shows. The result is the message alone, or suffixed with
     * {@code " on line N"} or {@code " on line N, character M"}.</p>
     *
     * @return The explanation, optionally suffixed with where loading failed.
     */
    @Override
    public String getMessage() {
        if (row != -1 && col != -1) {
            return super.getMessage() + " on line " + (row + 1) + ", character " + (col + 1);
        }
        if (row != -1) {
            return super.getMessage() + " on line " + (row + 1);
        }
        return super.getMessage();
    }
}

