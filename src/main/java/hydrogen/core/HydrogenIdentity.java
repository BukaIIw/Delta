package hydrogen.core;

import hydrogen.discord.NativeUserInfoState;

public final class HydrogenIdentity {
    private HydrogenIdentity() {
    }

    public static String uid() {
        return NativeUserInfoState.uid();
    }

    public static String username() {
        return NativeUserInfoState.username();
    }

    public static String hwid() {
        return NativeUserInfoState.hwid();
    }

    public static String role() {
        return NativeUserInfoState.role();
    }

    public static String expire() {
        return NativeUserInfoState.expire();
    }

    public static String token() {
        return NativeUserInfoState.token();
    }
}
