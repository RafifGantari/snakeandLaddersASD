import javax.swing.*;
import java.awt.*;

public class StyleTheme {
    // --- PALET WARNA NATAL ---
    public static final Color SANTA_RED     = new Color(220, 20, 60);   
    public static final Color PINE_GREEN    = new Color(34, 139, 34);   
    public static final Color GOLD          = new Color(255, 215, 0);   
    public static final Color WARM_WHITE    = new Color(250, 245, 235); 
    public static final Color NIGHT_BLUE    = new Color(25, 25, 112);   

    // Mapping Variable
    public static final Color PRIMARY     = SANTA_RED;
    public static final Color ACCENT      = GOLD;
    public static final Color SUCCESS     = PINE_GREEN;
    public static final Color DANGER      = new Color(178, 34, 34);
    public static final Color DARK_TEXT   = new Color(50, 50, 50);
    public static final Color SIDEBAR_BG  = WARM_WHITE; 
    public static final Color CARD_BG     = Color.WHITE; 

    public static Font font(int size) {
        return new Font("Segoe UI", Font.PLAIN, size);
    }
    
    public static Font fontBold(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    // --- TOMBOL MODERN 3D (LEBIH GAGAH & RAPI) ---
    public static JButton createModernButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth();
                int h = getHeight();
                
                // 1. Bayangan Bawah (Efek 3D Tebal)
                g2.setColor(baseColor.darker().darker());
                g2.fillRoundRect(0, 6, w, h-6, 20, 20);

                // 2. Warna Utama (Animasi Tekan)
                if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker());
                    // Kalau ditekan, tombol turun
                    g2.fillRoundRect(0, 6, w, h-6, 20, 20); 
                } else {
                    g2.setColor(baseColor);
                    // Kalau diam, tombol di atas
                    g2.fillRoundRect(0, 0, w, h-6, 20, 20); 
                }

                // 3. Kilauan Halus di Atas (Glassy look)
                if (!getModel().isPressed()) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, w, h/2, 20, 20);
                }

                // 4. Border Emas Tipis
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2f));
                if (getModel().isPressed()) {
                    g2.drawRoundRect(1, 7, w-2, h-8, 20, 20);
                } else {
                    g2.drawRoundRect(1, 1, w-2, h-8, 20, 20);
                }

                // 5. Teks
                g2.setColor(Color.WHITE);
                g2.setFont(fontBold(18)); // Font diperbesar
                FontMetrics fm = g2.getFontMetrics();
                int textX = (w - fm.stringWidth(getText())) / 2;
                int textY = (h - 6 + fm.getAscent()) / 2 - 4; 
                
                if (getModel().isPressed()) textY += 6; // Teks ikut turun
                
                // Shadow Teks biar kontras
                g2.setColor(new Color(0,0,0,50));
                g2.drawString(getText(), textX+1, textY+2);
                
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), textX, textY);
                
                g2.dispose();
            }
        };
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JPanel createRoundedPanel(int radius, Color bgColor) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            }
        };
    }
}