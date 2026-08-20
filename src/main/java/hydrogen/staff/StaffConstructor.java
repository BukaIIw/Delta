package hydrogen.staff;


import hydrogen.render.AnimationUtil;
import lombok.Generated;

public class StaffConstructor {
    private String a;
    private final AnimationUtil b = new AnimationUtil();

    @Generated
    public void a(String name) {
        this.a = name;
    }

    @Generated
    public StaffConstructor() {
    }

    @Generated
    public String a() {
        return this.a;
    }

    @Generated
    public AnimationUtil b() {
        return this.b;
    }

    public StaffConstructor(String name) {
        this.a = name;
    }
}
