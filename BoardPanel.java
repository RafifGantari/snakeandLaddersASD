import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardPanel extends JPanel {
    private Board board;
    private List<Player> players;
    private List<Point> snowFlakes = new ArrayList<>();

    private final int TILE_SIZE = 60;   
    private final int GAP = 20;         
    private final int ARC_SIZE = 25;    
    
    private final Color BG_COLOR      = new Color(225, 240, 250); 
    private final Color PATH_COLOR    = new Color(93, 173, 226);  
    private final Color PATH_SHADOW   = new Color(40, 100, 140, 50); 
    private final Color TILE_FILL     = Color.WHITE;              
    private final Color TILE_BORDER   = new Color(52, 152, 219);  
    private final Color TEXT_COLOR    = new Color(44, 62, 80);
    private final Color LADDER_COLOR  = new Color(160, 82, 45);   

    public BoardPanel(Board board) {
        this.board = board;
        this.players = new ArrayList<>();
        setBackground(BG_COLOR);
        
        Random r = new Random();
        for (int i = 0; i < 200; i++) {
            snowFlakes.add(new Point(r.nextInt(1200), r.nextInt(1000)));
        }
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        drawSnow(g2d);

        int boardTotalSize = (8 * TILE_SIZE) + (7 * GAP);
        int startX = (getWidth() - boardTotalSize) / 2;
        int startY = (getHeight() - boardTotalSize) / 2;

        if (startX < 20) startX = 20;
        if (startY < 20) startY = 20;

        List<Point> tileCenters = calculateAllTileCenters(startX, startY);

        drawWindingPath(g2d, tileCenters);
        drawTiles(g2d, tileCenters);
        drawLadders(g2d, tileCenters);
        drawPlayers(g2d, tileCenters);
        drawLabels(g2d, tileCenters);
    }

    private List<Point> calculateAllTileCenters(int marginX, int marginY) {
        List<Point> points = new ArrayList<>();
        int step = TILE_SIZE + GAP;

        for (int i = 0; i < 64; i++) {
            int row = i / 8; 
            int col = i % 8;
            int effectiveCol = (row % 2 == 0) ? col : (7 - col);
            int y = marginY + ((7 - row) * step);
            int x = marginX + (effectiveCol * step);
            points.add(new Point(x + TILE_SIZE/2, y + TILE_SIZE/2));
        }
        return points;
    }

    private void drawSnow(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        for (Point p : snowFlakes) {
            if (p.x < getWidth() && p.y < getHeight()) {
                g2d.fillOval(p.x, p.y, 4, 4);
            }
        }
    }

    private void drawWindingPath(Graphics2D g2d, List<Point> points) {
        if (points.isEmpty()) return;
        Path2D path = new Path2D.Float();
        path.moveTo(points.get(0).x, points.get(0).y);

        for (int i = 1; i < points.size(); i++) {
            Point p = points.get(i);
            path.lineTo(p.x, p.y);
        }

        g2d.setColor(PATH_SHADOW);
        g2d.setStroke(new BasicStroke(TILE_SIZE + 8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.translate(3, 3); 
        g2d.draw(path);
        g2d.translate(-3, -3); 

        g2d.setColor(PATH_COLOR);
        g2d.setStroke(new BasicStroke(TILE_SIZE + 8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(path);
        
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.setStroke(new BasicStroke(TILE_SIZE - 20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(path);
    }

    private void drawTiles(Graphics2D g2d, List<Point> centers) {
        for (int i = 0; i < 64; i++) {
            Point c = centers.get(i);
            int x = c.x - TILE_SIZE / 2;
            int y = c.y - TILE_SIZE / 2;
            int num = i + 1;

            g2d.setColor(TILE_FILL);
            g2d.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, ARC_SIZE, ARC_SIZE);

            g2d.setColor(TILE_BORDER);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(x, y, TILE_SIZE, TILE_SIZE, ARC_SIZE, ARC_SIZE);

            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            String s = String.valueOf(num);
            int txtX = x + (TILE_SIZE - fm.stringWidth(s)) / 2;
            int txtY = y + (TILE_SIZE + fm.getAscent()) / 2 - 4;
            g2d.drawString(s, txtX, txtY);
        }
    }

    private void drawLadders(Graphics2D g2d, List<Point> centers) {
        g2d.setColor(LADDER_COLOR);
        Stroke railStroke = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Stroke rungStroke = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        Tile[][] tiles = board.getTiles();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Tile t = tiles[r][c];
                if (t.isLadder()) {
                    Point p1 = centers.get(t.getNumber() - 1);
                    Point p2 = centers.get(t.getDestination() - 1);

                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double angle = Math.atan2(dy, dx);
                    double dist = Math.sqrt(dx*dx + dy*dy);
                    
                    int halfWidth = 10;
                    double offX = halfWidth * -Math.sin(angle);
                    double offY = halfWidth * Math.cos(angle);

                    g2d.setStroke(railStroke);
                    g2d.drawLine((int)(p1.x - offX), (int)(p1.y - offY), (int)(p2.x - offX), (int)(p2.y - offY));
                    g2d.drawLine((int)(p1.x + offX), (int)(p1.y + offY), (int)(p2.x + offX), (int)(p2.y + offY));

                    g2d.setStroke(rungStroke);
                    int steps = (int)(dist / 15);
                    for (int i = 1; i < steps; i++) {
                        double frac = i / (double)steps;
                        double mx = p1.x + frac * dx;
                        double my = p1.y + frac * dy;
                        g2d.drawLine((int)(mx - offX), (int)(my - offY), (int)(mx + offX), (int)(my + offY));
                    }
                }
            }
        }
    }

    private void drawPlayers(Graphics2D g2d, List<Point> centers) {
        if (players == null) return;
        int[] collisionCount = new int[65];

        for (Player p : players) {
            int pos = p.getPosition();
            if (pos < 1) pos = 1;
            if (pos > 64) pos = 64;

            Point c = centers.get(pos - 1);
            int shift = collisionCount[pos] * 12;
            collisionCount[pos]++;

            int size = 26;
            int px = c.x - (size/2) + shift;
            int py = c.y - (size/2) - 5; 

            g2d.setColor(new Color(0,0,0,50));
            g2d.fillOval(px+2, py+size-5, size-4, 8);

            g2d.setColor(p.getColor());
            g2d.fillOval(px, py, size, size);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(px, py, size, size);
        }
    }

    private void drawLabels(Graphics2D g2d, List<Point> centers) {
        g2d.setColor(new Color(231, 76, 60)); 
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        Point start = centers.get(0);
        Point finish = centers.get(63);
        
        drawCenteredText(g2d, "START", start.x, start.y + TILE_SIZE/2 + 15);
        drawCenteredText(g2d, "FINISH", finish.x, finish.y - TILE_SIZE/2 - 5);
    }
    
    private void drawCenteredText(Graphics2D g2d, String text, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(text);
        g2d.drawString(text, x - w/2, y);
    }
}