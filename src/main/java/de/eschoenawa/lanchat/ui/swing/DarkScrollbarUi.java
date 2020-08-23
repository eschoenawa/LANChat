package de.eschoenawa.lanchat.ui.swing;

import javax.swing.*;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DarkScrollbarUi extends MetalScrollBarUI {

    private Image imageThumb, imageTrack;
    private JButton b = new JButton() {

        private static final long serialVersionUID = 1L;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 0);
        }

    };

    public DarkScrollbarUi() {
        imageThumb = SingleColorImage.create(32, 32, Color.BLACK);
        imageTrack = SingleColorImage.create(32, 32, Color.DARK_GRAY);
    }

    @Override
    public void paintThumb(Graphics g, JComponent c, Rectangle r) {
        g.setColor(Color.blue);
        g.drawImage(imageThumb,
                r.x, r.y, r.width, r.height, null);
    }

    @Override
    public void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.drawImage(imageTrack,
                r.x, r.y, r.width, r.height, null);
    }

    @Override
    public JButton createDecreaseButton(int orientation) {
        return b;
    }

    @Override
    public JButton createIncreaseButton(int orientation) {
        return b;
    }

    private static class SingleColorImage {

        static public Image create(int w, int h, Color c) {
            BufferedImage bi = new BufferedImage(
                    w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            g2d.setPaint(c);
            g2d.fillRect(0, 0, w, h);
            g2d.dispose();
            return bi;
        }
    }
}