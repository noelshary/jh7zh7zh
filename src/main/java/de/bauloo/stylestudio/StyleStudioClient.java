package de.bauloo.stylestudio;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class StyleStudioClient implements ClientModInitializer {
    public static final String MOD_ID = "stylestudio";
    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        StyleConfig.load();
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.stylestudio.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.create(Identifier.of(MOD_ID, "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new StyleStudioScreen());
                }
            }
        });
    }
}
