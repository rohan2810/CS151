import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * This class is for Mancala main game board
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public class MancalaBoard extends JFrame {

    private final Model m;
    private final BoardStrategy design;
    private final int givenstones;
    boolean gameOver = false;

    /**
     * Initializes the game board with the given design and the number of stones in each pit/
     *
     * @param design
     * @param stone
     */
    public MancalaBoard(BoardStrategy design, int stone) {
        m = new Model(stone);
        this.design = design;
        this.givenstones = stone;

        int width = (int) ConstantValue.BOARD_WIDTH.getValue();
        int height = (int) ConstantValue.BOARD_HEIGHT.getValue();
        this.setSize(width + 200, height);

        JPanel pitHoldPanel = new JPanel();
        pitHoldPanel.setLayout(new BorderLayout());
        pitHoldPanel.setPreferredSize(new Dimension(700, 300));

        GridLayout layout = new GridLayout(2, 6);
        layout.setHgap(5);
        layout.setVgap(15);
        JPanel gridPanel = new JPanel();
        gridPanel.setBackground(Color.white);
        gridPanel.setLayout(layout);

        ArrayList<JPanel> eachPitA = new ArrayList<>();
        ArrayList<JPanel> eachPitB = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            eachPitA.add(new PitHoldPanel(Player.ONE, i));
        }

        for (int i = 6; i < 12; i++) {
            eachPitB.add(new PitHoldPanel(Player.TWO, i));
        }

        for (int i = 5; i >= 0; i--) {
            gridPanel.add(eachPitB.get(i));
        }
        for (int i = 0; i < 6; i++) {
            gridPanel.add(eachPitA.get(i));
        }

        pitHoldPanel.add(gridPanel, BorderLayout.CENTER);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(new TopPanel(), BorderLayout.NORTH);
        this.getContentPane().add(new MancalaPanel(Player.TWO), BorderLayout.WEST);
        this.getContentPane().add(pitHoldPanel, BorderLayout.CENTER);
        this.getContentPane().add(new MancalaPanel(Player.ONE), BorderLayout.EAST);

        this.getContentPane().setBackground(Color.white);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * View class for the Panel holding all the Mancala's.
     */
    class MancalaPanel extends JPanel implements ChangeListener {

        private final Player playerM;
        private int totalStones;

        public MancalaPanel(Player player) {
            this.setBackground(Color.white);
            this.setPreferredSize(new Dimension(200, 500));
            this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.yellow));

            m.addObserver(this);

            this.playerM = player;
            totalStones = m.getMancalaStones(player);
        }

        @Override
        public void paintComponent(Graphics g2) {
            super.paintComponent(g2);
            Graphics2D g = (Graphics2D) g2;

            if (playerM == Player.ONE) {
                g.setStroke(new BasicStroke(3.0f));
                g.setColor(design.PitandMancalaColorA());
                g.draw(design.mancalaShape());
            } else {
                g.setStroke(new BasicStroke(3.0f));
                g.setColor(design.PitandMancalaColorB());
                g.draw(design.mancalaShape());
            }

            int widthM = (int) ConstantValue.MANCALA_WIDTH.getValue();
            int heightM = (int) ConstantValue.MANCALA_HEIGHT.getValue();
            int dimension = (int) (Math.sqrt(totalStones) + 1);
            int stoneWidth = widthM / (dimension) - 15;
            int stoneHeight = heightM / (dimension) - 30;
            g.setColor(Color.BLACK);
            for (int i = 0; i < totalStones; i++) {
                int xMod = (i % dimension);
                int yMod = (i / dimension);
                int x = xMod * (stoneWidth + 4) + 10;
                int y = yMod * (stoneHeight + 4) + 5;
                Ellipse2D.Double eachStone = new Ellipse2D.Double(x, y, 10, 10);
                g.fill(eachStone);

            }
            g.setColor(Color.BLACK);
            Font f = new Font("Dialog", Font.BOLD, 15);
            g.setFont(f);
            if (playerM == Player.ONE) {
                g.drawString("Player " + "A", 50, heightM - 125);
            } else {
                g.drawString("Player " + "B", 50, heightM - 125);
            }

        }

        /**
         * Updates the view based on the ChangeEvent.
         *
         * @param e
         */
        public void stateChanged(ChangeEvent e) {
            totalStones = m.getMancalaStones(playerM);
            this.repaint();
        }

    }

    /**
     * View class for the Panel holding all the pits.
     */
    class PitHoldPanel extends JPanel implements ChangeListener {

        private final Player playerP;
        private final int pitNumber;
        private int stonePerpit;

        public PitHoldPanel(Player player, int pitNumber) {
            if (player == Player.ONE) {
                stonePerpit = m.getPitStones(pitNumber);
            } else {
                stonePerpit = m.getPitStones(pitNumber);
            }
            this.setBackground(Color.white);
            this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.yellow));

            m.addObserver(this);
            this.playerP = player;
            this.pitNumber = pitNumber;
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    //m.play(playerP, pitNumber);
                    if (playerP == Player.ONE) {
                        if (m.getaTurn()) {
                            m.play(playerP, pitNumber);
                        } else {
                            JFrame notice = new JFrame("Error");
                            JOptionPane.showMessageDialog(notice, "Turn for opposite player", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        if (m.getbTurn()) {
                            m.play(playerP, pitNumber);
                        } else {
                            JFrame notice = new JFrame("Error");
                            JOptionPane.showMessageDialog(notice, "Turn for opposite player", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    Player winner;
                    if (m.isGameOver().getKey()) {
                        gameOver = true;
                        winner = m.isGameOver().getValue();
                        JFrame fr = new JFrame("Winner");
                        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JOptionPane.showMessageDialog(fr, winner.name() + " is the winner!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    } else {
                        gameOver = false;
                    }
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            if (playerP == Player.ONE) {
                g2.setStroke(new BasicStroke(3.0f));
                g2.setColor(design.PitandMancalaColorA());
                g2.draw(design.pitShape());
            } else {
                g2.setStroke(new BasicStroke(3.0f));
                g2.setColor(design.PitandMancalaColorB());
                g2.draw(design.pitShape());
            }

            int widthM = (int) ConstantValue.PIT_WIDTH.getValue();
            int heightM = (int) ConstantValue.PIT_HEIGHT.getValue();
            int dimension = (int) (Math.sqrt(stonePerpit) + 1);
            int stoneWidth = widthM / (dimension) - 15;
            int stoneHeight = heightM / (dimension) - 30;
            g2.setColor(Color.BLACK);
            for (int i = 0; i < stonePerpit; i++) {
                int xMod = (i % dimension);
                int yMod = (i / dimension);
                int x = xMod * (stoneWidth + 4) + 10;
                int y = yMod * (stoneHeight + 4) + 5;
                Ellipse2D eachStone = new Ellipse2D.Double(x, y, 10, 10);
                g2.fill(eachStone);

            }
            g.setColor(Color.BLACK);
            Font f = new Font("Dialog", Font.BOLD, 15);
            g.setFont(f);

            int number = pitNumber + 1;
            if (playerP == Player.ONE) {
                g.drawString("Player " + "A" + number + "-->", 10, 150);
            } else {
                g.drawString("<--Player " + "B" + number + " ", 10, 150);
            }

        }

        /**
         * Updates the view based on the ChangeEvent.
         *
         * @param e
         */
        @Override
        public void stateChanged(ChangeEvent e) {

            stonePerpit = m.getPitStones(pitNumber);
            this.repaint();

        }

    }

    /**
     * View class for the top panel, that holds the Undo button, and the information about players turn and the number of undo's left.
     */
    class TopPanel extends JPanel implements ChangeListener {

        private final JTextArea playerTurn;
        private final JButton undo;
        private final JTextArea undoCount;
        private final JLabel turnStatus;

        /**
         * Constructor for the TopPanel view.
         */
        public TopPanel() {
            this.setBackground(Color.WHITE);
            m.addObserver(this);
            this.setPreferredSize(new Dimension(20, 65));
            turnStatus = new JLabel(" Player Turn:");
            turnStatus.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
            turnStatus.setSize(100, 50);
            playerTurn = new JTextArea("Player ");
            Font f = new Font(Font.SANS_SERIF, Font.BOLD, 15);
            playerTurn.setFont(f);
            playerTurn.setForeground(Color.red);
            playerTurn.setEditable(false);
            playerTurn.setLineWrap(true);
            playerTurn.setPreferredSize(new Dimension(200, 50));

            int count = m.getUndoCount();
            undoCount = new JTextArea("Undo Count: " + count);
            undoCount.setFont(f);
            undoCount.setForeground(Color.red);
            undoCount.setEditable(false);
            undoCount.setPreferredSize(new Dimension(150, 50));

            undo = new JButton(" UNDO ");
            undo.setSize(50, 10);
            undo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!m.undo()) {
                        JFrame fr = new JFrame("Error");
                        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JOptionPane.showMessageDialog(fr, "You cannot undo anymore.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    int count = m.getUndoCount();
                    undoCount.setText("Undo Count: " + count);
                }
            });

            this.add(undo);
            this.add(undoCount);
            this.add(turnStatus);
            this.add(playerTurn);
            this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.yellow));

        }

        /**
         * Updates the view based on the ChangeEvent.
         *
         * @param e
         */
        public void stateChanged(ChangeEvent e) {

            if (m.getaTurn()) {
                playerTurn.setText("Player 1");
            } else {
                playerTurn.setText("Player 2");
            }
        }
    }
}
