import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createStartScreen());
    }

    private static void createStartScreen() {
        JFrame startFrame = new JFrame("Snake & Ladders - Setup");
        startFrame.setSize(400, 500);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLayout(new BorderLayout());
        startFrame.getContentPane().setBackground(Color.WHITE);

        // --- PANEL TENGAH (ISI) ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(50, 40, 50, 40));

        // 1. Ikon / Judul Besar
        JLabel titleLabel = new JLabel("SNAKE & LADDERS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80)); // Dark Blue
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        JLabel subTitleLabel = new JLabel("Pro Edition");
        subTitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subTitleLabel.setForeground(Color.GRAY);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(subTitleLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 60))); // Spasi

        // 2. Label Pilihan
        JLabel infoLabel = new JLabel("Pilih Jumlah Pemain:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(infoLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 3. Dropdown (JComboBox)
        String[] options = {"2 Players", "3 Players", "4 Players", "5 Players", "6 Players"};
        JComboBox<String> playerCombo = new JComboBox<>(options);
        playerCombo.setMaximumSize(new Dimension(200, 40));
        playerCombo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ((JLabel)playerCombo.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(playerCombo);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        // 4. Tombol Start
        JButton startButton = new JButton("START GAME");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(39, 174, 96)); // Green
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Aksi Tombol
        startButton.addActionListener(e -> {
            int selectedIndex = playerCombo.getSelectedIndex();
            int totalPlayers = selectedIndex + 2; // Index 0 = 2 Players
            
            startFrame.dispose(); // Tutup layar start
            new GameFrame(totalPlayers); // Buka Game Utama
        });
        centerPanel.add(startButton);

        startFrame.add(centerPanel, BorderLayout.CENTER);
        startFrame.setLocationRelativeTo(null);
        startFrame.setVisible(true);
    }
}