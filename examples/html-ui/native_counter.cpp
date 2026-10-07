// Build as a normal native SDK plugin. Every asset is embedded in this DLL.
#include <titan/plugin.h>
#include <titan/overlay_panel.h>

class HtmlCounter final : public titan::Plugin {
    TITAN_PLUGIN("cpp_html_sample", "C++ HTML Sample")
public:
    HtmlCounter() {
        titan::HtmlPanelBundle bundle("index.html");
        bundle.text("index.html", "text/html", R"HTML(<!doctype html>
<html><head><meta charset="utf-8"><link rel="stylesheet" href="style.css"></head>
<body><h1>Counter</h1><output id="count">0</output><button id="increment">Increment</button>
<p><label><input id="showCount" type="checkbox" checked> Show count in HUD</label></p>
<p id="reply"></p><script src="app.js"></script></body></html>)HTML");
        bundle.text("style.css", "text/css", R"CSS(
body{background:var(--titan-bg);color:var(--titan-text);font:14px sans-serif;margin:16px}
button{background:var(--titan-accent);color:var(--titan-text);border:1px solid var(--titan-border);padding:8px}
output{display:block;font-size:28px;margin:12px 0}h1{font-size:18px})CSS");
        bundle.text("app.js", "application/javascript", R"JS(
'use strict';
function render(state){document.getElementById('count').textContent=String(state&&state.count||0);document.getElementById('showCount').checked=!state||state.showCount!==false;}
titanHtml.onState(render);render(titanHtml.getState());
document.getElementById('increment').addEventListener('click',function(){titanHtml.postMessage('increment',{},'counter-click');});
document.getElementById('showCount').addEventListener('change',function(){titanHtml.postMessage(this.checked?'showCount':'hideCount',null,'counter-setting');});
titanHtml.onMessage(function(message){if(message.type==='updated')document.getElementById('reply').textContent='Saved '+String(message.payload);});
)JS");
        panel_ = &htmlPanel("counter", "HTML Counter", bundle);
        panel_->icon("lucide:code").onMessage([this](const titan::HtmlPanelMessage& message) {
            if (message.type == "increment") ++count_;
            else if (message.type == "showCount") showCount_ = true;
            else if (message.type == "hideCount") showCount_ = false;
            else return;
            publish();
            panel_->postMessage("updated", std::to_string(count_), message.correlationId);
        });
        // The HUD has its own transparent document and remains click-through.
        // Same ID is legal in the separate overlay namespace.
        titan::HtmlPanelBundle hud("index.html");
        hud.text("index.html", "text/html", R"HTML(<!doctype html>
<html><head><meta charset="utf-8"><link rel="stylesheet" href="style.css"></head>
<body><output id="hud">Count: <span id="count">0</span></output><script src="app.js"></script></body></html>)HTML");
        hud.text("style.css", "text/css", R"CSS(
html,body{margin:0;background:transparent;overflow:hidden}
body{padding:8px;color:var(--titan-text);font:16px sans-serif}
output{display:inline-block;padding:6px 10px;background:rgba(20,24,33,.75);border:1px solid var(--titan-border);border-radius:4px})CSS");
        hud.text("app.js", "application/javascript", R"JS(
'use strict';function render(state){document.getElementById('count').textContent=String(state&&state.count||0);document.getElementById('hud').style.display=state&&state.showCount===false?'none':'inline-block';}
titanHtml.onState(render);render(titanHtml.getState());
)JS");
        titan::HtmlOverlayOptions options;
        options.anchor = static_cast<uint8_t>(titan::Anchor::TopCenter);
        options.width = 220;
        options.height = 160;
        overlay_ = &htmlOverlayPanel("counter", hud, options);
        publish();
    }
private:
    void publish() {
        const auto state = "{\"count\":" + std::to_string(count_) + ",\"showCount\":"
            + (showCount_ ? "true}" : "false}");
        panel_->setState(state);
        overlay_->setState(state);
    }
    int count_ = 0;
    bool showCount_ = true;
    titan::HtmlSidePanel* panel_ = nullptr;
    titan::HtmlOverlayPanel* overlay_ = nullptr;
};
TITAN_REGISTER_PLUGIN(HtmlCounter, "cpp_html_sample")
