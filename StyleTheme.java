import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StyleTheme {
    public static final Color BG_GRADIENT_1 = new Color(224, 247, 250); 
    public static final Color BG_GRADIENT_2 = new Color(129, 212, 250); 
    public static final Color PRIMARY_COLOR = new Color(2, 119, 189);   
    public static final Color ACCENT_COLOR  = new Color(79, 195, 247);  
    public static final Color TEXT_COLOR    = new Color(38, 50, 56);    
    public static final Color WIN_COLOR     = new Color(255, 111, 0);   

    public static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, BG_GRADIENT_1, 0, getHeight(), BG_GRADIENT_2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public static JButton createButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(PRIMARY_COLOR.darker());
                else if (getModel().isRollover()) g2.setColor(ACCENT_COLOR);
                else g2.setColor(PRIMARY_COLOR);
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 4;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 45));
        return btn;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                super.paintComponent(g); 
                g2.setColor(new Color(176, 190, 197)); 
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(5, 15, 5, 15));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }
}