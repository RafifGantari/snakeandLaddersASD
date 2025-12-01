import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

    private List<Player> players;
    private int currentPlayerIndex = 0;
    private boolean isAnimating = false;

    // STATUS IZIN NAIK TANGGA (Aturan Prima)
    private boolean canClimbCurrentTurn = false;

    public GameFrame(List<String> playerNames) {
        setTitle("Snake & Ladders - Prime Rules Edition");
        setSize(1100, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Audio (Aman jika file tidak ada)
        SoundManager.playBackground("./assets/bgm.wav");
        SoundManager.play("./assets/start.wav");

        initPlayersLogic(playerNames);

        board = new Board();
        boardPanel = new BoardPanel(board);

        // --- NEW: use VideoBackgroundPanel with animated GIF ---
        VideoBackgroundPanel bgPanel = new VideoBackgroundPanel("./assets/background.gif");
        // ensure board panel is transparent so background shows through where appropriate
        boardPanel.setOpaque(false);
        bgPanel.addForeground(boardPanel);

        add(bgPanel, BorderLayout.CENTER);

        sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.EAST);

        updateTurnUI();
        boardPanel.setPlayers(players);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initPlayersLogic(List<String> names) {
        players = new ArrayList<>();
        Color[] colors = {
            new Color(231, 76, 60),
            new Color(41, 128, 185),
            new Color(39, 174, 96),
            new Color(243, 156, 18),
            new Color(142, 68, 173),
            new Color(211, 84, 0)
        };

        for (int i = 0; i < names.size(); i++) {
            players.add(new Player(i + 1, names.get(i), colors[i % colors.length]));
        }
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("CONTROLS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        rollButton = StyleTheme.createButton("ROLL DICE");
        rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rollButton.addActionListener(e -> prepareRollDice());
        panel.add(rollButton);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        diceResultLabel = new JLabel("-");
        diceResultLabel.setFont(new Font("Segoe UI", Font.BOLD, 90));
        diceResultLabel.setForeground(new Color(44, 62, 80));
        diceResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(diceResultLabel);

        diceInfoLabel = new JLabel("Giliranmu!");
        diceInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        diceInfoLabel.setForeground(Color.GRAY);
        diceInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(diceInfoLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 40)));

        turnLabel = new JLabel("Player 1");
        turnLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        turnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(turnLabel);

        panel.add(Box.createVerticalGlue());

        JLabel legendTitle = new JLabel("Leaderboard:");
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        legendTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(legendTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        playerListPanel = new JPanel();
        playerListPanel.setLayout(new GridLayout(0, 1, 8, 8));
        playerListPanel.setBackground(new Color(248, 250, 252));
        playerListPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        refreshPlayerList();
        panel.add(playerListPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        return panel;
    }

    private void updateTurnUI() {
        if (players.isEmpty()) return;
        Player p = players.get(currentPlayerIndex);
        turnLabel.setText(p.getName());
        turnLabel.setForeground(p.getColor());
        refreshPlayerList();
    }

    private void refreshPlayerList() {
        if (playerListPanel == null) return;
        playerListPanel.removeAll();

        List<Player> sortedList = new ArrayList<>(players);
        sortedList.sort((p1, p2) -> p2.getPosition() - p1.getPosition());

        for (Player p : sortedList) {
            JPanel pRow = new JPanel(new BorderLayout());
            pRow.setBackground(Color.WHITE);
            pRow.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(8, 10, 8, 10)
            ));
            pRow.setMaximumSize(new Dimension(280, 50));

            JLabel nameLbl = new JLabel(p.getName());
            nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel posLbl = new JLabel("Pos: " + p.getPosition());
            posLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            posLbl.setForeground(p.getColor());

            if (p == players.get(currentPlayerIndex)) {
                pRow.setBorder(BorderFactory.createLineBorder(p.getColor(), 2));
                pRow.setBackground(new Color(240, 248, 255));
                nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            }

            pRow.add(nameLbl, BorderLayout.WEST);
            pRow.add(posLbl, BorderLayout.EAST);
            playerListPanel.add(pRow);
        }
        playerListPanel.revalidate();
        playerListPanel.repaint();
    }

    private void prepareRollDice() {
        if (isAnimating) return;
        isAnimating = true;
        rollButton.setEnabled(false);

        Player currentPlayer = players.get(currentPlayerIndex);
        int startPos = currentPlayer.getPosition();

        // --- CEK ATURAN PRIMA ---
        if (isPrime(startPos)) {
            canClimbCurrentTurn = true;
            System.out.println("Start " + startPos + " (PRIMA) -> Tangga ON");
        } else {
            canClimbCurrentTurn = false;
            System.out.println("Start " + startPos + " (BUKAN) -> Tangga OFF");
        }

        SoundManager.play("./assets/roll.wav");
        diceInfoLabel.setText("Rolling...");

        final int[] count = {0};
        Timer rollTimer = new Timer(80, null);
        rollTimer.addActionListener(e -> {
            Random r = new Random();
            diceResultLabel.setText(String.valueOf(r.nextInt(6) + 1));
            count[0]++;
            if (count[0] > 12) {
                rollTimer.stop();
                processResult();
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

            // --- FIX SINKRONISASI DADU ---
            diceResultLabel.setText(String.valueOf(steps));

            diceResultLabel.setForeground(new Color(39, 174, 96));
            diceInfoLabel.setText("Maju " + steps + " langkah");
            diceInfoLabel.setForeground(new Color(39, 174, 96));
            animateMovement(steps, true);
        } else {
            steps = 1;

            // --- FIX SINKRONISASI DADU ---
            diceResultLabel.setText("1");

            diceResultLabel.setForeground(new Color(192, 57, 43));
            diceInfoLabel.setText("Mundur 1 langkah");
            diceInfoLabel.setForeground(new Color(192, 57, 43));
            animateMovement(steps, false);
        }
    }

    private int remainingStepsAfterLadder = 0;

    private void animateMovement(int stepsToWalk, boolean forward) {
        Player currentPlayer = players.get(currentPlayerIndex);
        final int[] stepsTaken = {0};

        Timer moveTimer = new Timer(300, null);
        moveTimer.addActionListener(e -> {

            // Jika selesai melangkah
            if (stepsTaken[0] >= stepsToWalk) {
                ((Timer)e.getSource()).stop();

                // Jika ada sisa langkah setelah naik tangga
                if (remainingStepsAfterLadder > 0) {
                    int extra = remainingStepsAfterLadder;
                    remainingStepsAfterLadder = 0;
                    animateMovement(extra, true); // lanjut jalan ke depan
                    return;
                }

                finishTurn(currentPlayer);
                return;
            }

            // Gerak 1 langkah
            SoundManager.play("assets/step.wav");

            if (forward) currentPlayer.moveForward();
            else currentPlayer.moveBackward();

            int newPos = currentPlayer.getPosition();
            stepsTaken[0]++;

            boardPanel.repaint();
            refreshPlayerList();

            // --- CEK TANGGA / papan khusus ---
            Tile tile = board.getTileByNumber(newPos);

            if (tile != null && tile.isLadder()) {

                if (canClimbCurrentTurn) {
                    // STOP timer sementara
                    ((Timer)e.getSource()).stop();

                    diceInfoLabel.setText("Naik tangga!");
                    diceInfoLabel.setForeground(new Color(39, 174, 96));
                    SoundManager.play("./assets/roll.wav");

                    int sisa = stepsToWalk - stepsTaken[0];
                    remainingStepsAfterLadder = sisa; // simpan langkah tersisa

                    // Timer lompat
                    Timer jumpTimer = new Timer(600, ev -> {
                        currentPlayer.setPosition(tile.getDestination());
                        boardPanel.repaint();

                        // lanjutkan langkah tersisa
                        if (remainingStepsAfterLadder > 0) {
                            int extraSteps = remainingStepsAfterLadder;
                            remainingStepsAfterLadder = 0;

                            animateMovement(extraSteps, true);
                            return;
                        }

                        finishTurn(currentPlayer);
                    });
                    jumpTimer.setRepeats(false);
                    jumpTimer.start();

                } else {
                    diceInfoLabel.setText("Tidak bisa naik (bukan start prime)");
                    diceInfoLabel.setForeground(new Color(231, 76, 60));
                }
            }
        });

        moveTimer.start();
    }


    private void finishTurn(Player currentPlayer) {
        // CEK MENANG
        if (currentPlayer.getPosition() == 64) {
            SoundManager.play("./assets/win.wav");
            SwingUtilities.invokeLater(() -> showPremiumWinDialog(currentPlayer));
            return;
        }

        // CEK BONUS (misal: jika pos multiple of 5 -> bonus giliran)
        int finalPos = currentPlayer.getPosition();
        if (finalPos % 5 == 0) {
            diceInfoLabel.setText("Bonus Giliran!");
            diceInfoLabel.setForeground(new Color(155, 89, 182));
            SoundManager.play("./assets/start.wav");
            isAnimating = false;
            rollButton.setEnabled(true);
            refreshPlayerList();
            // pemain tetap mendapat giliran, tidak pindah turn
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

    private void showPremiumWinDialog(Player winner) {
        JDialog dialog = new JDialog(this, "Victory", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(500, 650);

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(255, 248, 225));
                g2.setColor(new Color(255, 248, 225)); // fallback
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(new Color(255, 215, 0));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 40, 40);
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel iconLbl = new JLabel("🏆");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(iconLbl);

        JLabel winText = new JLabel("VICTORY!");
        winText.setFont(new Font("Segoe UI", Font.BOLD, 42));
        winText.setForeground(new Color(255, 140, 0));
        winText.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(winText);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel subText = new JLabel("Congratulations, " + winner.getName() + "!");
        subText.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subText.setForeground(Color.GRAY);
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subText);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel rankCard = new JPanel();
        rankCard.setLayout(new BoxLayout(rankCard, BoxLayout.Y_AXIS));
        rankCard.setBackground(Color.WHITE);
        rankCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        List<Player> finalRank = new ArrayList<>(players);
        finalRank.sort((p1, p2) -> p2.getPosition() - p1.getPosition());

        for (int i = 0; i < finalRank.size(); i++) {
            Player p = finalRank.get(i);
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(400, 35));
            row.setBorder(new EmptyBorder(5, 0, 5, 0));

            JLabel rankL = new JLabel("#" + (i + 1));
            rankL.setFont(new Font("Segoe UI", Font.BOLD, 16));
            rankL.setPreferredSize(new Dimension(40, 0));

            JLabel nameL = new JLabel(p.getName());
            nameL.setFont(new Font("Segoe UI", Font.PLAIN, 16));

            JLabel scoreL = new JLabel(p.getPosition() + " pts");
            scoreL.setFont(new Font("Segoe UI", Font.BOLD, 16));

            if (i == 0) {
                rankL.setForeground(new Color(218, 165, 32));
                nameL.setForeground(new Color(218, 165, 32));
                scoreL.setForeground(new Color(218, 165, 32));
            } else {
                rankL.setForeground(Color.LIGHT_GRAY);
                scoreL.setForeground(Color.GRAY);
            }

            row.add(rankL, BorderLayout.WEST);
            row.add(nameL, BorderLayout.CENTER);
            row.add(scoreL, BorderLayout.EAST);
            rankCard.add(row);

            if (i < finalRank.size() - 1) {
                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(240, 240, 240));
                rankCard.add(sep);
            }
        }
        contentPanel.add(rankCard);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setOpaque(false);

        JButton playBtn = StyleTheme.createButton("MAIN LAGI");
        playBtn.addActionListener(e -> {
            dialog.dispose();
            this.dispose();
            Main.main(null);
        });

        JButton exitBtn = new JButton("KELUAR") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(231, 76, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent()) / 2 - 4);
            }
        };
        exitBtn.setPreferredSize(new Dimension(0, 45));
        exitBtn.setFocusPainted(false);
        exitBtn.setBorderPainted(false);
        exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        exitBtn.addActionListener(e -> System.exit(0));

        btnPanel.add(playBtn);
        btnPanel.add(exitBtn);
        contentPanel.add(btnPanel);

        dialog.add(contentPanel);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
