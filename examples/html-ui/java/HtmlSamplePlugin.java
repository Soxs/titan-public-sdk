package net.titan.sample;

import net.titan.api.html.HtmlMessage;
import net.titan.api.html.HtmlPanels;
import net.titan.api.html.HtmlOverlays;
import net.titan.api.overlay.OverlayPanelAnchor;
import net.titan.api.plugins.HtmlOverlayPanel;
import net.titan.api.plugins.HtmlSidePanel;
import net.titan.api.plugins.Plugin;
import net.titan.api.plugins.PluginDescriptor;

/** Both UI bundles are packaged by Gradle in the same plugin JAR. */
@PluginDescriptor(id="java_html_sample", name="Java HTML Sample", description="Packaged HTML state and messages",
    author="Titan", version="0.1.0", defaultEnabled=false)
@HtmlSidePanel(id="counter", title="HTML Counter", icon="lucide:code", resourceRoot="html-counter")
@HtmlOverlayPanel(id="counter", resourceRoot="html-hud", anchor=OverlayPanelAnchor.TOP_CENTER,
    width=220, height=160, interactive=false)
public final class HtmlSamplePlugin implements Plugin {
    private final HtmlPanels panels = HtmlPanels.of(this);
    private final HtmlOverlays overlays = HtmlOverlays.of(this);
    private int count;
    private boolean showCount = true;
    @Override public void onLoad() { publish(); }
    @Override public void onHtmlPanelMessage(String id, HtmlMessage message) {
        if (!id.equals("counter")) return;
        if (message.type().equals("increment")) ++count;
        else if (message.type().equals("showCount")) showCount = true;
        else if (message.type().equals("hideCount")) showCount = false;
        else return;
        publish();
        panels.postMessage(id, "updated", Integer.toString(count), message.correlationId().orElse(null));
    }
    private void publish() {
        String state = "{\"count\":" + count + ",\"showCount\":" + showCount + "}";
        panels.setState("counter", state);
        overlays.setState("counter", state);
    }
}
