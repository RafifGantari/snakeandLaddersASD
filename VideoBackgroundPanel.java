import javax.swing.*;
import java.awt.*;

public class VideoBackgroundPanel extends JLayeredPane {
    private JLabel backgroundLabel;
    private ImageIcon backgroundIcon;

    public VideoBackgroundPanel(String gifPath) {
        setLayout(null); // Wajib null agar kita bisa atur layer manual
        try {
            backgroundIcon = new ImageIcon(gifPath);
            backgroundLabel = new JLabel(backgroundIcon);
        } catch (Exception e) {
            backgroundLabel = new JLabel();
        }

        // Layer 0: Background
        add(backgroundLabel, Integer.valueOf(0));
    }

    public void addForeground(Component comp) {
        // Layer 1: Konten (Game / Menu)
        add(comp, Integer.valueOf(1));
        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {
        // Paksa Background & Konten memenuhi layar
        Dimension d = getSize();
        
        // Atur background
        if (backgroundLabel != null) {
            backgroundLabel.setBounds(0, 0, d.width, d.height);
        }

        // Atur semua komponen foreground
        for (Component comp : getComponents()) {
            if (getLayer(comp) > 0) { // Jika bukan background
                comp.setBounds(0, 0, d.width, d.height);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (backgroundIcon != null) {
            return new Dimension(backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        }
        return super.getPreferredSize();
    }
}