import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

// this class is responsible for creating a car object and implements the ChangeListener Interface
public class CarChangeListener extends JLabel implements ChangeListener {
    private int icon_width;
    private int icon_height;

    public CarChangeListener(int width, int height) {
        icon_width = width;
        icon_height = height + width;

        Icon carIcon = new Icon() {
            public int getIconWidth() {
                return icon_width;
            }

            public int getIconHeight() {
                return icon_height;
            }

            // this method is borrowed from textbook
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;

                Rectangle2D.Double body =
                        new Rectangle2D.Double(x, y + icon_width / 6, icon_width - 1, icon_width / 6);
                Ellipse2D.Double frontTire =
                        new Ellipse2D.Double(x + icon_width / 6, y + icon_width / 3, icon_width / 6, icon_width / 6);
                Ellipse2D.Double rearTire =
                        new Ellipse2D.Double(x + icon_width * 2 / 3, y + icon_width / 3, icon_width / 6, icon_width / 6);
                Point2D.Double r1 =
                        new Point2D.Double(x + icon_width / 6, y + icon_width / 6);
                Point2D.Double r2 =
                        new Point2D.Double(x + icon_width / 3, y);
                Point2D.Double r3 =
                        new Point2D.Double(x + icon_width * 2 / 3, y);
                Point2D.Double r4 =
                        new Point2D.Double(x + icon_width * 5 / 6, y + icon_width / 6);
                Line2D.Double frontWindshield =
                        new Line2D.Double(r1, r2);
                Line2D.Double roofTop =
                        new Line2D.Double(r2, r3);
                Line2D.Double rearWindshield =
                        new Line2D.Double(r3, r4);

                g2.fill(frontTire);
                g2.fill(rearTire);
                g2.setColor(Color.red);
                g2.fill(body);
                g2.draw(frontWindshield);
                g2.draw(roofTop);
                g2.draw(rearWindshield);
            }
        };

        setIcon(carIcon);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        icon_width = slider.getValue();
        this.setSize(icon_width, icon_height);
        repaint();
    }
}
