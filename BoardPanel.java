import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardPanel extends JPanel {
    private Board board;
    private List<Player> players;
    
    // Objek Dekorasi
    private List<Point> snowFlakes = new ArrayList<>();
    private List<Decoration> decorations = new ArrayList<>();
    
    private Color[] lightColors = { Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.ORANGE };
    private Color[] giftColors = { Color.RED, Color.BLUE, Color.GREEN, new Color(128,0,128) };

    private final int TILE_SIZE = 60;   
    private final int GAP = 20;         
    private final int ARC_SIZE = 25;    
    
    // Warna Latar & Board
    private final Color BG_COLOR_TOP      = new Color(10, 10, 60); 
    private final Color BG_COLOR_BOTTOM   = new Color(40, 60, 100);
    private final Color PATH_COLOR    = new Color(255, 255, 255);
    private final Color PATH_SHADOW   = new Color(200, 220, 240, 100); 
    private final Color TILE_FILL     = Color.WHITE;              
    private final Color TEXT_COLOR    = new Color(44, 62, 80);
    private final Color LADDER_COLOR  = new Color(139, 69, 19);

    private class Decoration {
        int x, y, type, scale; 
        Color color, ribbonColor;
        public Decoration(int x, int y, int type) {
            this.x = x; this.y = y; this.type = type;
            Random r = new Random();
            this.scale = r.nextInt(15) + 20;
            if(type == 1) { // Gift
                this.color = giftColors[r.nextInt(giftColors.length)];
                this.ribbonColor = (r.nextBoolean()) ? StyleTheme.GOLD : Color.WHITE;
            }
        }
    }

    public BoardPanel(Board board) {
        this.board = board;
        this.players = new ArrayList<>();
        
        Random r = new Random();
        // Generate Salju
        for (int i = 0; i < 400; i++) snowFlakes.add(new Point(r.nextInt(1200), r.nextInt(1000)));
        // Generate Dekorasi
        for (int i = 0; i < 30; i++) {
            int x = r.nextInt(1100);
            int y = r.nextInt(850);
            if (x > 150 && x < 950 && y > 100 && y < 800) continue; // Jangan di tengah board
            int type = r.nextInt(2); 
            decorations.add(new Decoration(x, y, type));
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

        // 1. Background Gradient
        GradientPaint bgGradient = new GradientPaint(0, 0, BG_COLOR_TOP, 0, getHeight(), BG_COLOR_BOTTOM);
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 2. Dekorasi & Salju
        drawBackgroundDecorations(g2d);
        drawSnow(g2d);

        // 3. Frame Board
        int boardW = (8 * TILE_SIZE) + (7 * GAP);
        int startX = (getWidth() - boardW) / 2;
        int startY = (getHeight() - boardW) / 2;
        if(startX < 20) startX = 20;
        if(startY < 60) startY = 60;

        drawBoardFrame(g2d, startX - 20, startY - 20, boardW + 40, boardW + 40);

        List<Point> centers = calculateAllTileCenters(startX, startY);

        drawWindingPath(g2d, centers);
        drawTiles(g2d, centers);
        drawLadders(g2d, centers);
        drawPlayers(g2d, centers);
        drawLabels(g2d, centers);
        drawHangingLights(g2d);
    }

    private void drawBoardFrame(Graphics2D g2d, int x, int y, int w, int h) {
        g2d.setColor(new Color(255,255,255, 50));
        g2d.fillRoundRect(x, y, w, h, 40, 40);
        // Garis Tepi Candy Cane
        g2d.setClip(new RoundRectangle2D.Float(x, y, w, h, 40, 40));
        g2d.setColor(StyleTheme.SANTA_RED);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawRoundRect(x, y, w, h, 40, 40);
        g2d.setClip(null);
    }

    private void drawBackgroundDecorations(Graphics2D g2d) {
        for (Decoration d : decorations) {
            if (d.type == 0) drawChristmasTree(g2d, d.x, d.y, d.scale);
            else drawGiftBox(g2d, d.x, d.y, d.scale, d.color, d.ribbonColor);
        }
    }

    private void drawChristmasTree(Graphics2D g2d, int x, int y, int s) {
        g2d.setColor(new Color(80, 50, 20)); g2d.fillRect(x + s/3, y + s, s/3, s/3); // Batang
        g2d.setColor(StyleTheme.PINE_GREEN);
        int[] xP = {x, x + s/2, x + s};
        int[] yP1 = {y + s, y + s/3, y + s};
        int[] yP2 = {y + 2*s/3, y, y + 2*s/3};
        g2d.fillPolygon(xP, yP1, 3); g2d.fillPolygon(xP, yP2, 3);
        g2d.setColor(StyleTheme.GOLD); g2d.fillOval(x+s/2-2, y-2, 4, 4); // Bintang kecil
    }

    private void drawGiftBox(Graphics2D g2d, int x, int y, int s, Color c, Color rc) {
        g2d.setColor(c); g2d.fillRect(x, y, s, s);
        g2d.setColor(rc); g2d.fillRect(x + s/2 - 2, y, 4, s); g2d.fillRect(x, y + s/2 - 2, s, 4);
    }

    private void drawHangingLights(Graphics2D g2d) {
        g2d.setColor(new Color(30,30,30)); g2d.setStroke(new BasicStroke(2));
        Path2D wire = new Path2D.Float();
        wire.moveTo(0, 0); wire.quadTo(getWidth()/2, 80, getWidth(), 0);
        g2d.draw(wire);

        for (int i = 30; i < getWidth(); i += 50) {
            double t = (double)i/getWidth();
            int y = (int)(80 * (1 - Math.pow(2*t-1, 2))) / 2; // Parabola approx
            Color c = lightColors[(i/50)%lightColors.length];
            
            // Glow
            g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
            g2d.fillOval(i-6, y+2, 20, 20);
            
            g2d.setColor(c); g2d.fillOval(i, y+5, 8, 12);
        }
    }

    private List<Point> calculateAllTileCenters(int marginX, int marginY) {
        List<Point> points = new ArrayList<>();
        int step = TILE_SIZE + GAP;
        for (int i = 0; i < 64; i++) {
            int row = i / 8; int col = i % 8;
            int effectiveCol = (row % 2 == 0) ? col : (7 - col);
            int y = marginY + ((7 - row) * step);
            int x = marginX + (effectiveCol * step);
            points.add(new Point(x + TILE_SIZE/2, y + TILE_SIZE/2));
        }
        return points;
    }

    private void drawSnow(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 150));
        for (Point p : snowFlakes) {
            p.y += (p.x % 3 + 1); if(p.y > getHeight()) p.y = -5;
            g2d.fillOval(p.x, p.y, 3, 3);
        }
    }

    private void drawWindingPath(Graphics2D g2d, List<Point> points) {
        if (points.isEmpty()) return;
        Path2D path = new Path2D.Float();
        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) path.lineTo(points.get(i).x, points.get(i).y);

        g2d.setColor(PATH_SHADOW);
        g2d.setStroke(new BasicStroke(TILE_SIZE + 12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.translate(4, 4); g2d.draw(path); g2d.translate(-4, -4); 

        g2d.setColor(PATH_COLOR);
        g2d.setStroke(new BasicStroke(TILE_SIZE + 8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(path);
    }

    private void drawTiles(Graphics2D g2d, List<Point> centers) {
        for (int i = 0; i < 64; i++) {
            Point c = centers.get(i);
            int x = c.x - TILE_SIZE / 2; int y = c.y - TILE_SIZE / 2; int num = i + 1;
            Tile t = board.getTileByNumber(num);
            int pts = (t != null) ? t.getPoints() : 0;

            g2d.setColor(TILE_FILL);
            g2d.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, ARC_SIZE, ARC_SIZE);

            if (num % 2 == 0) g2d.setColor(StyleTheme.SANTA_RED); else g2d.setColor(StyleTheme.PINE_GREEN);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(x, y, TILE_SIZE, TILE_SIZE, ARC_SIZE, ARC_SIZE);

            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            String s = String.valueOf(num);
            g2d.drawString(s, x + (TILE_SIZE - fm.stringWidth(s)) / 2, y + (TILE_SIZE + fm.getAscent()) / 2 - 4);
            
            if (pts > 0) {
                g2d.setColor(StyleTheme.GOLD);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2d.drawString("+" + pts, x + TILE_SIZE - 22, y + 16);
            }
        }
    }

    private void drawLadders(Graphics2D g2d, List<Point> centers) {
        g2d.setColor(LADDER_COLOR);
        Stroke railStroke = new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Stroke rungStroke = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Tile[][] tiles = board.getTiles();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Tile t = tiles[r][c];
                if (t.isLadder()) {
                    Point p1 = centers.get(t.getNumber() - 1);
                    Point p2 = centers.get(t.getDestination() - 1);
                    double dx = p2.x - p1.x; double dy = p2.y - p1.y;
                    double angle = Math.atan2(dy, dx);
                    double dist = Math.sqrt(dx*dx + dy*dy);
                    int halfWidth = 12;
                    double offX = halfWidth * -Math.sin(angle); double offY = halfWidth * Math.cos(angle);
                    g2d.setStroke(railStroke);
                    g2d.drawLine((int)(p1.x - offX), (int)(p1.y - offY), (int)(p2.x - offX), (int)(p2.y - offY));
                    g2d.drawLine((int)(p1.x + offX), (int)(p1.y + offY), (int)(p2.x + offX), (int)(p2.y + offY));
                    g2d.setStroke(rungStroke);
                    int steps = (int)(dist / 18);
                    for (int i = 1; i < steps; i++) {
                        double frac = i / (double)steps;
                        double mx = p1.x + frac * dx; double my = p1.y + frac * dy;
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
            int pos = p.getPosition(); if (pos < 1) pos = 1; if (pos > 64) pos = 64;
            Point c = centers.get(pos - 1);
            int shift = collisionCount[pos] * 12; collisionCount[pos]++;
            int size = 26; int px = c.x - (size/2) + shift; int py = c.y - (size/2) - 5; 
            g2d.setColor(new Color(0,0,0,50)); g2d.fillOval(px+2, py+size-5, size-4, 8);
            g2d.setColor(p.getColor()); g2d.fillOval(px, py, size, size);
            g2d.setColor(Color.WHITE); g2d.setStroke(new BasicStroke(2f)); g2d.drawOval(px, py, size, size);
        }
    }

    private void drawLabels(Graphics2D g2d, List<Point> centers) {
        g2d.setColor(StyleTheme.GOLD);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        Point start = centers.get(0); Point finish = centers.get(63);
        drawCenteredText(g2d, "START", start.x, start.y + TILE_SIZE/2 + 15);
        drawCenteredText(g2d, "FINISH", finish.x, finish.y - TILE_SIZE/2 - 5);
    }
    
    private void drawCenteredText(Graphics2D g2d, String text, int x, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(text);
        g2d.drawString(text, x - w/2, y);
    }
}