package hydrogen.module.movement;

import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.Interface;
import hydrogen.core.ModuleRegister;

@ModuleRegister(a = "Water Jump", b = "Подбрасывает вас вверх при попадании на сыпучий блок под водой", c = Category.Movement)
public class WaterJump extends Module implements Interface {
}
