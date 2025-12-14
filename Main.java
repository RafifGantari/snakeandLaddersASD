import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private static final String[] PLAYER_ICONS = {"🎅", "🦌", "⛄", "🍪", "🧦", "🎁"};

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(Main::createStartScreen);
    }

    // ==========================================
    // 1. SCREEN MENU UTAMA (START)
    // ==========================================
    private static void createStartScreen() {
        JFrame startFrame = new JFrame("Snake & Ladders - Christmas Party");
        startFrame.setSize(1100, 900);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);

        // Background
        FestivePanel bgPanel = new FestivePanel();
        startFrame.setContentPane(bgPanel);
        bgPanel.setLayout(new GridBagLayout());

        // Kartu Menu
        ShadowPanel menuCard = new ShadowPanel(550, 580); // Tinggi ditambah sedikit untuk tombol baru
        menuCard.setLayout(new BoxLayout(menuCard, BoxLayout.Y_AXIS));
        menuCard.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Header Icon
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        iconPanel.setOpaque(false);
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 50);
        
        JLabel tree1 = new JLabel("🎄"); tree1.setFont(emojiFont);
        JLabel star  = new JLabel("⭐"); star.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        JLabel tree2 = new JLabel("🎄"); tree2.setFont(emojiFont);
        
        iconPanel.add(tree1); iconPanel.add(star); iconPanel.add(tree2);

        // Judul
        JLabel titleLabel = new JLabel("SNAKE & LADDERS");
        titleLabel.setFont(StyleTheme.fontBold(36));
        titleLabel.setForeground(StyleTheme.SANTA_RED); 
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Christmas Party Edition");
        subLabel.setFont(StyleTheme.font(20));
        subLabel.setForeground(StyleTheme.PINE_GREEN); 
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Info
        JLabel infoLabel = new JLabel("Pilih Jumlah Pemain");
        infoLabel.setFont(StyleTheme.font(16));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Dropdown
        String[] options = {"2 Players", "3 Players", "4 Players", "5 Players", "6 Players"};
        JComboBox<String> playerCombo = new JComboBox<>(options);
        styleModernComboBox(playerCombo);
        playerCombo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- TOMBOL RANKING (BARU) ---
        JButton rankButton = StyleTheme.createModernButton("RANKING JUARA 🏆", StyleTheme.PINE_GREEN);
        rankButton.setForeground(Color.WHITE);
        rankButton.setPreferredSize(new Dimension(260, 50));
        rankButton.setMaximumSize(new Dimension(260, 50));
        rankButton.setFont(StyleTheme.fontBold(16));
        rankButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Aksi Tombol Ranking
        rankButton.addActionListener(e -> showRankingDialog(startFrame));
        // ------------------------------

        // Tombol Setup (Merah Besar)
        JButton startButton = StyleTheme.createModernButton("SETUP PEMAIN", StyleTheme.SANTA_RED);
        startButton.setForeground(Color.WHITE);
        startButton.setPreferredSize(new Dimension(260, 65));
        startButton.setMaximumSize(new Dimension(260, 65));
        startButton.setFont(StyleTheme.fontBold(20));
        
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> {
            int totalPlayers = playerCombo.getSelectedIndex() + 2;
            startFrame.dispose();
            showCustomNameInput(totalPlayers);
        });

        // Susun Layout
        menuCard.add(iconPanel);
        menuCard.add(Box.createRigidArea(new Dimension(0, 20)));
        menuCard.add(titleLabel);
        menuCard.add(subLabel);
        menuCard.add(Box.createRigidArea(new Dimension(0, 30))); 
        menuCard.add(infoLabel);
        menuCard.add(Box.createRigidArea(new Dimension(0, 10)));
        menuCard.add(playerCombo);
        
        menuCard.add(Box.createRigidArea(new Dimension(0, 20))); 
        menuCard.add(rankButton); // Tambah tombol ranking ke layout

        menuCard.add(Box.createRigidArea(new Dimension(0, 20))); 
        menuCard.add(startButton);

        bgPanel.add(menuCard);
        startFrame.setVisible(true);
    }

    // --- POP-UP RANKING (BARU) ---
    private static void showRankingDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Hall of Fame", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Header
        JLabel title = new JLabel("HALL OF FAME", JLabel.CENTER);
        title.setFont(StyleTheme.fontBold(24));
        title.setForeground(StyleTheme.SANTA_RED);
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        // List Container
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Ambil data dari WinDatabase
        List<java.util.Map.Entry<String, Integer>> ranking = WinDatabase.getLeaderboard();

        if (ranking.isEmpty()) {
            JLabel empty = new JLabel("Belum ada data kemenangan.", JLabel.CENTER);
            empty.setFont(StyleTheme.font(16));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalGlue());
            listPanel.add(empty);
            listPanel.add(Box.createVerticalGlue());
        } else {
            int rank = 1;
            for (java.util.Map.Entry<String, Integer> entry : ranking) {
                // Style Panel per Baris
                Color rowColor = (rank == 1) ? new Color(255, 250, 230) : new Color(245, 245, 250);
                JPanel row = StyleTheme.createRoundedPanel(15, rowColor);
                row.setLayout(new BorderLayout());
                row.setMaximumSize(new Dimension(450, 55));
                row.setPreferredSize(new Dimension(450, 55));
                row.setBorder(new EmptyBorder(10, 20, 10, 20));

                String rankStr = (rank == 1) ? "👑" : "#" + rank;
                JLabel nameLbl = new JLabel(rankStr + "  " + entry.getKey());
                nameLbl.setFont(StyleTheme.fontBold(16));
                nameLbl.setForeground(StyleTheme.DARK_TEXT);
                
                JLabel scoreLbl = new JLabel(entry.getValue() + " Wins");
                scoreLbl.setFont(StyleTheme.fontBold(16));
                scoreLbl.setForeground(StyleTheme.SANTA_RED);

                if (rank == 1) { 
                    nameLbl.setForeground(new Color(184, 134, 11)); // Warna Emas Gelap
                    scoreLbl.setForeground(new Color(184, 134, 11));
                }

                row.add(nameLbl, BorderLayout.WEST);
                row.add(scoreLbl, BorderLayout.EAST);
                
                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                rank++;
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // ==========================================
    // 2. SCREEN INPUT NAMA
    // ==========================================
    private static void showCustomNameInput(int totalPlayers) {
        JDialog dialog = new JDialog((Frame) null, "Daftar Pemain", true);
        int dialogHeight = 450 + (totalPlayers * 60);
        if(dialogHeight > 850) dialogHeight = 850; 
        dialog.setSize(700, dialogHeight);
        dialog.setLocationRelativeTo(null);

        FestivePanel bgPanel = new FestivePanel();
        dialog.setContentPane(bgPanel);
        bgPanel.setLayout(new GridBagLayout());

        ShadowPanel inputCard = new ShadowPanel(550, dialogHeight - 100);
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));
        inputCard.setBorder(new EmptyBorder(40, 60, 40, 60));
        
        JLabel head = new JLabel("Daftar Nama Pemain");
        head.setFont(StyleTheme.fontBold(28));
        head.setForeground(StyleTheme.SANTA_RED);
        head.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        inputCard.add(head);
        inputCard.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        List<JTextField> inputs = new ArrayList<>();

        for (int i = 0; i < totalPlayers; i++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(450, 50));
            
            String iconChar = PLAYER_ICONS[i % PLAYER_ICONS.length];
            JLabel iconLbl = new JLabel(iconChar);
            iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
            iconLbl.setBorder(new EmptyBorder(0, 0, 0, 15));

            JLabel lbl = new JLabel("P" + (i + 1));
            lbl.setFont(StyleTheme.fontBold(20));
            lbl.setForeground(StyleTheme.PINE_GREEN);
            
            JTextField tf = createStyledTextField("Nama Pemain " + (i+1));
            inputs.add(tf);

            JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            leftInfo.setOpaque(false);
            leftInfo.add(iconLbl);
            leftInfo.add(lbl);
            leftInfo.setPreferredSize(new Dimension(100, 0));

            row.add(leftInfo, BorderLayout.WEST);
            row.add(tf, BorderLayout.CENTER);
            
            fieldsPanel.add(row);
            fieldsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        if (totalPlayers > 4) {
            JScrollPane scroll = new JScrollPane(fieldsPanel);
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            inputCard.add(scroll);
        } else {
            inputCard.add(fieldsPanel);
        }

        inputCard.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- TOMBOL MULAI MAIN ---
        JButton goBtn = StyleTheme.createModernButton("MULAI MAIN 🎲", StyleTheme.SANTA_RED);
        goBtn.setForeground(Color.WHITE); 
        goBtn.setPreferredSize(new Dimension(260, 65));
        goBtn.setMaximumSize(new Dimension(260, 65));
        goBtn.setFont(StyleTheme.fontBold(20));
        
        goBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        goBtn.addActionListener(ev -> {
            List<String> names = new ArrayList<>();
            for (JTextField tf : inputs) {
                String val = tf.getText().trim();
                if (val.isEmpty() || val.startsWith("Nama Pemain")) val = "Player " + (inputs.indexOf(tf) + 1);
                names.add(val);
            }
            dialog.dispose();
            new GameFrame(names); 
        });

        inputCard.add(goBtn);
        bgPanel.add(inputCard);

        dialog.setVisible(true);
    }

    // ==========================================
    // UTILITIES
    // ==========================================

    static class ShadowPanel extends JPanel {
        public ShadowPanel(int w, int h) {
            setOpaque(false);
            setMinimumSize(new Dimension(w, h));
            setPreferredSize(new Dimension(w, h));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 40, 40);
            
            g2.setColor(new Color(253, 253, 250)); 
            g2.fillRoundRect(0, 0, getWidth()-16, getHeight()-16, 40, 40);
            
            g2.setColor(StyleTheme.GOLD);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, getWidth()-16, getHeight()-16, 40, 40);
            
            super.paintComponent(g);
        }
    }

    private static void styleModernComboBox(JComboBox<String> combo) {
        combo.setFont(StyleTheme.fontBold(16));
        combo.setForeground(StyleTheme.DARK_TEXT);
        combo.setBackground(Color.WHITE);
        combo.setMaximumSize(new Dimension(300, 55));
        combo.setPreferredSize(new Dimension(300, 55));
        combo.setFocusable(false);

        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▼");
                btn.setFont(new Font("Arial", Font.BOLD, 12));
                btn.setForeground(Color.WHITE);
                btn.setBackground(StyleTheme.SANTA_RED);
                btn.setBorder(null);
                btn.setContentAreaFilled(false);
                btn.setOpaque(true);
                return btn;
            }
            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox);
                popup.setBorder(new MatteBorder(2, 2, 2, 2, StyleTheme.GOLD));
                return popup;
            }
        });

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(CENTER);
                setBorder(new EmptyBorder(10, 10, 10, 10));
                if (isSelected) {
                    setBackground(new Color(255, 230, 230)); 
                    setForeground(StyleTheme.SANTA_RED);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(StyleTheme.DARK_TEXT);
                }
                return this;
            }
        });

        combo.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 5)
        ));
    }

    private static JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(StyleTheme.font(16));
        field.setForeground(Color.GRAY);
        field.setOpaque(false);
        field.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 3, 0, new Color(220, 220, 220)), 
            new EmptyBorder(5, 5, 5, 5)
        ));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                field.setForeground(StyleTheme.DARK_TEXT);
                field.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 3, 0, StyleTheme.SANTA_RED), 
                    new EmptyBorder(5, 5, 5, 5)
                ));
                if (field.getText().equals(placeholder)) field.setText("");
            }
            public void focusLost(FocusEvent evt) {
                field.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 3, 0, new Color(220, 220, 220)), 
                    new EmptyBorder(5, 5, 5, 5)
                ));
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    static class FestivePanel extends JPanel {
        private List<Point> snowflakes = new ArrayList<>();
        private List<Point> ornaments = new ArrayList<>();
        private Color[] ornamentColors = {Color.RED, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.ORANGE};

        public FestivePanel() {
            Random r = new Random();
            for (int i = 0; i < 200; i++) snowflakes.add(new Point(r.nextInt(1200), r.nextInt(1000)));
            for (int i = 0; i < 25; i++) ornaments.add(new Point(r.nextInt(1100), r.nextInt(900)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(0, 0, new Color(10, 20, 50), 0, getHeight(), new Color(60, 20, 20));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(255, 255, 255, 80));
            for (Point p : snowflakes) g2.fillOval(p.x, p.y, 4, 4);

            for (int i = 0; i < ornaments.size(); i++) {
                Point p = ornaments.get(i);
                Color baseColor = ornamentColors[i % ornamentColors.length];
                RadialGradientPaint paint = new RadialGradientPaint(p.x, p.y, 40, new float[]{0f, 1f}, 
                    new Color[]{new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 80), new Color(0,0,0,0)});
                g2.setPaint(paint);
                g2.fillOval(p.x - 40, p.y - 40, 80, 80);
            }
        }
    }
}