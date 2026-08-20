package hydrogen.module.combat;

import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.AttackEvent;

@ModuleRegister(a = "No Friend Damage", b = "Не позволяет наносить урон вашим друзьям", c = Category.Combat)
public class NoFriendDamage extends Module {
    @EventTarget
    public void a(AttackEvent event) {
        event.a(HydrogenClient.h().d().e().d(event.b().getName().getString()));
    }
}
