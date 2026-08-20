package platform.inject.mixin;


import hydrogen.command.CommandProcessor;
import hydrogen.core.HydrogenClient;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ChatInputSuggestor.class})
public abstract class ChatInputSuggestorMixin {

    @Shadow
    @Final
    TextFieldWidget textField;

    @Shadow
    boolean completingSuggestions;

    @Shadow
    private ParseResults<CommandSource> parse;

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow
    private ChatInputSuggestor.SuggestionWindow window;

    @Shadow
    protected abstract void showCommandSuggestions();

    @WrapMethod(method = {"refresh"})
    private void refresh(Operation<Void> original) {
        try {
            original.call(new Object[0]);
        } catch (Throwable th) {
        }
    }

    @Inject(method = {"refresh"}, at = {@At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false)}, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void onRefresh(CallbackInfo callbackInfo, @Local StringReader reader) {
        CommandProcessor commandProcessor = HydrogenClient.h().d().u();
        String prefix = commandProcessor.i();
        if (reader.canRead(prefix.length()) && reader.getString().startsWith(prefix, reader.getCursor())) {
            String text = this.textField.getText();
            int cursor = this.textField.getCursor();
            StringReader reading = new StringReader(text);
            reading.setCursor(prefix.length());
            this.parse = commandProcessor.a().parse(reading, commandProcessor.h());
            if (cursor >= prefix.length() && (this.window == null || !this.completingSuggestions)) {
                this.pendingSuggestions = commandProcessor.a().getCompletionSuggestions(this.parse, cursor);
                this.pendingSuggestions.thenRun(() -> {
                    if (this.pendingSuggestions.isDone()) {
                        showCommandSuggestions();
                    }
                });
            }
            callbackInfo.cancel();
        }
    }
}
