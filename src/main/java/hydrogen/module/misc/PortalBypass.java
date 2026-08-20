package hydrogen.module.misc;

import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.PortalEvent;

@ModuleRegister(a = "Portal Bypass", b = "Позволяет открывать окна, находясь в портале", c = Category.Misc)
public class PortalBypass extends Module {
    @EventTarget
    public void a(PortalEvent event) {
        event.b(false);
    }
}
