import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Snake & Ladders");
        setSize(650, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Board board = new Board();
        BoardPanel boardPanel = new BoardPanel(board);

        add(boardPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
