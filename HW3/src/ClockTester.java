import javax.swing.*;

public class ClockTester {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Clock Tester");
        frame.setSize(500, 500);

        ClockIcon clockIcon = new ClockIcon(250);
        JLabel label = new JLabel(clockIcon);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

