package com.github.gnottero;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.script.*;
import carpet.script.bundled.BundledModule;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

public class CarpetMain implements CarpetExtension {

public static final String ModId = TwitchProject.Modid;

    @Override
        public void onGameStarted() {
        CarpetScriptServer.registerBuiltInScript(TwitchPrjDefaultScript("master_script", false));
    }

    public static void loadScripts(MinecraftServer server) {
        CarpetServer.scriptServer.addScriptHost(server.getCommandSource(), "master_script", null, true, true, false, null);
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        loadScripts(server);
    }

    @Override
    public void onReload(MinecraftServer server) {
        loadScripts(server);
    }

    private static BundledModule TwitchPrjDefaultScript(String scriptName, boolean isLibrary) {
        BundledModule module = new BundledModule(scriptName.toLowerCase(Locale.ROOT), null, false);
        try {
            module = new BundledModule(scriptName.toLowerCase(Locale.ROOT),
                IOUtils.toString(
                Objects.requireNonNull(BundledModule.class.getClassLoader().getResourceAsStream("assets/" + ModId + "/scripts/" + scriptName + (isLibrary ? ".scl" : ".sc"))),
                StandardCharsets.UTF_8
                ), isLibrary
            );
        } catch (NullPointerException | IOException ignored) {
        }
        return module;
    }
}