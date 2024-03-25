package com.example;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Connect extends Remote {
    private static final Logger logger = LoggerFactory.getLogger(Connect.class);
    private static volatile int clientId;


    public static void prod(String botName) throws IOException {
        JSONObject configJson = loadConfigFromJson();
        assert configJson != null;
        String address = configJson.getJSONObject("gib").getString("address");
        int querryPort = configJson.getJSONObject("gib").getInt("QueryPort");

        config.setHost(address);
        config.setQueryPort(querryPort);
        config.setFloodRate(TS3Query.FloodRate.DEFAULT);
        config.setEnableCommunicationsLogging(true);
        config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
        config.setConnectionHandler(new ConnectionHandler() {
            @Override
            public void onConnect(TS3Api api) {
                try {
                    stuffThatNeedsToRunEveryTimeTheQueryConnects (api);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onDisconnect(TS3Query ts3Query) {

            }
        });

        query = new TS3Query(config);
        query.connect();
    }
    private static void stuffThatNeedsToRunEveryTimeTheQueryConnects (TS3Api api) throws IOException {
        JSONObject configJson = loadConfigFromJson();
        assert configJson != null;
        String username = configJson.getJSONObject("gib").getJSONObject("Login").getString("Username");
        String password = configJson.getJSONObject("gib").getJSONObject("Login").getString("Password");
        int virtualServer = configJson.getJSONObject("gib").getInt("ForNetPlayerPort");


        // Logging in, selecting the virtual server, selecting a channel
        // and setting a nickname needs to be done every time we reconnect
        api.login(username, password);
        api.selectVirtualServerByPort(virtualServer);
        api.setNickname("Clanbot");

        // What events we listen to also resets
        api.registerEvent(TS3EventType.TEXT_CHANNEL, 0);
        api.registerEvent(TS3EventType.TEXT_PRIVATE);

        // Out clientID changes every time we connect and we need it
        // for our event listener, so we need to store the ID in a field
        clientId = api.whoAmI().getId();

        MessageToAllClients("Bot is online", api);
        logger.info("Bot connected");

    }
    private static JSONObject loadConfigFromJson() {
        try {
            // Lade den Inhalt der JSON-Datei
            String fileContent = new String(Files.readAllBytes(Paths.get(Main.FILE_PATH)));


            // Erstelle ein JSON-Objekt aus dem Inhalt
            return new JSONObject(fileContent);
        } catch (IOException e) {
            e.printStackTrace(); // oder andere geeignete Behandlung
            return null; // oder einen Standardwert zurückgeben
        }
    }

}
