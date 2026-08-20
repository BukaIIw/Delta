package hydrogen.handler;

import hydrogen.core.NativeMethodLookup;
import hydrogen.lib.log4j.LoggerFactory;

import hydrogen.config.BaseProcessor;
import hydrogen.handler.ANFindHandler;
import hydrogen.handler.InteractHandler;
import hydrogen.handler.InventoryHandler;
import hydrogen.handler.MainHandler;
import hydrogen.handler.PvEHandler;
import hydrogen.handler.StopHandler;
import hydrogen.handler.TPSHandler;
import hydrogen.handler.UseableHandler;

import hydrogen.module.misc.AFKHandler;
import hydrogen.module.combat.AimHandler;
import hydrogen.module.combat.AuraHandler;
import hydrogen.network.DistributionHandler;
import hydrogen.lib.log4j.Logger_2;
import hydrogen.lib.jsoup.ParserHandler;
import hydrogen.api.Compile;
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
