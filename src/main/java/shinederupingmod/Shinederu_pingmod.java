package shinederupingmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;


public class Shinederu_pingmod implements ModInitializer {
	public static final String MOD_ID = "shinederu_pingmod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		System.out.println("[ShinederuPingMod] Le mod est chargé");

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			System.out.println("[ShinederuPingMod] Le serveur a démarré !");
			envoyerPing();
		});
	}

	private void envoyerPing() {
		new Thread(() -> {
			try {
				String urlStr = "http://172.16.20.11:85/server/server-off";
				URL url = new URL(urlStr);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(5000); // 5 sec timeout
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