import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * This is the second design of the board(Strategy 2).
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public class RectangleDesign implements BoardStrategy {
    /**
     * @return the color of the board of the second strategy.
     */
    @Override
    public Color PitandMancalaColorA() {
        return Color.blue;
    }

    @Override
    public Color PitandMancalaColorB() {
        return Color.MAGENTA;
    }

    /**
     * @return the pit shape of the second strategy.
     */
    @Override
    public Shape pitShape() {
        return new Rectangle2D.Double(5, 5, ConstantValue.PIT_WIDTH.getValue(), ConstantValue.PIT_WIDTH.getValue());
    }

    /**
     * @return the mancala shape of the second strategy.
     */
    @Override
    public Shape mancalaShape() {
        return new Ellipse2D.Double(10, 5, ConstantValue.MANCALA_WIDTH.getValue() - 50, ConstantValue.MANCALA_HEIGHT.getValue() - 100);
    }

}
