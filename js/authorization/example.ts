/// <reference path="../../titan-plugin-sdk.d.ts" />

class ManagedInstanceSample extends titan.Plugin {
    id = "managed_instance_sample";
    name = "Managed Instance Sample";
    private ticks = 0;
    private refreshing = false;
    private access = titan.entitlements(this);
    private bot: titan.InstanceBot = {
        start: () => { this.ticks = 0; },
        tick: () => { this.ticks++; /* Existing bot work belongs here. */ },
        stop: () => { /* Signal owned workers to stop promptly. */ }
    };
    private run = titan.instance(this, this.bot);
    panels: titan.PanelDef[] = [{
        id: "main", title: "Managed Instance",
        build: panel => {
            this.run.controls(panel);
            const library = this.access.snapshot();
            panel.separatorText("Your publisher library").button("Refresh library", 1)
                .label("Snapshot", this.refreshing ? "Refreshing" : library.freshness);
            if (!library.complete) { panel.wrapped("No verified library is available yet."); return; }
            panel.label("Plugins", String(library.entries.length));
            for (const entry of library.entries.slice(0, 20))
                panel.label(entry.display_name || entry.key,
                    `${entry.entitled ? "Access granted" : "No access"}; instances: ${entry.total_instances ?? "unlimited"}`);
            if (library.entries.length > 20) panel.wrapped("Showing the first 20 plugins.");
        },
        onAction: action => {
            if (this.run.control(action)) return;
            if (action === 1 && !this.refreshing) {
                this.refreshing = true;
                // Read-only: listing the approved publisher/dependency scope consumes no slot.
                this.access.list().finally(() => { this.refreshing = false; });
            }
        }
    }];
}
// Disable/unload cleanup is automatic; enabling consumes no slot.
titan.register(new ManagedInstanceSample());
