import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ClockIcon implements Icon {
    private final int width;

    public ClockIcon(int width) {
        this.width = width;
    }

    // took help from online resources for the clock's math calculations
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        Ellipse2D.Double circle = new Ellipse2D.Double(x, y, 200, 200);
        int X = (int) circle.getCenterX();
        int Y = (int) circle.getCenterY();
        g2.draw(circle);

        GregorianCalendar calendar = new GregorianCalendar();
        double hour = 0.5 * (60 * calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE)) + 90;
        double minute = calendar.get(Calendar.MINUTE) * 6 + 90;
        double second = calendar.get(Calendar.SECOND) * 6 + 90;


        g2.drawLine(X, Y, X - (int) (75 * Math.cos(Math.toRadians(minute))), Y - (int) (70 * Math.sin(Math.toRadians(minute))));
        g2.setColor(Color.blue);
        g2.drawLine(X, Y, X - (int) (50 * Math.cos(Math.toRadians(hour))), Y - (int) (50 * Math.sin(Math.toRadians(hour))));
        g2.setColor(Color.red);
        g2.drawLine(X, Y, X - (int) (100 * Math.cos(Math.toRadians(second))), Y - (int) (100 * Math.sin(Math.toRadians(second))));
        g2.setColor(Color.green);

    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return 0;
    }
}
