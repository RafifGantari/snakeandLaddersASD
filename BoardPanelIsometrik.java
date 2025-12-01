import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardPanelIsometrik extends JPanel {
    private Board board;
    private List<Player> players;

    // --- KONSTANTA VISUAL ISOMETRIK ---
    private final int TILE_WIDTH = 60;   
    private final int TILE_HEIGHT = 30;  
    private final int BLOCK_DEPTH = 25;  

    // --- PALET WARNA PRIMITIVES (Gaya Hutan) ---
    private final Color BG_COLOR = new Color(135, 206, 235); // Langit Biru
    private final Color COLOR_GRASS_1 = new Color(46, 204, 113); // Hijau Rumput 1
    private final Color COLOR_GRASS_2 = new Color(39, 174, 96);  // Hijau Rumput 2
    private final Color COLOR_DIRT_SIDE = new Color(121, 85, 72);    // Coklat Tanah
    private final Color COLOR_DIRT_FRONT = new Color(93, 64, 55);    // Coklat Gelap
    private final Color LADDER_COLOR = new Color(139, 69, 19);       // Coklat Kayu

    public BoardPanelIsometrik(Board board) {
        this.board = board;
        this.players = new ArrayList<>();
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(800, 750));
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        repaint();
    }

    // Method setHintPath SUDAH DIHAPUS

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int startX = getWidth() / 2;
        int startY = 150; 

        Tile[][] tiles = board.getTiles();
        int size = Board.SIZE;

        // 1. GAMBAR BLOK TANAH
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                
                Tile t = tiles[row][col];
                Point isoPt = toIso(col, row, startX, startY);
                
                // Logika Hint DIHAPUS. Hanya warna selang-seling rumput biasa.
                Color grassColor = ((row + col) % 2 == 0) ? COLOR_GRASS_1 : COLOR_GRASS_2;

                drawBlock(g2d, isoPt.x, isoPt.y, grassColor, t.getNumber());
            }
        }
        
        // 2. GAMBAR OBJEK (Pemain & Tangga)
        drawPlayers(g2d, startX, startY);
        drawLadders(g2d, startX, startY);
    }

    private void drawBlock(Graphics2D g2d, int x, int y, Color topColor, int number) {
        // POLIGON ATAS
        Polygon topFace = new Polygon();
        topFace.addPoint(x, y);                       
        topFace.addPoint(x + TILE_WIDTH, y + TILE_HEIGHT); 
        topFace.addPoint(x, y + TILE_HEIGHT * 2);     
        topFace.addPoint(x - TILE_WIDTH, y + TILE_HEIGHT); 
        
        // POLIGON DEPAN
        Polygon frontFace = new Polygon();
        frontFace.addPoint(x - TILE_WIDTH, y + TILE_HEIGHT);
        frontFace.addPoint(x, y + TILE_HEIGHT * 2);
        frontFace.addPoint(x, y + TILE_HEIGHT * 2 + BLOCK_DEPTH); 
        frontFace.addPoint(x - TILE_WIDTH, y + TILE_HEIGHT + BLOCK_DEPTH);
        
        // POLIGON SAMPING
        Polygon sideFace = new Polygon();
        sideFace.addPoint(x, y + TILE_HEIGHT * 2);
        sideFace.addPoint(x + TILE_WIDTH, y + TILE_HEIGHT);
        sideFace.addPoint(x + TILE_WIDTH, y + TILE_HEIGHT + BLOCK_DEPTH);
        sideFace.addPoint(x, y + TILE_HEIGHT * 2 + BLOCK_DEPTH);

        // WARNAI
        g2d.setColor(COLOR_DIRT_FRONT);
        g2d.fill(frontFace);
        g2d.setColor(COLOR_DIRT_SIDE);
        g2d.fill(sideFace);
        
        g2d.setColor(topColor);
        g2d.fill(topFace);
        
        // Garis pinggir tipis
        g2d.setColor(new Color(0,0,0,30));
        g2d.draw(topFace);
        
        // Nomor Petak
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.drawString(String.valueOf(number), x - 6, y + TILE_HEIGHT + 5);
    }

    private void drawPlayers(Graphics2D g2d, int startX, int startY) {
        if (players == null) return;
        int offset = 0;
        for (Player p : players) {
            Point center = getTileCenterIso(p.getPosition(), startX, startY);
            if (center == null) continue;

            int pWidth = 20;
            int pHeight = 30;
            int px = center.x - (pWidth / 2) + offset;
            int py = center.y - pHeight + 5; 

            g2d.setColor(p.getColor());
            g2d.fillOval(px, py, pWidth, pHeight);
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(px, py, pWidth, pHeight);
            
            offset += 8; 
        }
    }

    private void drawLadders(Graphics2D g2d, int startX, int startY) {
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(LADDER_COLOR);
        
        Tile[][] tiles = board.getTiles();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Tile t = tiles[row][col];
                if (t.isLadder()) {
                    Point pStart = getTileCenterIso(t.getNumber(), startX, startY);
                    Point pEnd = getTileCenterIso(t.getDestination(), startX, startY);
                    if (pStart == null || pEnd == null) continue;

                    g2d.drawLine(pStart.x - 5, pStart.y, pEnd.x - 5, pEnd.y);
                    g2d.drawLine(pStart.x + 5, pStart.y, pEnd.x + 5, pEnd.y);
                    
                    int steps = 6;
                    double dx = (pEnd.x - pStart.x) / (double)steps;
                    double dy = (pEnd.y - pStart.y) / (double)steps;
                    g2d.setStroke(new BasicStroke(2));
                    for(int i=1; i<steps; i++){
                        int rX = (int)(pStart.x + dx*i);
                        int rY = (int)(pStart.y + dy*i);
                        g2d.drawLine(rX-5, rY, rX+5, rY);
                    }
                    g2d.setStroke(new BasicStroke(3)); 
                }
            }
        }
    }

    private Point toIso(int col, int row, int startX, int startY) {
        int x = startX + (col - row) * TILE_WIDTH;
        int y = startY + (col + row) * TILE_HEIGHT;
        return new Point(x, y);
    }

    private Point getTileCenterIso(int number, int startX, int startY) {
        if (number < 1 || number > 64) return null;
        int index = number - 1;
        int size = Board.SIZE;
        int rowFromBottom = index / size;
        int gridRow = (size - 1) - rowFromBottom; 
        int gridCol = (rowFromBottom % 2 == 0) ? (index % size) : (size - 1 - (index % size));
        
        Point base = toIso(gridCol, gridRow, startX, startY);
        return new Point(base.x, base.y + TILE_HEIGHT); 
    }
}