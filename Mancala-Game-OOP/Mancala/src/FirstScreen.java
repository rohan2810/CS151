import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Initial screen for the program.
 * This loads up when the main method of the tester is called.
 * It prompts the user to input his selection for number of stones per pit.
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public class FirstScreen extends JFrame {

    private final JToggleButton option1;
    private final JToggleButton option2;
    private final JButton next;
    private Model m;

    /**
     * Initializes the FirstScreen prompting the user to make a selection for the number of the stones to be placed in each pit on the board.
     */
    public FirstScreen() {

        JLabel title = new JLabel(" Mancala Board Game ");
        Font f = new Font(Font.SANS_SERIF, Font.BOLD, 15);
        title.setFont(f);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.getContentPane().add(title);

        JPanel panel = makeFrame(this);

        option1 = new JToggleButton(" 3 Stones pit ");
        option1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int stones = 3;
                m = new Model(stones);
                m.setGivenStone(stones);
                option2.setSelected(false);

            }
        });
        panel.add(option1);

        option2 = new JToggleButton(" 4 Stones pit ");
        option2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int stones = 4;
                m = new Model(stones);
                m.setGivenStone(stones);
                option1.setSelected(false);

            }
        });
        panel.add(option2);

        next = new JButton(" NEXT->");
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((!option1.getModel().isSelected() && !option2.getModel().isSelected())) {

                    JFrame error = new JFrame("Error");
                    JOptionPane.showMessageDialog(error, "Select stones number", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    SecondScreen sc = new SecondScreen();
                    dispose();
                    sc.setVisible(true);
                }

            }
        });
        next.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.getContentPane().add(next);
        panel.setBorder(BorderFactory.createTitledBorder("Choose an option from below"));
    }

    /**
     * This method is responsible for creating the panel given the JFrame.
     *
     * @param f JFrame
     * @return JPanel made out of the given JFrame.
     */
    public JPanel makeFrame(JFrame f) {
        f.setTitle("ManCala Board Game");
        f.setSize(500, 300);
        f.getContentPane().setBackground(Color.lightGray);
        f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        JPanel panel = new JPanel();
        panel.setBackground(Color.cyan);

        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setPreferredSize(new Dimension(400, 100));
        panel.setMaximumSize(new Dimension(400, 100));

        f.getContentPane().add(panel);
        f.setVisible(true);
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return panel;
    }

    /**
     * Second screen for the program which shows up after a selection is made on the first screen.
     * This loads up when a selection is made on the first screen.
     * It prompts the user to input his selection for design of the board.
     */
    class SecondScreen extends JFrame {

        private final JToggleButton board1;
        private final JToggleButton board2;
        private final JButton start;

        /**
         * Initializes the SecondScreen prompting the user to make a selection for the design of the board.
         */
        public SecondScreen() {

            JPanel panel = makeFrame(this);
            board1 = new JToggleButton(" Design 1 ");
            panel.add(board1);
            board1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    BoardStrategy circle = new CircleDesign();
                    m.setDesign(circle);
                    board2.setSelected(false);
                }
            });

            board2 = new JToggleButton(" Design 2 ");
            panel.add(board2);
            board2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    BoardStrategy triangle = new RectangleDesign();
                    m.setDesign(triangle);
                    board1.setSelected(false);
                }
            });

            start = new JButton(" START ");
            start.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.add(start);
            start.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!board1.isSelected() && !board2.isSelected()) {
                        JFrame error = new JFrame("Error");
                        JOptionPane.showMessageDialog(error, "Select a design", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        MancalaBoard game = new MancalaBoard(m.getDesign(), m.getGivenStone());
                        dispose();

                    }


                }
            });

            panel.setBorder(BorderFactory.createTitledBorder("Choose a board design from below"));

        }
    }

}
