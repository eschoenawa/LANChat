package de.eschoenawa.lanchat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class DarkScrollbarUI extends MetalScrollBarUI {

    private Image imageThumb, imageTrack;
    private JButton b = new JButton() {

		private static final long serialVersionUID = 1L;

		@Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 0);
        }

    };

    public DarkScrollbarUI() {
        imageThumb = FauxImage.create(32, 32, Color.BLACK);
        imageTrack = FauxImage.create(32, 32, Color.DARK_GRAY);
    }

    @Override
    public void paintThumb(Graphics g, JComponent c, Rectangle r) {
        g.setColor(Color.blue);
        ((Graphics2D) g).drawImage(imageThumb,
            r.x, r.y, r.width, r.height, null);
    }

    @Override
    public void paintTrack(Graphics g, JComponent c, Rectangle r) {
        ((Graphics2D) g).drawImage(imageTrack,
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

private static class FauxImage {

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