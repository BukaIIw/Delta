package hydrogen.core;


public class Processor {
    private final a a;
    private final b b;

    public enum a {
        BIT_32("32-bit"),
        BIT_64("64-bit"),
        UNKNOWN("Unknown");

        private final String d;

        a(final String label) {
            this.d = label;
        }

        public String a() {
            return this.d;
        }
    }

    public enum b {
        AARCH_64("AArch64"),
        X86("x86"),
        IA_64("IA-64"),
        PPC("PPC"),
        RISC_V("RISC-V"),
        UNKNOWN("Unknown");

        private final String g;

        b(final String label) {
            this.g = label;
        }

        public String a() {
            return this.g;
        }
    }

    public Processor(a arch, b type) {
        this.a = arch;
        this.b = type;
    }

    public a a() {
        return this.a;
    }

    public b b() {
        return this.b;
    }

    public boolean c() {
        return a.BIT_32 == this.a;
    }

    public boolean d() {
        return a.BIT_64 == this.a;
    }

    public boolean e() {
        return b.AARCH_64 == this.b;
    }

    public boolean f() {
        return b.IA_64 == this.b;
    }

    public boolean g() {
        return b.PPC == this.b;
    }

    public boolean h() {
        return b.RISC_V == this.b;
    }

    public boolean i() {
        return b.X86 == this.b;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.b.a()).append(' ').append(this.a.a());
        return builder.toString();
    }
}
