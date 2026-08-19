package aethereal.handler;

import aethereal.core.NativeMethodLookup;
import aethereal.lib.log4j.LoggerFactory;

import aethereal.config.BaseProcessor;
import aethereal.handler.ANFindHandler;
import aethereal.handler.InteractHandler;
import aethereal.handler.InventoryHandler;
import aethereal.handler.MainHandler;
import aethereal.handler.PvEHandler;
import aethereal.handler.StopHandler;
import aethereal.handler.TPSHandler;
import aethereal.handler.UseableHandler;

import aethereal.module.misc.AFKHandler;
import aethereal.module.combat.AimHandler;
import aethereal.module.combat.AuraHandler;
import aethereal.network.DistributionHandler;
import aethereal.lib.log4j.Logger_2;
import aethereal.lib.jsoup.ParserHandler;
import aethereal.api.Compile;
import lombok.Generated;

public class HandlerProcessor extends BaseProcessor {

    @Generated
    private static final Logger_2 b;
    private final InventoryHandler c = new InventoryHandler();
    private final UseableHandler d = new UseableHandler();
    private final StopHandler e = new StopHandler();
    private final AuraHandler f = new AuraHandler();
    private final AimHandler g = new AimHandler();
    private final ANFindHandler h = new ANFindHandler();
    private final AFKHandler i = new AFKHandler();
    private final MainHandler j = new MainHandler();
    private final PvEHandler k = new PvEHandler();
    private final TPSHandler l = new TPSHandler();
    private final InteractHandler m = new InteractHandler();
    private final ParserHandler n = new ParserHandler();
    private final DistributionHandler o = new DistributionHandler();

    @Override
    @Compile
    public void setup() {
    }

    static {
        NativeMethodLookup.lookup(HandlerProcessor.class, 31);
        b = LoggerFactory.a((Class<?>) HandlerProcessor.class);
    }

    @Generated
    public InventoryHandler a() {
        return this.c;
    }

    @Generated
    public UseableHandler b() {
        return this.d;
    }

    @Generated
    public StopHandler c() {
        return this.e;
    }

    @Generated
    public AuraHandler d() {
        return this.f;
    }

    @Generated
    public AimHandler e() {
        return this.g;
    }

    @Generated
    public ANFindHandler f() {
        return this.h;
    }

    @Generated
    public AFKHandler g() {
        return this.i;
    }

    @Generated
    public MainHandler h() {
        return this.j;
    }

    @Generated
    public PvEHandler i() {
        return this.k;
    }

    @Generated
    public TPSHandler j() {
        return this.l;
    }

    @Generated
    public InteractHandler k() {
        return this.m;
    }

    @Generated
    public ParserHandler l() {
        return this.n;
    }

    @Generated
    public DistributionHandler m() {
        return this.o;
    }

    @Override
    public void unSetup() {
    }
}
