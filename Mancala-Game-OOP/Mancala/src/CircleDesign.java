import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;


/**
 * This is the first designs of the board(Strategy 1).
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public class CircleDesign implements BoardStrategy {
    /**
     * @return the pit and mancala color of the first strategy.
     */
    @Override
    public Color PitandMancalaColorA() {
        return Color.red;
    }

    @Override
    public Color PitandMancalaColorB() {
        return Color.green;
    }

    /**
     * @return the Pit shape of the first strategy.
     */
    @Override
    public Shape pitShape() {
        return new Ellipse2D.Double(5, 5, ConstantValue.PIT_WIDTH.getValue(), ConstantValue.PIT_WIDTH.getValue());
    }

    /**
     * @return the mancala shape of the first strategy.
     */
    @Override
    public Shape mancalaShape() {
        return new RoundRectangle2D.Double(10, 5, ConstantValue.MANCALA_WIDTH.getValue() - 50, ConstantValue.MANCALA_HEIGHT.getValue() - 100, 40, 40);
    }


}
