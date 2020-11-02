import javax.swing.*;
import java.awt.*;

/**
 * This class shows two buttons i.e. 'Zoom In' and 'Zoom Out'.
 * Functions accordingly to the user choice
 */
public class ZoomTester {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        int ICON_WIDTH = 500;
        CarIcon carIcon = new CarIcon(ICON_WIDTH);

        final JButton zoom_in = new JButton("ZOOM IN");
        final JButton zoom_out = new JButton("ZOOM OUT");
        final JLabel jLabel = new JLabel(carIcon);

        zoom_in.addActionListener(event -> {
            carIcon.setWidth(carIcon.getIconWidth() + 30);
            jLabel.repaint();
        });

        zoom_out.addActionListener(event -> {
            carIcon.setWidth(carIcon.getIconWidth() - 30);
            jLabel.repaint();
        });

        jFrame.add(zoom_in);
        jFrame.add(zoom_out);
        jFrame.add(jLabel);
        jFrame.setLayout(new FlowLayout());
        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }
}

