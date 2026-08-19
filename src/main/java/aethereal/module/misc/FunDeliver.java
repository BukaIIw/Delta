package aethereal.module.misc;

import aethereal.core.Module;

import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.Interface;
import aethereal.core.ModuleRegister;
import aethereal.event.TickEvent;

import aethereal.network.FunPay;
import aethereal.setting.SliderSetting;
import aethereal.setting.StringSetting;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.Generated;

@ModuleRegister(a = "Fun Deliver", b = "Автоматическая выдача валюты по заказам FunPay", c = Category.Misc)
public class FunDeliver extends Module implements Interface {
    private final StringSetting b = (StringSetting) new StringSetting("Укажите ваш Golden-Key", "").a();
    private final SliderSetting c = new SliderSetting("Триггер цены обработки товара", 1.0f, 0.5f, 10.0f, 0.01f, true);
    private final SliderSetting d = new SliderSetting("Продавать при сумме от (кк)", 10.0f, 1.0f, 50.0f, 1.0f, true);
    private ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();
    private FunPay f;

    @Override
    public void b() {
    }

    @EventTarget
    public void a(TickEvent event) {
    }

    private void t() {
    }

    @Generated
    public StringSetting q() {
        return this.b;
    }

    @Generated
    public SliderSetting r() {
        return this.c;
    }

    @Generated
    public SliderSetting s() {
        return this.d;
    }

    public FunDeliver() {
        a(this.b, this.c, this.d);
    }

    @Override
    public void c() {
        super.c();
        this.e.shutdownNow();
    }
}
