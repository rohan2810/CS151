import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * This class is taken from Hortsmann's solutions.
 * New implementation: Created a new MouseListener object and implemented the mousePressed method.
 * A class that implements an Observer object that displays a barchart view of
 * a data model.
 */
public class BarFrame extends JFrame implements ChangeListener {
    private static final int ICON_WIDTH = 200;
    private static final int ICON_HEIGHT = 200;
    private final DataModel dataModel;
    private ArrayList<Double> a;

    /**
     * Constructs a BarFrame object
     *
     * @param dataModel the data that is displayed in the barchart
     */
    public BarFrame(DataModel dataModel) {
        this.dataModel = dataModel;
        a = dataModel.getData();

        setLocation(0, 200);
        setLayout(new BorderLayout());

        Icon barIcon = new Icon() {
            public int getIconWidth() {
                return ICON_WIDTH;
            }

            public int getIconHeight() {
                return ICON_HEIGHT;
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;

                g2.setColor(Color.red);

                double max = a.get(0);
                for (Double v : a) {
                    double val = v;
                    if (val > max)
                        max = val;
                }

                double barHeight = getIconHeight() / a.size();

                int i = 0;
                for (Double v : a) {
                    double value = v;

                    double barLength = getIconWidth() * value / max;

                    Rectangle2D.Double rectangle = new Rectangle2D.Double
                            (0, barHeight * i, barLength, barHeight);
                    i++;
                    g2.fill(rectangle);
                }
            }
        };
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                double X = e.getX();
                double Y = e.getY();
                double barHeight = ICON_HEIGHT / a.size();
                dataModel.update((int) (Y / barHeight), X);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
        add(new JLabel(barIcon));
        addMouseListener(mouseListener);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * Called when the data in the model is changed.
     *
     * @param e the event representing the change
     */
    public void stateChanged(ChangeEvent e) {
        a = dataModel.getData();
        repaint();
    }
}