package net.titan.sample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.titan.api.Fonts;
import net.titan.api.overlay.Graphics2DOverlay;
import net.titan.api.overlay.OverlayLayer;
import net.titan.api.overlay.OverlayPanelAnchor;
import net.titan.api.plugins.Plugin;
import net.titan.api.plugins.PluginDescriptor;
import net.titan.api.swing.SwingPanel;
import net.titan.api.swing.SwingPanels;

@PluginDescriptor(id = "java_swing_hud", name = "Swing Counter", author = "Titan",
    version = "1.0", description = "A Swing counter with a draggable Java2D overlay.", defaultEnabled = false)
public final class SwingHudSamplePlugin implements Plugin {
    private static final Color BACKGROUND = new Color(27, 32, 38, 218);
    private final AtomicInteger count = new AtomicInteger();
    private SwingPanel panel;
    private Graphics2DOverlay hud;
    private Font font;

    @Override public void onEnable() {
        font = Fonts.font(Fonts.Face.BOLD).orElseGet(() -> new Font(Font.DIALOG, Font.BOLD, 16));
        hud = Graphics2DOverlay.anchored("counter", 152, 38, OverlayPanelAnchor.TOP_CENTER, 50);
        panel = SwingPanels.of(this).register("counter", "Swing Counter", () -> {
            JLabel label = new JLabel("Count: " + count.get());
            JButton increment = new JButton("Add one");
            increment.addActionListener(event -> label.setText("Count: " + count.incrementAndGet()));
            JPanel content = new JPanel(new BorderLayout(0, 8));
            content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            content.add(label, BorderLayout.NORTH);
            content.add(increment, BorderLayout.SOUTH);
            return content;
        });
    }

    @Override public void renderOverlay(OverlayLayer layer) {
        Graphics2DOverlay current = hud;
        if (layer != OverlayLayer.ABOVE_WIDGETS || current == null) return;
        int value = count.get();
        current.renderCached(value, g -> {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(BACKGROUND);
            g.fillRoundRect(0, 0, current.width(), current.height(), 9, 9);
            g.setColor(Color.WHITE);
            g.setFont(font);
            g.drawString("Count: " + value, 12, 24);
        });
    }

    @Override public void onDisable() {
        if (panel != null) panel.close();
        if (hud != null) hud.close();
        panel = null;
        hud = null;
    }
}
