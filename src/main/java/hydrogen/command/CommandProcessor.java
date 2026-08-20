package hydrogen.command;

import hydrogen.core.NativeMethodLookup;
import hydrogen.lib.log4j.LoggerFactory;

import hydrogen.command.AHCommand;
import hydrogen.command.BindCommand;
import hydrogen.command.BlockESPCommand;
import hydrogen.command.CCCommand;
import hydrogen.command.ConfigCommand;
import hydrogen.command.FriendCommand;
import hydrogen.command.GPSCommand;
import hydrogen.command.HClipCommand;
import hydrogen.command.LayoutCommand;
import hydrogen.command.MacrosCommand;
import hydrogen.command.RCTCommand;
import hydrogen.command.StaffCommand;
import hydrogen.command.VClipCommand;
import hydrogen.command.WardenCommand;
import hydrogen.command.WayCommand;
import hydrogen.config.BaseProcessor;

import hydrogen.lib.log4j.Logger_2;
import hydrogen.api.Compile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.command.CommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientCommandSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class CommandProcessor extends BaseProcessor {

    @Generated
    private static final Logger_2 b;
    private final List<BaseCommand> d = new ArrayList();
    private final WayCommand e = new WayCommand();
    private final GPSCommand f = new GPSCommand();
    private final LayoutCommand g = new LayoutCommand();
    private final RCTCommand h = new RCTCommand();
    private final BlockESPCommand i = new BlockESPCommand();
    private final String k = ".";
    private final CommandDispatcher<CommandSource> c = new CommandDispatcher<>(new CaseInsensitiveLiteral.a());
    private final ClientCommandSource j = new ClientCommandSource((ClientPlayNetworkHandler) null, MinecraftClient.getInstance());

    @Override
    @Compile
    public void setup() {
        a(this.e, this.f, this.g, this.h, this.i, new AHCommand(), new MacrosCommand(), new FriendCommand(), new StaffCommand(), new WardenCommand(), new ConfigCommand(), new BindCommand(), new VClipCommand(), new HClipCommand(), new CCCommand(), new AiCommand(), new AiTrainCommand(), new AiSelectCommand());
    }

    static {
        NativeMethodLookup.lookup(CommandProcessor.class, 22);
        b = LoggerFactory.a((Class<?>) CommandProcessor.class);
    }

    @Generated
    public CommandDispatcher<CommandSource> a() {
        return this.c;
    }

    @Generated
    public List<BaseCommand> b() {
        return this.d;
    }

    @Generated
    public WayCommand c() {
        return this.e;
    }

    @Generated
    public GPSCommand d() {
        return this.f;
    }

    @Generated
    public LayoutCommand e() {
        return this.g;
    }

    @Generated
    public RCTCommand f() {
        return this.h;
    }

    @Generated
    public BlockESPCommand g() {
        return this.i;
    }

    @Generated
    public ClientCommandSource h() {
        return this.j;
    }

    @Generated
    public String i() {
        Objects.requireNonNull(this);
        return ".";
    }

    @Override
    public void unSetup() {
    }

    public void a(BaseCommand... commands) {
        for (BaseCommand command : commands) {
            this.d.add(command);
            command.a(this.c);
        }
    }

    public void a(String message, CallbackInfo ci) {
        if (message == null || message.isEmpty() || !message.startsWith(i())) {
            return;
        }
        String command = message.substring(i().length()).trim();
        if (!command.isEmpty()) {
            try {
                ParseResults<CommandSource> results = this.c.parse(command, this.j);
                for (ParsedCommandNode<CommandSource> parsed : results.getContext().getNodes()) {
                    if (parsed.getNode() instanceof LiteralCommandNode<?> literal) {
                        int typedLength = parsed.getRange().getLength();
                        if (typedLength != literal.getLiteral().length()) {
                            return;
                        }
                    }
                }
                this.c.execute(results);
                ci.cancel();
            } catch (CommandSyntaxException e) {
                System.out.println("Failure command: " + e.getMessage());
            }
        }
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> a(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}