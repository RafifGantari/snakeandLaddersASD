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
    private JPanel turnIndicatorPanel; 

    private List<Player> players;
    private int currentPlayerIndex = 0;
    private boolean isAnimating = false;
    private boolean canClimbCurrentTurn = false;

    public GameFrame(List<String> playerNames) {
        setTitle("Snake & Ladders - Christmas Edition");
        setSize(1250, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Audio
        SoundManager.playBackground("assets/bgm.wav");
        SoundManager.play("assets/start.wav");

        initPlayersLogic(playerNames);

        board = new Board();
        boardPanel = new BoardPanel(board);
        add(boardPanel, BorderLayout.CENTER);

        sidePanel = createModernSidePanel();
        add(sidePanel, BorderLayout.EAST);

        updateTurnUI();
        boardPanel.setPlayers(players);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initPlayersLogic(List<String> names) {
        players = new ArrayList<>();
        Color[] colors = {
            new Color(255, 69, 0), new Color(30, 144, 255),
            new Color(50, 205, 50), new Color(255, 215, 0),
            new Color(138, 43, 226), new Color(255, 140, 0)
        };
        for (int i = 0; i < names.size(); i++) {
            players.add(new Player(i + 1, names.get(i), colors[i % colors.length]));
        }
    }

    private JPanel createModernSidePanel() {
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(StyleTheme.SIDEBAR_BG); 
        mainContainer.setOpaque(true); 
        mainContainer.setPreferredSize(new Dimension(340, 0));
        
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 2, 0, 0, StyleTheme.GOLD), 
            new EmptyBorder(30, 25, 30, 25) 
        ));

        // HEADER
        JLabel title = new JLabel("MERRY CHRISTMAS");
        title.setFont(StyleTheme.fontBold(22));
        title.setForeground(StyleTheme.SANTA_RED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContainer.add(title);
        
        mainContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // STATUS
        JPanel statusCard = StyleTheme.createRoundedPanel(20, StyleTheme.CARD_BG);
        statusCard.setLayout(new BorderLayout());
        statusCard.setMaximumSize(new Dimension(280, 75));
        statusCard.setBorder(new EmptyBorder(10, 15, 10, 15));
        statusCard.setBackground(StyleTheme.SIDEBAR_BG);

        turnIndicatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!players.isEmpty()) {
                    g2.setColor(players.get(currentPlayerIndex).getColor());
                    g2.fillOval(0, 0, 20, 20);
                }
            }
        };
        turnIndicatorPanel.setPreferredSize(new Dimension(25, 25));
        turnIndicatorPanel.setOpaque(false);

        turnLabel = new JLabel("Player 1");
        turnLabel.setFont(StyleTheme.fontBold(18));
        turnLabel.setForeground(StyleTheme.DARK_TEXT);

        JLabel statusSub = new JLabel("Giliran Kamu");
        statusSub.setFont(StyleTheme.font(14));
        statusSub.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(turnLabel);
        textPanel.add(statusSub);

        statusCard.add(textPanel, BorderLayout.CENTER);
        statusCard.add(turnIndicatorPanel, BorderLayout.EAST);
        
        mainContainer.add(statusCard);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 40)));

        // DADU
        diceResultLabel = new JLabel("?");
        diceResultLabel.setFont(new Font("Segoe UI", Font.BOLD, 90)); 
        diceResultLabel.setForeground(StyleTheme.PRIMARY);
        diceResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        diceInfoLabel = new JLabel("Klik Roll untuk main");
        diceInfoLabel.setFont(StyleTheme.font(14));
        diceInfoLabel.setForeground(Color.GRAY);
        diceInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainContainer.add(diceResultLabel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        mainContainer.add(diceInfoLabel);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 30)));

        // TOMBOL ROLL BESAR (MERAH)
        rollButton = StyleTheme.createModernButton("PUTAR DADU 🎲", StyleTheme.SANTA_RED);
        rollButton.setForeground(Color.WHITE);
        rollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rollButton.setPreferredSize(new Dimension(260, 65)); 
        rollButton.setMaximumSize(new Dimension(260, 65)); 
        
        rollButton.addActionListener(e -> prepareRollDice());
        
        mainContainer.add(rollButton);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 40)));

        // LEADERBOARD SEMENTARA (SESSION)
        JLabel lbTitle = new JLabel("Session Leaderboard");
        lbTitle.setFont(StyleTheme.fontBold(16));
        lbTitle.setForeground(StyleTheme.DARK_TEXT);
        lbTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainContainer.add(lbTitle);
        mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        playerListPanel = new JPanel();
        playerListPanel.setLayout(new BoxLayout(playerListPanel, BoxLayout.Y_AXIS));
        playerListPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(playerListPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        
        mainContainer.add(scrollPane);

        return mainContainer;
    }

    private void updateTurnUI() {
        if (players.isEmpty()) return;
        Player p = players.get(currentPlayerIndex);
        turnLabel.setText(p.getName());
        if(turnIndicatorPanel != null) turnIndicatorPanel.repaint();
        refreshPlayerList();
    }

    private void refreshPlayerList() {
        if (playerListPanel == null) return;
        playerListPanel.removeAll();

        List<Player> sortedList = new ArrayList<>(players);
        sortedList.sort((p1, p2) -> p2.getPosition() - p1.getPosition());

        for (Player p : sortedList) {
            JPanel row = StyleTheme.createRoundedPanel(15, StyleTheme.CARD_BG);
            row.setLayout(new BorderLayout());
            row.setMaximumSize(new Dimension(300, 45));
            row.setBorder(new EmptyBorder(5, 10, 5, 10));
            row.setBackground(StyleTheme.SIDEBAR_BG);

            JLabel nameLbl = new JLabel(p.getName());
            nameLbl.setFont(StyleTheme.font(13));
            nameLbl.setForeground(StyleTheme.DARK_TEXT);
            if(p == players.get(currentPlayerIndex)) nameLbl.setFont(StyleTheme.fontBold(13));

            JLabel statLbl = new JLabel("Pos: " + p.getPosition() + " | Pts: " + p.getScore());
            statLbl.setFont(StyleTheme.fontBold(13));
            statLbl.setForeground(p.getColor());

            row.add(nameLbl, BorderLayout.WEST);
            row.add(statLbl, BorderLayout.EAST);
            
            playerListPanel.add(row);
            playerListPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        }
        playerListPanel.revalidate();
        playerListPanel.repaint();
    }

    private void prepareRollDice() {
        if (isAnimating) return;
        isAnimating = true;
        rollButton.setEnabled(false);

        Player currentPlayer = players.get(currentPlayerIndex);
        if (isPrime(currentPlayer.getPosition())) canClimbCurrentTurn = true;
        else canClimbCurrentTurn = false;

        SoundManager.play("assets/roll.wav");
        diceInfoLabel.setText("Mengocok...");

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
            diceResultLabel.setText(String.valueOf(steps));
            diceResultLabel.setForeground(StyleTheme.SUCCESS);
            diceInfoLabel.setText("Maju " + steps + " langkah");
            animateMovement(steps, true);
        } else {
            steps = 1;
            diceResultLabel.setText("1");
            diceResultLabel.setForeground(StyleTheme.DANGER);
            diceInfoLabel.setText("Mundur 1 langkah");
            animateMovement(steps, false);
        }
    }

    private int remainingStepsAfterLadder = 0;

    private void animateMovement(int stepsToWalk, boolean forward) {
        Player currentPlayer = players.get(currentPlayerIndex);
        final int[] stepsTaken = {0};

        Timer moveTimer = new Timer(300, null);
        moveTimer.addActionListener(e -> {
            if (stepsTaken[0] >= stepsToWalk) {
                ((Timer)e.getSource()).stop();
                if (remainingStepsAfterLadder > 0) {
                    int extra = remainingStepsAfterLadder;
                    remainingStepsAfterLadder = 0;
                    animateMovement(extra, true);
                    return;
                }
                finishTurn(currentPlayer);
                return;
            }

            SoundManager.play("assets/step.wav");

            if (forward) currentPlayer.moveForward(); else currentPlayer.moveBackward();
            stepsTaken[0]++;

            boardPanel.repaint();
            refreshPlayerList();

            Tile tile = board.getTileByNumber(currentPlayer.getPosition());
            if (tile != null && tile.isLadder()) {
                if (canClimbCurrentTurn) {
                    ((Timer)e.getSource()).stop();
                    diceInfoLabel.setText("Naik Tangga!");
                    SoundManager.play("assets/roll.wav");
                    remainingStepsAfterLadder = stepsToWalk - stepsTaken[0];
                    Timer jumpTimer = new Timer(600, ev -> {
                        currentPlayer.setPosition(tile.getDestination());
                        boardPanel.repaint();
                        if (remainingStepsAfterLadder > 0) {
                            int extraSteps = remainingStepsAfterLadder;
                            remainingStepsAfterLadder = 0;
                            animateMovement(extraSteps, true);
                        } else {
                            finishTurn(currentPlayer);
                        }
                    });
                    jumpTimer.setRepeats(false);
                    jumpTimer.start();
                } else {
                    diceInfoLabel.setText("Gagal naik (Bukan Prima)");
                }
            }
        });
        moveTimer.start();
    }

    private void finishTurn(Player currentPlayer) {
        Tile t = board.getTileByNumber(currentPlayer.getPosition());
        if (t != null) {
            int pts = t.getPoints();
            currentPlayer.addScore(pts);
            diceInfoLabel.setText("Dapat +" + pts + " Poin!");
        }
        refreshPlayerList();

        // --- CEK KEMENANGAN ---
        if (currentPlayer.getPosition() == 64) {
            SoundManager.play("assets/win.wav");

            // --- SIMPAN KEMENANGAN KE DATABASE ---
            WinDatabase.addWin(currentPlayer.getName());
            // -------------------------------------

            SwingUtilities.invokeLater(() -> showPremiumWinDialog(currentPlayer));
            return;
        }

        if (currentPlayer.getPosition() % 5 != 0) { 
            isAnimating = false;
            rollButton.setEnabled(true);
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            updateTurnUI();
        } else {
            diceInfoLabel.setText("Bonus Giliran!");
            isAnimating = false;
            rollButton.setEnabled(true);
        }
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) if (n % i == 0) return false;
        return true;
    }

    // ==========================================
    // POP-UP KEMENANGAN MEWAH
    // ==========================================
    private void showPremiumWinDialog(Player winner) {
        JDialog dialog = new JDialog(this, "Victory", true);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setSize(550, 750); 

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(StyleTheme.WARM_WHITE);
                g2.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 50, 50);
                
                g2.setColor(StyleTheme.GOLD);
                g2.setStroke(new BasicStroke(6f));
                g2.drawRoundRect(10, 10, getWidth()-20, getHeight()-20, 50, 50);

                g2.setColor(StyleTheme.SANTA_RED);
                int ribbonSize = 70;
                g2.fillArc(10, 10, ribbonSize, ribbonSize, 90, 90);
                g2.fillArc(getWidth()-10-ribbonSize, 10, ribbonSize, ribbonSize, 0, 90);
            }
        };
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(50, 50, 50, 50)); 
        contentPanel.setOpaque(false);

        JLabel iconLbl = new JLabel("🎄🎅🎄"); 
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(iconLbl);

        JLabel winText = new JLabel("VICTORY!");
        winText.setFont(StyleTheme.fontBold(48));
        winText.setForeground(StyleTheme.SANTA_RED);
        winText.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(winText);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel subText = new JLabel("Selamat, " + winner.getName() + "!");
        subText.setFont(StyleTheme.font(20));
        subText.setForeground(StyleTheme.DARK_TEXT);
        subText.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subText);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // RANKING (Sesi Ini)
        JPanel rankContainer = new JPanel();
        rankContainer.setLayout(new BoxLayout(rankContainer, BoxLayout.Y_AXIS));
        rankContainer.setOpaque(false);

        List<Player> finalRank = new ArrayList<>(players);
        finalRank.sort((p1, p2) -> p2.getScore() - p1.getScore()); 

        for (int i = 0; i < finalRank.size(); i++) {
            Player p = finalRank.get(i);
            JPanel playerCard = createFestiveRankCard(p, i + 1);
            rankContainer.add(playerCard);
            rankContainer.add(Box.createRigidArea(new Dimension(0, 10))); 
        }
        contentPanel.add(rankContainer);
        contentPanel.add(Box.createVerticalGlue());

        // TOMBOL
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(450, 70)); 
        btnPanel.setPreferredSize(new Dimension(450, 70));

        JButton playBtn = StyleTheme.createModernButton("MAIN LAGI", StyleTheme.PINE_GREEN);
        playBtn.setForeground(Color.WHITE);
        playBtn.setFont(StyleTheme.fontBold(16));
        playBtn.addActionListener(e -> {
            dialog.dispose();
            this.dispose();
            Main.main(null);
        });

        JButton exitBtn = StyleTheme.createModernButton("KELUAR", StyleTheme.SANTA_RED);
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(StyleTheme.fontBold(16));
        exitBtn.addActionListener(e -> System.exit(0));

        btnPanel.add(playBtn);
        btnPanel.add(exitBtn);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(btnPanel);

        dialog.add(contentPanel);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private JPanel createFestiveRankCard(Player p, int rank) {
        boolean isWinner = (rank == 1);
        Color cardBg = isWinner ? new Color(255, 248, 220) : Color.WHITE;
        Color borderColor = isWinner ? StyleTheme.GOLD : (rank % 2 == 0 ? StyleTheme.SANTA_RED : StyleTheme.PINE_GREEN);
        String rankIcon = isWinner ? "👑" : (rank == 2 ? "🥈" : (rank == 3 ? "🥉" : "#" + rank));

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cardBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(isWinner ? 3f : 2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 30, 30);
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(450, 55));
        card.setPreferredSize(new Dimension(450, 55));
        card.setBorder(new EmptyBorder(5, 20, 5, 20));

        JLabel rankLbl = new JLabel(rankIcon);
        rankLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, isWinner ? 24 : 18));
        rankLbl.setPreferredSize(new Dimension(50, 0));

        JLabel nameLbl = new JLabel(p.getName());
        nameLbl.setFont(StyleTheme.fontBold(18));
        nameLbl.setForeground(StyleTheme.DARK_TEXT);

        JLabel scoreLbl = new JLabel(p.getScore() + " Pts");
        scoreLbl.setFont(StyleTheme.fontBold(18));
        scoreLbl.setForeground(isWinner ? StyleTheme.GOLD.darker() : p.getColor());

        card.add(rankLbl, BorderLayout.WEST);
        card.add(nameLbl, BorderLayout.CENTER);
        card.add(scoreLbl, BorderLayout.EAST);

        return card;
    }
}