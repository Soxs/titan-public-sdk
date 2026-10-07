'use strict';
const count = document.getElementById('count');
function render(state) {
    count.textContent = String(state && state.count || 0);
    document.getElementById('showCount').checked = !state || state.showCount !== false;
}
titanHtml.onState(render);
render(titanHtml.getState());
document.getElementById('increment').addEventListener('click', function () {
    titanHtml.postMessage('increment', {}, 'counter-click');
});
document.getElementById('showCount').addEventListener('change', function () {
    titanHtml.postMessage(this.checked ? 'showCount' : 'hideCount', null, 'counter-setting');
});
titanHtml.onMessage(function (message) {
    if (message.type === 'updated') document.getElementById('reply').textContent = 'Saved ' + String(message.payload);
});
