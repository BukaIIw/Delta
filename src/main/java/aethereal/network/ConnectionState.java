package aethereal.network;


import aethereal.discord.DiscordBuild;
import aethereal.discord.FailureInfo;
import aethereal.core.User_2;

public interface ConnectionState {

    public static final class d implements ConnectionState {
}

    public static final class c implements ConnectionState {
}

    public static final class b implements ConnectionState {
        private final User_2 a;
        private final DiscordBuild b;

        public b(User_2 user, DiscordBuild build) {
            this.a = user;
            this.b = build;
        }
public User_2 a() {
            return this.a;
        }

        public DiscordBuild b() {
            return this.b;
        }
    }

    public static final class f implements ConnectionState {
        private final int a;
        private final FailureInfo b;

        public f(int attempt, FailureInfo failure) {
            this.a = attempt;
            this.b = failure;
        }
public int a() {
            return this.a;
        }

        public FailureInfo b() {
            return this.b;
        }
    }

    public static final class e implements ConnectionState {
        private final FailureInfo a;

        public e(FailureInfo failure) {
            this.a = failure;
        }
public FailureInfo a() {
            return this.a;
        }
    }

    public static final class a implements ConnectionState {
}
}
