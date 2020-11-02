import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Most of the code is taken from Prof. Horstmann book
 * New implementation: added a new MovableShape object.
 * This program implements an animation that moves
 * a car shape.
 */
public class AnimationTester {
    private static final int ICON_WIDTH = 400;
    private static final int ICON_HEIGHT = 100;
    private static final int CAR_WIDTH = 100;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        ArrayList<MoveableShape> icons = new ArrayList<>();
        MoveableShape shape = new CarShape(0, 0, CAR_WIDTH);
        MoveableShape shape1 = new CarShape(50, 50, CAR_WIDTH);
        icons.add(shape);
        icons.add(shape1);
        frame.setLayout(new FlowLayout());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        final int DELAY = 100;
        Timer t = new Timer(DELAY, event ->
        {
            for (MoveableShape moveableShape : icons) {
                moveableShape.move();
                JLabel temp = new JLabel(new ShapeIcon(moveableShape, ICON_WIDTH, ICON_HEIGHT));
                frame.add(temp);
                frame.setSize(1000, 500);
                frame.repaint();
            }
        });
        t.start();
    }
}