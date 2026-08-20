package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;
import net.minecraft.text.Text;

public class ScoreboardEvent extends Event implements IEvent {
    private Text a;

    @Generated
    public void a(Text title) {
        this.a = title;
    }

    @Generated
    public Text b() {
        return this.a;
    }

    public ScoreboardEvent(Text title) {
        this.a = title;
    }
}
