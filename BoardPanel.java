import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanel extends JPanel {
    private Board board;
    private List<Player> players;
    
    // Variabel baru untuk menyimpan jalur Dijkstra
    private List<Integer> hintPath = new ArrayList<>(); 

    // Warna Palet
    private final Color COLOR_TILE_1 = new Color(255, 255, 255);
    private final Color COLOR_TILE_2 = new Color(236, 240, 241);
    private final Color LADDER_COLOR = new Color(46, 204, 113, 180);
    private final Color HINT_COLOR = new Color(241, 196, 15, 200); // Kuning Emas

    public BoardPanel(Board board) {
        this.board = board;
        this.players = new ArrayList<>();
        setPreferredSize(new Dimension(600, 600)); 
        setBackground(Color.WHITE);
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        repaint();
    }
    
    // Method baru untuk update jalur hint
    public void setHintPath(List<Integer> path) {
        this.hintPath = path;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Tile[][] tiles = board.getTiles();
        int size = Board.SIZE;
        int tileSize = getWidth() / size;

        // 1. Gambar Papan
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = col * tileSize;
                int y = row * tileSize;

                if ((row + col) % 2 == 0) g2d.setColor(COLOR_TILE_1);
                else g2d.setColor(COLOR_TILE_2);
                g2d.fillRect(x, y, tileSize, tileSize);
                g2d.setColor(new Color(220, 220, 220));
                g2d.drawRect(x, y, tileSize, tileSize);
                
                Tile t = tiles[row][col];
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2d.drawString(String.valueOf(t.getNumber()), x + 8, y + 20);
            }
        }

        // 2. GAMBAR JALUR DIJKSTRA (HINT) - BARU!
        if (hintPath != null && !hintPath.isEmpty()) {
            g2d.setColor(HINT_COLOR);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{10}, 0));
            
            // Kita gambar garis dari start (player) ke point-point di path
            // (Untuk simplifikasi, kita gambar lingkaran di setiap kotak jalur)
            for (int tileNum : hintPath) {
                Point p = getCoordinatesFromNumber(tileNum, tileSize);
                g2d.drawRect(p.x + 5, p.y + 5, tileSize - 10, tileSize - 10);
            }
        }
        g2d.setStroke(new BasicStroke(1)); // Reset stroke

        // 3. GAMBAR TANGGA
        g2d.setStroke(new BasicStroke(4)); 
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile t = tiles[row][col];
                if (t.isLadder()) {
                    Point start = getCoordinatesFromNumber(t.getNumber(), tileSize);
                    Point end = getCoordinatesFromNumber(t.getDestination(), tileSize);
                    
                    int x1 = start.x + tileSize/2;
                    int y1 = start.y + tileSize/2;
                    int x2 = end.x + tileSize/2;
                    int y2 = end.y + tileSize/2;

                    g2d.setColor(LADDER_COLOR);
                    g2d.drawLine(x1, y1, x2, y2);
                    g2d.fillOval(x1-5, y1-5, 10, 10);
                    g2d.fillOval(x2-5, y2-5, 10, 10);
                }
            }
        }

        // 4. Gambar Pemain
        if (players != null) {
            for (Player p : players) {
                drawPlayer(g2d, p, tileSize);
            }
        }
    }

    private void drawPlayer(Graphics2D g2d, Player p, int tileSize) {
        Point coord = getCoordinatesFromNumber(p.getPosition(), tileSize);
        int offset = (p.getId() - 1) * 8; 
        int pSize = tileSize / 2;
        
        g2d.setColor(p.getColor());
        g2d.fillOval(coord.x + (tileSize/4) + offset, coord.y + (tileSize/4) + offset, pSize, pSize);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(coord.x + (tileSize/4) + offset, coord.y + (tileSize/4) + offset, pSize, pSize);
    }

    private Point getCoordinatesFromNumber(int number, int tileSize) {
        int size = Board.SIZE;
        int index = number - 1; 
        int rowFromBottom = index / size; 
        int col;
        if (rowFromBottom % 2 == 0) col = index % size; 
        else col = size - 1 - (index % size);
        int visualRow = size - 1 - rowFromBottom;
        return new Point(col * tileSize, visualRow * tileSize);
    }
}