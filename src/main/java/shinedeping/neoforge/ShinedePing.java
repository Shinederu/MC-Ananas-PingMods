package shinedeping.neoforge;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ShinedePing.MODID)
public class ShinedePing {
    public static final String MODID = "shinedeping";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ShinedePing(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }
        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());
        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[ShinedePing] Le serveur a démarré !");
        envoyerPing();
    }

    private void envoyerPing() {
        new Thread(() -> {
            try {
                String urlStr = "https://api.play.shinederu.lol/server/server-off";
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int code = connection.getResponseCode();
                InputStreamReader streamReader;

                if (code >= 200 && code < 300) {
                    streamReader = new InputStreamReader(connection.getInputStream());
                } else {
                    streamReader = new InputStreamReader(connection.getErrorStream());
                }

                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }

                reader.close();

                String response = responseBuilder.toString();
                LOGGER.info("[ShinederuPingMod] Réponse brute : " + response);

                if (response.trim().startsWith("{") && response.trim().endsWith("}")) {
                    try {
                        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
                        String message = json.has("message") ? json.get("message").getAsString() : "Aucun message.";

                        if (code == 200) {
                            LOGGER.info("[ShinederuPingMod] ✅ Code HTTP 200 - Message du serveur : " + message);
                        } else {
                            LOGGER.warn("[ShinederuPingMod] ⚠️ Code HTTP " + code + " - Erreur retournée : " + message);
                        }
                    } catch (Exception e) {
                        LOGGER.error("[ShinederuPingMod] ❌ Erreur lors de l’analyse du JSON : " + e.getMessage());
                    }
                } else {
                    LOGGER.warn("[ShinederuPingMod] ❗ Réponse non-JSON ou inattendue : " + response);
                }

            } catch (SocketTimeoutException e) {
                LOGGER.error("[ShinederuPingMod] ❌ Timeout lors de la tentative de ping !");
            } catch (IOException e) {
                LOGGER.error("[ShinederuPingMod] ❌ Erreur réseau : " + e.getMessage());
            } catch (Exception e) {
                LOGGER.error("[ShinederuPingMod] ❌ Erreur inconnue : ", e);
            }
        }).start();
    }
}
