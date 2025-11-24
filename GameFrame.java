import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameFrame extends JFrame {

    private BoardPanel boardPanel;
    private Board board;
    private JPanel sidePanel;
    private JPanel playerListPanel;
    
    private JButton rollButton;
    private JLabel diceResultLabel;
    private JLabel diceInfoLabel;
    private JLabel turnLabel;
    
    // Label baru untuk Hint Dijkstra
    private JLabel hintLabel; 

    private List<Player> players;
    private int currentPlayerIndex = 0; 
    private boolean isAnimating = false; 
    private boolean canClimbLadder = false;

    public GameFrame(int totalPlayers) {
        setTitle("Snake & Ladders - AI Assistant (Dijkstra)");
        setSize(1000, 750); // Tinggi ditambah sedikit
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        SoundManager.play("start.wav");

        initPlayersLogic(totalPlayers);

        board = new Board();
        boardPanel = new BoardPanel(board);
        add(boardPanel, BorderLayout.CENTER);

        sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.EAST);
        
        updateTurnUI(); // Ini akan memicu perhitungan Dijkstra awal
        boardPanel.setPlayers(players);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initPlayersLogic(int amount) {
        players = new ArrayList<>();
        Color[] colors = {
            new Color(231, 76, 60), new Color(52, 152, 219),
            new Color(46, 204, 113), new Color(241, 196, 15),
            new Color(155, 89, 182), new Color(230, 126, 34)
        };
        for (int i = 0; i < amount; i++) {
            players.add(new Player(i + 1, colors[i]));
        }
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("GAME CONTROL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        rollButton = new JButton("ROLL DICE");
        rollButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        rollButton.setBackground(new Color(52, 152, 219));
        rollButton.setForeground(Color.WHITE);
        rollButton.setFocusPainted(false);
        rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rollButton.setMaximumSize(new Dimension(240, 60));
        rollButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rollButton.addActionListener(e -> prepareRollDice());
        panel.add(rollButton);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        diceResultLabel = new JLabel("-");
        diceResultLabel.setFont(new Font("Segoe UI", Font.BOLD, 80));
        diceResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(diceResultLabel);

        diceInfoLabel = new JLabel("Klik Roll Dice");
        diceInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        diceInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(diceInfoLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // --- VISUAL HINT DIJKSTRA ---
        hintLabel = new JLabel("Best Path: Calculating...");
        hintLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hintLabel.setForeground(new Color(211, 84, 0)); // Warna Oranye Tua
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(hintLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        turnLabel = new JLabel("Giliran: Player 1");
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        turnLabel.setForeground(Color.DARK_GRAY);
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        panel.add(turnLabel);

        panel.add(Box.createVerticalGlue());

        JLabel legendTitle = new JLabel("Daftar Pemain:");
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        legendTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(legendTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        playerListPanel = new JPanel();
        playerListPanel.setLayout(new GridLayout(0, 1, 5, 5));
        playerListPanel.setBackground(new Color(248, 249, 250));
        playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        for (Player p : players) {
            JPanel pRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pRow.setBackground(new Color(240, 240, 240));
            pRow.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            pRow.setMaximumSize(new Dimension(240, 40));
            
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(20, 20));
            colorBox.setBackground(p.getColor());
            colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            JLabel nameLbl = new JLabel(" Player " + p.getId());
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            pRow.add(colorBox);
            pRow.add(nameLbl);
            playerListPanel.add(pRow);
        }
        panel.add(playerListPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        return panel;
    }

    private void updateTurnUI() {
        if (players.isEmpty()) return;
        Player p = players.get(currentPlayerIndex);
        turnLabel.setText("Giliran: Player " + p.getId());
        turnLabel.setForeground(p.getColor()); 
        
        // --- HITUNG DIJKSTRA SAAT GILIRAN BERUBAH ---
        calculateHint(p.getPosition());
    }
    
    // Method baru untuk hitung dan gambar hint
    private void calculateHint(int startPos) {
        if (startPos >= 64) {
            hintLabel.setText("Finish!");
            boardPanel.setHintPath(new ArrayList<>());
            return;
        }

        // Panggil Solver
        List<Integer> path = DijkstraSolver.getShortestPath(board, startPos);
        
        // Update Label
        // Path size = jumlah kotak yg harus diinjak. Estimasi roll minimal = size / rata-rata dadu?
        // Tapi secara harfiah, size dari list path Dijkstra di kasus ini adalah jumlah 'langkah/roll' minimal
        // karena setiap edge weight-nya 1.
        hintLabel.setText("Min. Roll ke Finish: " + path.size());
        
        // Gambar di Board
        boardPanel.setHintPath(path);
    }

    private void prepareRollDice() {
        if (isAnimating) return;
        isAnimating = true;
        rollButton.setEnabled(false);
        
        Player currentPlayer = players.get(currentPlayerIndex);
        int startPos = currentPlayer.getPosition();
        
        if (isPrime(startPos)) {
            canClimbLadder = true;
            System.out.println("Start Prima (" + startPos + "). Tangga AKTIF.");
        } else {
            canClimbLadder = false;
            System.out.println("Start Biasa (" + startPos + "). Tangga MATI.");
        }

        SoundManager.play("roll.wav");
        diceInfoLabel.setText("Rolling...");
        diceResultLabel.setForeground(Color.BLACK);

        Timer rollTimer = new Timer(100, new ActionListener() {
            int count = 0;
            Random r = new Random();
            @Override
            public void actionPerformed(ActionEvent e) {
                diceResultLabel.setText(String.valueOf(r.nextInt(6)+1));
                count++;
                if (count > 10) {
                    ((Timer)e.getSource()).stop();
                    processResult();
                }
            }
        });
        rollTimer.start();
    }

    private void processResult() {
        Random rand = new Random();
        boolean isForward = rand.nextDouble() < 0.7; 
        
        int steps;
        if (isForward) {
            steps = rand.nextInt(6) + 1;
            diceResultLabel.setForeground(new Color(39, 174, 96));
            diceResultLabel.setText(String.valueOf(steps));
            diceInfoLabel.setText("Maju " + steps + " langkah");
            diceInfoLabel.setForeground(new Color(39, 174, 96));
            animateMovement(steps, true);
        } else {
            steps = 1; 
            diceResultLabel.setForeground(new Color(192, 57, 43));
            diceResultLabel.setText(String.valueOf(steps));
            diceInfoLabel.setText("Mundur " + steps + " langkah");
            diceInfoLabel.setForeground(new Color(192, 57, 43));
            animateMovement(steps, false);
        }
    }

    private void animateMovement(int stepsToWalk, boolean forward) {
        Player currentPlayer = players.get(currentPlayerIndex);

        Timer timer = new Timer(300, null);
        timer.addActionListener(new ActionListener() {
            int stepsTaken = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stepsTaken >= stepsToWalk) {
                    timer.stop();
                    finishTurn(currentPlayer);
                    return;
                }

                SoundManager.play("step.wav"); 
                if (forward) {
                    if (currentPlayer.getPosition() >= 64) {
                        timer.stop(); finishTurn(currentPlayer); return;
                    }
                    currentPlayer.moveForward();
                } else {
                    if (currentPlayer.getPosition() <= 1) {
                        timer.stop(); finishTurn(currentPlayer); return;
                    }
                    currentPlayer.moveBackward();
                }
                
                boardPanel.repaint();
                stepsTaken++;
                
                int currentPos = currentPlayer.getPosition();
                Tile currentTile = board.getTileByNumber(currentPos);
                
                if (currentTile != null && currentTile.isLadder() && canClimbLadder) {
                    timer.stop();
                    int remainingSteps = stepsToWalk - stepsTaken;
                    diceInfoLabel.setText("Naik Tangga! Sisa: " + remainingSteps);
                    SoundManager.play("roll.wav"); 

                    Timer jumpTimer = new Timer(600, evt -> {
                        currentPlayer.setPosition(currentTile.getDestination());
                        boardPanel.repaint();
                        if (remainingSteps > 0) {
                             diceInfoLabel.setText("Melanjutkan " + remainingSteps + " langkah...");
                             animateMovement(remainingSteps, forward);
                        } else {
                             finishTurn(currentPlayer);
                        }
                    });
                    jumpTimer.setRepeats(false);
                    jumpTimer.start();
                } 
            }
        });
        timer.start();
    }
    
    private void finishTurn(Player currentPlayer) {
        if (currentPlayer.getPosition() == 64) {
            SoundManager.play("win.wav"); 
            int response = JOptionPane.showConfirmDialog(this, 
                "Player " + currentPlayer.getId() + " MENANG!\nMain Lagi?", 
                "Game Over", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                this.dispose(); 
                Main.main(null); 
            } else {
                System.exit(0);
            }
            return;
        }

        int finalPos = currentPlayer.getPosition();
        if (finalPos % 5 == 0) {
            diceInfoLabel.setText("Kelipatan 5! MAIN LAGI!");
            diceInfoLabel.setForeground(Color.MAGENTA);
            SoundManager.play("start.wav");
            isAnimating = false;
            rollButton.setEnabled(true);
            
            // Hitung ulang Dijkstra untuk posisi baru
            calculateHint(finalPos);
        } else {
            isAnimating = false;
            rollButton.setEnabled(true);
            nextTurn();
        }
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        updateTurnUI();
    }
    
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}