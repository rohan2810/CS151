import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * This class is taken from Prof. Horstmann book
 * An icon that contains a moveable shape.
 */
public class ShapeIcon implements Icon {
    private int width;
    private int height;
    private MoveableShape shape;
    private ArrayList<MoveableShape> moveableShapes;

    public ShapeIcon(MoveableShape shape,
                     int width, int height) {
        this.shape = shape;
        this.width = width;
        this.height = height;
        this.moveableShapes = new ArrayList<>();
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        shape.draw(g2);
    }
}