import javafx.util.Pair;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;

/**
 * This class represents the model of the program.
 * It hold the logic for the program and is responsible for notifying the view.
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public class Model {

    private static final int TOTAL_UNDO = 3;
    private final ArrayList<ChangeListener> observers;
    private int undoCounter = 0;
    private boolean undoOccurred;
    private ArrayList<PitStones> clusters;
    private ArrayList<PitStones> cfrUndo;
    private Mancala storeA;
    private Mancala storeAUndo;
    private Mancala storeB;
    private Mancala storeBUndo;
    private boolean aTurn;
    private boolean bTurn;
    private boolean aUndo;
    private boolean bUndo;
    private Player savePlayer;
    private int givenStones;
    private BoardStrategy design;

    /**
     * Constructor for the Model class.
     * Responsible for initializing variables.
     *
     * @param stoneCount the initial count of stones.
     */
    public Model(int stoneCount) {
        clusters = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            clusters.add(i, new PitStones(stoneCount));
        }

        design = getDesign();
        storeA = new Mancala();
        storeB = new Mancala();
        observers = new ArrayList<>();
        aTurn = true;
        bTurn = true;
        aUndo = aTurn;
        bUndo = bTurn;
        cfrUndo = new ArrayList<>();
        givenStones = stoneCount;
    }

    /**
     * @return the count of stones.
     */
    public int getGivenStone() {
        return this.givenStones;
    }

    /**
     * setter for stoneCount
     *
     * @param stone the new count for stone count.
     */
    public void setGivenStone(int stone) {
        this.givenStones = stone;
    }

    /**
     * @return the board design(Strategy).
     */
    public BoardStrategy getDesign() {
        return this.design;
    }

    /**
     * getter for the board design (Strategy)
     *
     * @param design the new Strategy
     */
    public void setDesign(BoardStrategy design) {
        this.design = design;
    }

    /**
     * Get number of stones in the respective Mancala for the given Player
     *
     * @param player Player ONE or TWO
     * @return the number of stone in the mancala of given player;
     */
    public int getMancalaStones(Player player) {
        if (player.equals(Player.ONE)) {
            return storeA.getStone();
        } else {
            return storeB.getStone();
        }
    }

    /**
     * returns the number of stones for the given pit.
     *
     * @param spot the spot position.
     * @return the number of stones in the given pit.
     */
    public int getPitStones(int spot) {
        return clusters.get(spot).getCount();
    }

    public boolean getaTurn() {
        return (this.aTurn);
    }

    public boolean getbTurn() {
        return (this.bTurn);
    }

    /**
     * @return the number of undo's left.
     */
    public int getUndoCount() {
        return this.undoCounter;
    }

    /**
     * Add Observer.
     *
     * @param l
     */
    public void addObserver(ChangeListener l) {
        observers.add(l);
    }

    /**
     * Update the Change Listeners for the view.
     */
    public void update() {
        for (ChangeListener l : observers) {
            l.stateChanged(new ChangeEvent(this));
        }
    }

    /**
     * Is responsible the the undo action.
     *
     * @return
     */
    public boolean undo() {
        if (this.undoOccurred) {
            return false;
        } else if (this.undoCounter >= TOTAL_UNDO) {
            return false;
        }
        clusters = cfrUndo;
        storeA = storeAUndo;
        storeB = storeBUndo;
        aTurn = aUndo;
        bTurn = bUndo;
        this.update();
        this.undoCounter++;
        this.undoOccurred = true;
        return true;
    }

    /**
     * @return the list of pits for Player ONE
     */
    public ArrayList<PitStones> getApits() {
        ArrayList<PitStones> a = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            a.add(clusters.get(i));
        }
        return a;
    }

    /**
     * @return the Mancala for Player ONE
     */
    public Mancala getAmancala() {
        return storeA;
    }

    /**
     * @return the Mancala for Player TWO
     */
    public Mancala getBmancala() {
        return storeB;
    }

    /**
     * This method is responsible for the movement of the stones on the board.
     *
     * @param player Current Player
     * @param pit    Selected Pit
     */
    public void play(Player player, int pit) {

        int value = getPitStones(pit);
        if (value == 0) {
            return;
        }
        if (savePlayer == null) {
            savePlayer = player;
        }
        if (this.savePlayer != player) {
            this.undoCounter = 0;
        }
        this.saveForUndo(player);
        int nextPit = pit + 1;
        if (player == Player.ONE) {
            aTurn = false;
            bTurn = true;
            clusters.get(pit).removeStones();
            while (nextPit <= 5 && value > 0) {
                if (value == 1 && getApits().get(nextPit).isEmpty()) {
                    int captured = clusters.get(11 - nextPit).removeStones();
                    getAmancala().addStone(captured + 1);

                } else {
                    clusters.get(nextPit).addStone();
                }
                value--;
                nextPit++;
            }
        } else {
            bTurn = false;
            aTurn = true;
            clusters.get(pit).removeStones();
            while (nextPit <= 11 && value > 0) {
                //clusters.get(pit).removeStones();
                if (value == 1 && clusters.get(nextPit).isEmpty()) {
                    int captured = clusters.get(11 - nextPit).removeStones();
                    getBmancala().addStone(captured + 1);

                } else {
                    clusters.get(nextPit).addStone();
                }
                value--;
                nextPit++;
            }

        }

        if (value > 0) {
            if (player == Player.ONE) {
                if (value == 1) {
                    getAmancala().addStone(1);
                    aTurn = true;
                    bTurn = false;
                } else {
                    getAmancala().addStone(1);
                }
            } else {
                if (value == 1) {
                    getBmancala().addStone(1);
                    bTurn = true;
                    aTurn = false;
                } else {
                    getBmancala().addStone(1);
                }
            }
            value--;

        }

        //if still there is stone left go through oponent pit        
        if (value > 0) {
            if (player == Player.ONE) {
                nextPit = 6;
                while (value > 0 && nextPit < 12) {
                    clusters.get(nextPit).addStone();
                    value--;
                    nextPit++;
                }
            } else {
                nextPit = 0;
                while (value > 0 && nextPit < 5) {
                    clusters.get(nextPit).addStone();
                    value--;
                    nextPit++;
                }
            }

        }
        if (value > 0) {
            if (player == Player.ONE) {
                nextPit = 0;
                while (nextPit <= 5 && value > 0) {
                    if (value == 1 && clusters.get(nextPit).isEmpty()) {
                        int captured = clusters.get(11 - nextPit).removeStones();
                        getAmancala().addStone(captured + 1);

                    } else {
                        clusters.get(nextPit).addStone();
                    }
                    value--;
                    nextPit++;
                }

            } else {
                nextPit = 6;
                while (nextPit <= 11 && value > 0) {
                    if (value == 1 && clusters.get(nextPit).isEmpty()) {
                        int captured = clusters.get(11 - nextPit).removeStones();
                        getBmancala().addStone(captured + 1);

                    } else {
                        clusters.get(nextPit).addStone();
                    }
                    value--;
                    nextPit++;
                }
            }
        }
        if (value > 0) {
            if (player == Player.ONE) {
                if (value == 1) {
                    getAmancala().addStone(1);
                    aTurn = true;
                } else {
                    getAmancala().addStone(1);
                }
            } else {
                if (value == 1) {
                    getBmancala().addStone(1);
                    bTurn = true;
                } else {
                    getBmancala().addStone(1);
                }
            }
            value--;

        }

        this.update();
        this.undoOccurred = false;
    }

    /**
     * This method is responsible for saving the current state of the board(data structure) before playing the turn.
     *
     * @param player
     */
    public void saveForUndo(Player player) {
        cfrUndo = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            cfrUndo.add(new PitStones(clusters.get(i).getCount()));
        }
        storeAUndo = new Mancala();
        storeAUndo.setStone(storeA.getStone());
        storeBUndo = new Mancala();
        storeBUndo.setStone(storeB.getStone());
        if (player == Player.ONE) {
            savePlayer = Player.ONE;
        } else {
            savePlayer = Player.TWO;
        }
        aUndo = aTurn;
        bUndo = bTurn;
    }

    /**
     * Check if the game is over. If game is over, pair.getLeft(boolean) == true
     * and pair.getRight(Player) = Player.ONE or Player.TWO
     *
     * @return a Pair consisting information about Game.
     */
    public Pair<Boolean, Player> isGameOver() {
        boolean gameOver = true;
        Player player = null;
        for (int i = 0; i < 6; i++) {
            if (!clusters.get(i).isEmpty()) {
                gameOver = false;
                break;
            }
        }
        if (gameOver) {
            for (int i = 6; i < 12; i++) {
                getBmancala().addStone(clusters.get(i).getCount());
                clusters.get(i).setStones(0);
            }
        } else {
            gameOver = true;
            for (int i = 6; i < 12; i++) {
                if (!clusters.get(i).isEmpty()) {
                    gameOver = false;
                    break;
                }
            }
            if (gameOver) {
                for (int j = 0; j < 6; j++) {
                    getAmancala().addStone(clusters.get(j).getCount());
                    clusters.get(j).setStones(0);
                }
            }
        }
        if (gameOver) {
            this.update();
            if (getAmancala().getStone() < getBmancala().getStone()) {
                player = Player.TWO;
            } else {
                player = Player.ONE;
            }
        }
        return new Pair<>(gameOver, player);

    }

}
