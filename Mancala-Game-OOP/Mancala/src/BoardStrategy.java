import java.awt.*;

/**
 * This interface for strategy pattern of the Mancala board
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public interface BoardStrategy {
    /**
     * @return the Color of the Pit and Mancala.
     */
    Color PitandMancalaColorA();

    Color PitandMancalaColorB();

    /**
     * @return the pit shape.
     */
    Shape pitShape();

    /**
     * @return the mancala shape.
     */
    Shape mancalaShape();

}