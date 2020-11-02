import javax.swing.*;

public class SliderTester {
    // main class which is responsible for creating a slider, jFrame, and CarChangeListener object.
    // it attaches the slider and the car object to the frame and is used for testing.

    public static void main(String[] args) {
        JSlider slider = new JSlider(0, 400, 50);
        slider.setMajorTickSpacing(100);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        CarChangeListener shape = new CarChangeListener(100, 100);
        slider.addChangeListener(shape);
        final JFrame frame = new JFrame();
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(500, 500);
        frame.add(slider);
        frame.add(shape);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
