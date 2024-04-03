package com.gib;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class Support extends Remote {
    static Logger logger = LoggerFactory.getLogger(Support.class);

    private static final String NO_ACTORS_ARE_ONLINE = "Zurzeit ist niemand vom Support online";
    private static final String ACTORS_ARE_ONLINE = "Es wurden Leute aus Support benachrichtigt";
    private static final String ACTOR_MESSAGE = "Es braucht jemand Support";
    private static final int PERIOD_TIME =   10 * 60 * 1000;
    private static final int DELAY_TIME = 1 * 60 * 1000;

    public static void HandleNewUser(ClientJoinEvent e) {
        JSONObject configJson = loadConfigFromJson();
        assert configJson != null;

        int NewChannelGroupId = configJson.getJSONObject("ServerGroupID").getInt("New");
        int SupportChannelGroupId = configJson.getJSONObject("ServerGroupID").getInt("Support");
        Channel targetChannel = api.getChannelByNameExact(configJson.getString("SupportChannel"), false);

        int clientID = e.getClientId();
        logger.info("return client id: " + clientID);
        if (clientID > 0) {
            ClientInfo client = api.getClientInfo(e.getClientId());

            logger.info("return clientinfo: " + client);
            if (client != null ) {
                MoveInSupportRoom(NewChannelGroupId, client, targetChannel);
                SupportRoutine(NewChannelGroupId, SupportChannelGroupId, targetChannel);
            } else {
                logger.info("Error with client");
            }
        } else {
            logger.info("Error with clientId");
        }

    }

    //targetChannelGroupId --> Client, who gets support
    //actorChannelGroupId --> Client, who should react by support
    private static void SupportRoutine(int targetGroupId, int actorGroupID, Channel targetChannel) {;
        if (CheckUserIsinChannel(targetChannel.getId(), api)) {

            if (CheckClientsInGroup(actorGroupID, api)) {

                PokeClientsInSpecifiedChannelGroup(targetGroupId, ACTORS_ARE_ONLINE, api);
                PokeClientsInSpecifiedChannelGroup(actorGroupID, ACTOR_MESSAGE, api);

            } else {

                PokeClientsInSpecifiedChannelGroup(targetGroupId, NO_ACTORS_ARE_ONLINE, api);
            }
        }

    }

    private static void MoveInSupportRoom (int ServerGroup, Client client, Channel channel) {
        if (client.isInServerGroup(ServerGroup)) {
            api.moveClient(client.getId(), channel.getId());
            logger.info("moved" + client.getNickname() + "to room " + channel.getName());
        }
    }
    public static void HandleSupport() {
        JSONObject configJson = loadConfigFromJson();
        assert configJson != null;

        int NewChannelGroupId = configJson.getJSONObject("ServerGroupID").getInt("New");
        int SupportChannelGroupId = configJson.getJSONObject("ServerGroupID").getInt("Support");
        Channel targetChannel = api.getChannelByNameExact(configJson.getString("SupportChannel"), false);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SupportRoutine(NewChannelGroupId, SupportChannelGroupId, targetChannel);
            }
        }, DELAY_TIME, PERIOD_TIME);
    }

    private static JSONObject loadConfigFromJson() {
        try {
            // load content of the Json-File
            String fileContent = new String(Files.readAllBytes(Paths.get(Main.FILE_PATH)));

            // create Json-Object of the content
            return new JSONObject(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
