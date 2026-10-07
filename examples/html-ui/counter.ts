// Compile with the public titan-plugin-sdk.d.ts. Resources remain in this script.
class HtmlCounterPlugin extends titan.Plugin {
    id = "js_html_sample";
    name = "JavaScript HTML Sample";
    enabled = false;
    private count = 0;
    private showCount = true;
    private readonly bundle: titan.HtmlPanelBundle = {
        version: 1,
        entrypoint: "index.html",
        resources: {
            "index.html": { mime: "text/html", content: `<!doctype html><html><head><meta charset="utf-8"><link rel="stylesheet" href="style.css"></head><body><h1>Counter</h1><output id="count">0</output><button id="increment">Increment</button><p><label><input id="showCount" type="checkbox" checked> Show count in HUD</label></p><p id="reply"></p><script src="app.js"></script></body></html>` },
            "style.css": { mime: "text/css", content: `body{background:var(--titan-bg);color:var(--titan-text);font:14px sans-serif;margin:16px}button{background:var(--titan-accent);color:var(--titan-text);border:1px solid var(--titan-border);padding:8px}output{display:block;font-size:28px;margin:12px 0}h1{font-size:18px}` },
            "app.js": { mime: "application/javascript", content: `'use strict';function render(s){document.getElementById('count').textContent=String(s&&s.count||0);document.getElementById('showCount').checked=!s||s.showCount!==false;}titanHtml.onState(render);render(titanHtml.getState());document.getElementById('increment').addEventListener('click',function(){titanHtml.postMessage('increment',{},'counter-click');});document.getElementById('showCount').addEventListener('change',function(){titanHtml.postMessage(this.checked?'showCount':'hideCount',null,'counter-setting');});titanHtml.onMessage(function(m){if(m.type==='updated')document.getElementById('reply').textContent='Saved '+String(m.payload);});` }
        }
    };
    private readonly hudBundle: titan.HtmlPanelBundle = {
        version: 1,
        entrypoint: "index.html",
        resources: {
            "index.html": { mime: "text/html", content: `<!doctype html><html><head><meta charset="utf-8"><link rel="stylesheet" href="style.css"></head><body><output id="hud">Count: <span id="count">0</span></output><script src="app.js"></script></body></html>` },
            "style.css": { mime: "text/css", content: `html,body{margin:0;background:transparent;overflow:hidden}body{padding:8px;color:var(--titan-text);font:16px sans-serif}output{display:inline-block;padding:6px 10px;background:rgba(20,24,33,.75);border:1px solid var(--titan-border);border-radius:4px}` },
            "app.js": { mime: "application/javascript", content: `'use strict';function render(s){document.getElementById('count').textContent=String(s&&s.count||0);document.getElementById('hud').style.display=s&&s.showCount===false?'none':'inline-block';}titanHtml.onState(render);render(titanHtml.getState());` }
        }
    };
    panels: titan.PanelDef[] = [{ kind: "html", id: "counter", title: "HTML Counter", icon: "lucide:code", bundle: this.bundle,
        onMessage: message => {
            if (message.type === "increment") ++this.count;
            else if (message.type === "showCount") this.showCount = true;
            else if (message.type === "hideCount") this.showCount = false;
            else return;
            this.publish();
            titan.htmlPanels.postMessage(this, "counter", "updated", this.count, message.id);
        }
    }];
    // A separate transparent, click-through HUD restores the same canonical state.
    private readonly hud = this.htmlOverlayPanel({ id: "counter", bundle: this.hudBundle, anchor: "TopCenter", width: 220, height: 160 });
    onEnable() { this.publish(); }
    private publish() {
        const state = { count: this.count, showCount: this.showCount };
        titan.htmlPanels.setState(this, "counter", state);
        this.hud.setState(state);
    }
}
titan.register(new HtmlCounterPlugin());
