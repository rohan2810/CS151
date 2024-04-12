/**
 * This class represents the pit.
 *
 * @author mahmudaakter
 * @author rohansurana
 */
public class PitStones {
    int stoneCount;

    /**
     * Initialize the pit with the given number of stones.
     *
     * @param initialCount
     */
    public PitStones(int initialCount) {
        this.stoneCount = initialCount;
    }

    /**
     * Get the count of the stones in the pit.
     *
     * @return
     */
    public int getCount() {
        return this.stoneCount;
    }

    /**
     * Set the number of stones in the pit to the new number.
     *
     * @param newCount
     */
    public void setStones(int newCount) {
        this.stoneCount = newCount;
    }

    /**
     * Add a stone to the pit.
     */
    public void addStone() {
        this.stoneCount++;
    }

    /**
     * check if the pit is empty.
     *
     * @return is the pit empty?
     */
    public boolean isEmpty() {
        return (this.stoneCount == 0);
    }

    /**
     * Removes all the stones for the given pit.
     */
    public int removeStones() {
        int count = stoneCount;
        this.stoneCount = 0;
        return count;
    }

}
