import java.awt.Color;
public class Player {
    private int id;
    private String name;
    private int position; 
    private Color color;
    public Player(int id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.position = 1; 
    }
    public void moveForward() { if (this.position < 64) this.position++; }
    public void moveBackward() { if (this.position > 1) this.position--; }
    public int getPosition() { return position; }
    public void setPosition(int pos) { this.position = pos; }
    public Color getColor() { return color; }
    public int getId() { return id; }
    public String getName() { return name; }
}