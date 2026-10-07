'use strict';
function render(state) {
    document.getElementById('count').textContent = String(state && state.count || 0);
    document.getElementById('hud').style.display = state && state.showCount === false ? 'none' : 'inline-block';
}
titanHtml.onState(render);
render(titanHtml.getState());
