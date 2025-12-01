public class Tile {
    private int number;
    private int destination; 
    public Tile(int number) {
        this.number = number;
        this.destination = number; 
    }
    public int getNumber() { return number; }
    public int getDestination() { return destination; }
    public void setDestination(int destination) { this.destination = destination; }
    public boolean isLadder() { return destination > number; }
}