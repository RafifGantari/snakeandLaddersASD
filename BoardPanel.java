import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private Board board;

    public BoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Tile[][] tiles = board.getTiles();
        int size = Board.SIZE;
        int tileSize = getWidth() / size;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = col * tileSize;
                int y = row * tileSize;

                // background warna kotak
                if ((row + col) % 2 == 0) g.setColor(Color.WHITE);
                else g.setColor(new Color(220, 220, 220));
                g.fillRect(x, y, tileSize, tileSize);

                // border kotak
                g.setColor(Color.BLACK);
                g.drawRect(x, y, tileSize, tileSize);

                // nomor tile
                g.drawString(String.valueOf(tiles[row][col].getNumber()),
                        x + tileSize / 2 - 5,
                        y + tileSize / 2);
            }
        }
    }
}
