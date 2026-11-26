/// <reference path="../../titan-plugin-sdk.d.ts" />

class BundledUiAssetsExample extends titan.Plugin {
    id = 'bundled_ui_assets_example';
    name = 'Bundled UI Assets';
    private sailing = titan.skillIcons.get(titan.Skill.SAILING);
    private iconOverlay = this.overlay({
        layer: titan.OverlayLayer.ABOVE_WIDGETS,
        render: () => {
            if (this.sailing) titan.overlay.sprite(this.sailing, 24, 24);
        }
    });

    constructor() {
        super();
        const font = titan.fonts.data('regular');
        if (!font) { titan.log('The bundled regular font is unavailable.'); return; }
        this.panels = [{
            kind: 'html', id: 'font-example', title: 'Bundled font',
            bundle: {
                version: 1, entrypoint: 'index.html',
                resources: {
                    'fonts/regular.ttf': { mime: 'font/ttf', content: font },
                    'index.html': { mime: 'text/html', content: `<!doctype html>
<meta charset="utf-8"><style>
@font-face { font-family: TitanBundled; src: url('fonts/regular.ttf') format('truetype'); }
body { color: #eee; background: #202225; font-family: TitanBundled, sans-serif; font-size: 18px; }
</style><p>This panel uses the bundled regular font.</p>` }
                }
            }
        }];
    }
}
titan.register(new BundledUiAssetsExample());
