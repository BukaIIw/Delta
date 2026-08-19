package aethereal.core;


import aethereal.util.ProviderUtil;
import aethereal.api.InternalApi;
import aethereal.lib.log4j.LoggerContextFactory;
import aethereal.lib.log4j.Provider;
import aethereal.lib.log4j.LoggerContextFactory;
import aethereal.lib.log4j.Provider;

import aethereal.lib.log4j.StatusLogger;
import aethereal.lib.log4j.Logger;
import java.net.URL;
import java.security.Permission;
import java.util.Collection;
import java.util.List;
import org.osgi.annotation.bundle.Header;
import org.osgi.annotation.bundle.Headers;
import org.osgi.framework.AdaptPermission;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

@Headers({@Header(name = "Bundle-Activator", value = "${@class}"), @Header(name = "Bundle-ActivationPolicy", value = "lazy")})
@InternalApi
public class Activator implements BundleActivator, SynchronousBundleListener {
    private static final SecurityManager a = System.getSecurityManager();
    private static final Logger b = StatusLogger.x();
    private boolean c;

    private static void a(final Permission permission) {
        if (a != null) {
            a.checkPermission(permission);
        }
    }

    private void a(final Bundle bundle) {
        if (bundle.getState() == 1) {
            return;
        }
        try {
            a((Permission) new AdminPermission(bundle, "resource"));
            a((Permission) new AdaptPermission(BundleWiring.class.getName(), bundle, "adapt"));
            BundleContext bundleContext = bundle.getBundleContext();
            if (bundleContext == null) {
                b.a("Bundle {} has no context (state={}), skipping loading provider", bundle.getSymbolicName(), a(bundle.getState()));
            } else {
                a(bundleContext, (BundleWiring) bundle.adapt(BundleWiring.class));
            }
        } catch (SecurityException e) {
            b.a("Cannot access bundle [{}] contents. Ignoring.", bundle.getSymbolicName(), e);
        } catch (Exception e2) {
            b.f("Problem checking bundle {} for Log4j 2 provider.", bundle.getSymbolicName(), e2);
        }
    }

    private String a(final int state) {
        switch (state) {
            case 1:
                return "UNINSTALLED";
            case 2:
                return "INSTALLED";
            case 4:
                return "RESOLVED";
            case 8:
                return "STARTING";
            case 16:
                return "STOPPING";
            case 32:
                return "ACTIVE";
            default:
                return Integer.toString(state);
        }
    }

    private void a(final BundleContext bundleContext, final BundleWiring bundleWiring) {
        try {
            Collection<ServiceReference<Provider>> serviceReferences = bundleContext.getServiceReferences(Provider.class, "(APIVersion>=2.6.0)");
            Provider maxProvider = null;
            for (ServiceReference<Provider> serviceReference : serviceReferences) {
                Provider provider = (Provider) bundleContext.getService(serviceReference);
                if (maxProvider == null || provider.d().intValue() > maxProvider.d().intValue()) {
                    maxProvider = provider;
                }
            }
            if (maxProvider != null) {
                ProviderUtil.a(maxProvider);
            }
        } catch (InvalidSyntaxException e) {
            b.b("Invalid service filter: (APIVersion>=2.6.0)", e);
        }
        List<URL> urls = bundleWiring.findEntries("META-INF", "log4j-provider.properties", 0);
        for (URL url : urls) {
            ProviderUtil.a(url, bundleWiring.getClassLoader());
        }
    }

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        ProviderUtil.c.lock();
        this.c = true;
        BundleWiring self = (BundleWiring) bundleContext.getBundle().adapt(BundleWiring.class);
        List<BundleWire> required = self.getRequiredWires(LoggerContextFactory.class.getName());
        for (BundleWire wire : required) {
            a(bundleContext, wire.getProviderWiring());
        }
        bundleContext.addBundleListener(this);
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            a(bundle);
        }
        a();
    }

    private void a() {
        if (this.c && !ProviderUtil.b.isEmpty()) {
            ProviderUtil.c.unlock();
            this.c = false;
        }
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        bundleContext.removeBundleListener(this);
        a();
    }

    public void a(final BundleEvent event) {
        bundleChanged(event);
    }

    @Override
    public void bundleChanged(final BundleEvent event) {
        switch (event.getType()) {
            case 2:
                a(event.getBundle());
                a();
                break;
        }
    }
}
