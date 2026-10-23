package net.titan.sample;

import net.titan.api.panel.Panel;
import net.titan.api.panel.PanelValue;
import net.titan.api.plugins.Plugin;
import net.titan.api.plugins.PluginDescriptor;
import net.titan.api.plugins.SidePanel;
import net.titan.authorization.Instance;
import net.titan.authorization.Entitlements;

/** A local example requires a matching approved marketplace development build to start. */
@PluginDescriptor(id="managed_instance_sample",name="Managed Instance Sample",
    description="Explicit Start/Stop with verified plugin permits",author="Titan",version="0.1.0")
@SidePanel(id="main",title="Managed Instance",icon="awesome:play")
public final class ManagedInstanceSamplePlugin implements Plugin {
    private static final int REFRESH_LIBRARY=1;
    private int ticks;
    private boolean refreshing;
    private final Entitlements access=Entitlements.forPlugin(this);
    private final Instance run=Instance.bind(this,new Instance.Bot(){
        public boolean start(){ticks=0;return true;}
        public void tick(int tick){ticks++; /* Existing bot work belongs here. */}
        public void stop(Instance.StopReason reason){/* Signal owned workers to stop promptly. */}
    });
    @Override public void buildPanel(String panelId,Panel panel){
        run.controls(panel);
        panel.label("Ticks",Integer.toString(ticks));
        Entitlements.Snapshot library=access.snapshot();
        panel.separatorText("Your publisher library")
            .button("Refresh library",REFRESH_LIBRARY)
            .label("Snapshot",refreshing?"Refreshing":library.freshness().name());
        if(!library.complete){panel.wrapped("No verified library is available yet.");return;}
        panel.label("Plugins",Integer.toString(library.entries.size()));
        for(Entitlements.Entry entry:library.entries.subList(0,Math.min(20,library.entries.size())))
            panel.label(entry.displayName.isEmpty()?entry.key:entry.displayName,
                (entry.entitled?"Access granted":"No access")+"; instances: "+
                (entry.totalInstances==null?"unlimited":entry.totalInstances));
        if(library.entries.size()>20)panel.wrapped("Showing the first 20 plugins.");
    }
    @Override public void onPanelAction(String panelId,int actionId,PanelValue value){
        if(run.control(actionId))return;
        if(actionId==REFRESH_LIBRARY&&!refreshing){
            refreshing=true;
            // Read-only: listing the approved publisher/dependency scope consumes no slot.
            access.list().whenComplete((library,error)->refreshing=false);
        }
    }
    // Disable/unload cleanup is automatic. Enabling this plugin never acquires a slot.
}
