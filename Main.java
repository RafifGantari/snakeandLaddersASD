import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(Main::createStartScreen);

        System.out.println(new File("assets/background.gif").getAbsolutePath());
        System.out.println(new File("assets/background.gif").exists());
    }

    // ======================
    //       START SCREEN
    // ======================
    private static void createStartScreen() {
        JFrame startFrame = new JFrame("Snake & Ladders - Winter Pro");
        startFrame.setSize(1100, 900);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLocationRelativeTo(null);

        // Layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); // WAJIB!

        // === BACKGROUND GIF ===
        ImageIcon bgGif = new ImageIcon("assets/background.gif");
        JLabel bgLabel = new JLabel(bgGif);
        bgLabel.setBounds(0, 0, 1100, 900);
        layeredPane.add(bgLabel, Integer.valueOf(0));

        // === UI PANEL ===
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBounds(0, 0, 1100, 900);

        JLabel titleLabel = new JLabel("SNAKE & LADDERS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Prime Rules Edition");
        subLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        mainPanel.add(titleLabel);
        mainPanel.add(subLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 80)));

        JLabel infoLabel = new JLabel("Jumlah Pemain:");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        String[] options = {"2 Players", "3 Players", "4 Players", "5 Players"};
        JComboBox<String> playerCombo = new JComboBox<>(options);
        playerCombo.setMaximumSize(new Dimension(200, 40));
        playerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        ((JLabel) playerCombo.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(playerCombo);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton startButton = new JButton("SETUP NAMES");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> {
            int totalPlayers = playerCombo.getSelectedIndex() + 2;
            startFrame.dispose();
            showCustomNameInput(totalPlayers);
        });

        mainPanel.add(startButton);

        layeredPane.add(mainPanel, Integer.valueOf(1));

        startFrame.setContentPane(layeredPane);
        startFrame.setVisible(true);
    }

    // ======================
    //    INPUT PLAYER NAME
    // ======================
    private static void showCustomNameInput(int totalPlayers) {

        JDialog dialog = new JDialog((Frame) null, "Player Setup", true);
        dialog.setSize(500, 200 + totalPlayers * 70);
        dialog.setLocationRelativeTo(null);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        // ===== BACKGROUND GIF =====
        ImageIcon bgGif = new ImageIcon("assets/background.gif");
        JLabel bg = new JLabel(bgGif);
        bg.setBounds(0, 0, dialog.getWidth(), dialog.getHeight());
        layeredPane.add(bg, Integer.valueOf(0));

        // ===== UI PANEL =====
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBounds(0, 0, dialog.getWidth(), dialog.getHeight());

        JLabel head = new JLabel("Masukkan Nama Pemain");
        head.setFont(new Font("Segoe UI", Font.BOLD, 20));
        head.setForeground(Color.WHITE);
        head.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(head);

        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        List<JTextField> inputs = new ArrayList<>();

        for (int i = 0; i < totalPlayers; i++) {

            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(350, 40));

            JLabel lbl = new JLabel("Player " + (i + 1) + ": ");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(Color.WHITE);

            JTextField tf = new JTextField("Player " + (i + 1));
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            row.add(lbl, BorderLayout.WEST);
            row.add(tf, BorderLayout.CENTER);

            inputs.add(tf);
            panel.add(row);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JButton goBtn = new JButton("START GAME");
        goBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        goBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        goBtn.addActionListener(ev -> {
            List<String> names = new ArrayList<>();
            for (JTextField tf : inputs) {
                String val = tf.getText().trim();
                names.add(val.isEmpty() ? "Unknown" : val);
            }
            dialog.dispose();

            // Pastikan kamu punya class GameFrame
            new GameFrame(names);
        });

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(goBtn);

        layeredPane.add(panel, Integer.valueOf(1));

        dialog.setContentPane(layeredPane);
        dialog.setVisible(true);
    }
}
