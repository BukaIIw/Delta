package hydrogen.core;

import hydrogen.core.NativeMethodLookup;
import hydrogen.lib.websocket.ServerHandshake;
import hydrogen.lib.websocket.WebSocketClient;

import hydrogen.core.HydrogenClient;
import hydrogen.lib.log4j.LoggerFactory;

import hydrogen.core.Packet;

import hydrogen.lib.log4j.Logger_2;
import hydrogen.network.PacketSecurity;
import hydrogen.api.Compile;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.Generated;

@Compile
public class Client extends WebSocketClient {

    @Generated
    private static Logger_2 b;
    private final ScheduledExecutorService c;
    private final List<Packet> d;
    private final PacketSecurity e;

    private SSLSocketFactory C() throws Exception {
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                if (chain == null || chain.length == 0) {
                    throw new a();
                }
                try {
                    String pin = java.util.Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(chain[0].getPublicKey().getEncoded()));
                    boolean matched = Set.of("tjzKnQqXiG8qfKkHSOtckEHsKNtsONSU9NN+d8vZ1XQ=").stream().anyMatch(expected -> {
                        return MessageDigest.isEqual(pin.getBytes(StandardCharsets.UTF_8), expected.getBytes(StandardCharsets.UTF_8));
                    });
                    if (!matched) {
                        throw new a();
                    }
                } catch (a e) {
                    throw e;
                } catch (Exception e2) {
                    throw new a();
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext sSLContext = SSLContext.getInstance("TLS");
        sSLContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
        return sSLContext.getSocketFactory();
    }

    @Override
    public void a(ServerHandshake handshake) {
    }

    @Override
    public void c(String message) {
    }

    @Override
    public void b(int code, String reason, boolean remote) {
    }

    @Override
    public void a(Exception ex) {
    }

    public void a(boolean change, String packetId, Object... keyValues) {
    }

    public void A() {
    }

    @Generated
    public PacketSecurity B() {
        return this.e;
    }

    public static boolean a(String packetId, Packet p) {
        if (p == null) {
            throw new NullPointerException();
        }
        String strB = p.b();
        if (strB == null) {
            throw new NullPointerException();
        }
        return strB.equals(packetId);
    }

    public void D() {
    }

    static {
        NativeMethodLookup.lookup(Client.class, 2);
        jc$clinit$();
    }

    private static void jc$clinit$() {
        b = LoggerFactory.a((Class<?>) Client.class);
    }

    public boolean g() {
        return isOpen();
    }

    public Client(boolean dev) {
        super(URI.create(dev ? "ws://localhost:2002/" : "wss://hydrogendlc.xyz/ws/"), (Map<String, String>) Map.of("Sec-WebSocket-Protocol", HydrogenClient.h().g().f() + "-minecraft"));
        this.c = Executors.newSingleThreadScheduledExecutor();
        this.d = new ArrayList();
        this.e = new PacketSecurity();
    }

    static final class a extends RuntimeException {
        a() {
            super(null, null, false, false);
        }
    }
}
