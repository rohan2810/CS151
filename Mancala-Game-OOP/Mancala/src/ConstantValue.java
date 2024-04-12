/**
 * An enumeration for all the constants used for creating the board, mancala, and the pit dimensions.
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public enum ConstantValue {

    BOARD_WIDTH(1000),
    BOARD_HEIGHT(600),
    MANCALA_WIDTH(200),
    MANCALA_HEIGHT(600),
    PIT_WIDTH(100),
    PIT_HEIGHT(200);

    private final double value;

    ConstantValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

}
