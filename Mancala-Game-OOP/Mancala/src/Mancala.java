/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Defines the shape of the Mancala
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public class Mancala {
    private int totalstone;

    /**
     * Initialize mancala with 0 stones.
     */
    public Mancala() {
        this.totalstone = 0;
    }

    /**
     * Add the given number of stones to the mancala;
     *
     * @param stone number of stones to be added.
     */
    public void addStone(int stone) {
        this.totalstone += stone;
    }

    /**
     * get the number of stones in the mancala.
     *
     * @return count of stones.
     */
    public int getStone() {
        return this.totalstone;
    }

    /**
     * Change the number of stones in the mancala to the given new count.
     *
     * @param stone the new count of stones.
     */
    public void setStone(int stone) {
        this.totalstone = stone;
    }
}
