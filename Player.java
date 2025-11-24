import java.awt.Color;

public class Player {
    private int id;
    private int position; 
    private Color color;

    public Player(int id, Color color) {
        this.id = id;
        this.color = color;
        this.position = 1; 
    }

    public void moveForward() {
        if (this.position < 64) {
            this.position++;
        }
    }

    // LOGIKA BARU: Mundur (Mentok di 1)
    public void moveBackward() {
        if (this.position > 1) {
            this.position--;
        }
    }

    public int getPosition() { return position; }
    public void setPosition(int pos) { this.position = pos; }
    public Color getColor() { return color; }
    public int getId() { return id; }
}