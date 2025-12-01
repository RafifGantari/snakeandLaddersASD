import javax.swing.*;
import java.awt.*;

/**
 * Simple layered panel that shows an animated GIF as background (using JLabel + ImageIcon)
 * and allows adding foreground components (game board, UI) on top.
 *
 * Usage:
 *   VideoBackgroundPanel bg = new VideoBackgroundPanel("./assets/background.gif");
 *   bg.addForeground(boardPanel);
 */
public class VideoBackgroundPanel extends JLayeredPane {
    private JLabel backgroundLabel;
    private ImageIcon backgroundIcon;

    public VideoBackgroundPanel(String gifPath) {
        setLayout(null); // we'll manually layout children so background covers whole area
        try {
            backgroundIcon = new ImageIcon(gifPath);
            backgroundLabel = new JLabel(backgroundIcon);
        } catch (Exception e) {
            backgroundLabel = new JLabel("Background not found");
            e.printStackTrace();
        }

        // Add background at default layer (lowest)
        add(backgroundLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER));
    }

    /**
     * Add a component to the foreground (above background). The component will be resized to fill the panel
     * unless you set bounds manually afterwards.
     */
    public void addForeground(Component comp) {
        add(comp, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        comp.setBounds(0, 0, getWidth(), getHeight());
        if (comp instanceof JComponent) {
            ((JComponent) comp).setOpaque(false); // usually foreground should be transparent
        }
        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {
        // ensure background covers entire area, and foreground components fill area by default
        Dimension d = getSize();
        if (backgroundLabel != null) {
            backgroundLabel.setBounds(0, 0, d.width, d.height);
        }

        // set bounds of all foreground children (PALETTE_LAYER and above) to fill container if their bounds are zero
        for (Component comp : getComponents()) {
            int layer = getLayer(comp);
            if (layer > JLayeredPane.DEFAULT_LAYER) {
                // only override if likely not manually set (x/y == 0 and size is 0)
                if (comp.getWidth() == 0 && comp.getHeight() == 0) {
                    comp.setBounds(0, 0, d.width, d.height);
                } else {
                    // if user set size, keep it; but ensure within container
                    comp.setBounds(comp.getX(), comp.getY(), Math.min(comp.getWidth(), d.width), Math.min(comp.getHeight(), d.height));
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // prefer background icon size if available
        if (backgroundIcon != null && backgroundIcon.getIconWidth() > 0) {
            return new Dimension(backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
        }
        return super.getPreferredSize();
    }
}
