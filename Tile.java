public class Tile {
    private int number;
    private int destination; 
    private int points; // Atribut Poin

    public Tile(int number) {
        this.number = number;
        this.destination = number; 
        this.points = 1; // Default 1
    }

    public int getNumber() { return number; }
    
    public int getDestination() { return destination; }
    public void setDestination(int destination) { this.destination = destination; }
    
    public boolean isLadder() { return destination > number; }

    // Getter & Setter Poin
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}