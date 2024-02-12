package com.example;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

public class Support extends Remote {
    private static final String FILE_PATH = "C:\\Users\\dbaum\\Documents\\Teamspeak\\GIB\\config.json";
    static Logger logger = LoggerFactory.getLogger(Support.class);

    private static final String NO_ACTORS_ARE_ONLINE = "Zurzeit ist niemand vom Support online";
    private static final String ACTORS_ARE_ONLINE = "Es wurden Leute aus Support benachrichtigt";
    private static final String ACTOR_MESSAGE = "Es braucht jemand Support";
    private static final int PERIOD_TIME =   10 * 60 * 1000;
    private static final int DELAY_TIME = 0;

    public static void HandleNewUser() {
        JSONObject configJson = loadConfigFromJson();
        assert configJson != null;

        int NewChannelGroupId = configJson.getJSONObject("ServerGroupID").getInt("New");
        int SupportChannelGroupId = configJson.getJSONObject("ServerGroupID").getInt("Support");
        Channel targetChannel = api.getChannelByNameExact(configJson.getString("SupportChannel"), false);

        api.registerEvent(TS3EventType.SERVER);
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientJoin(ClientJoinEvent e) {
                Client client = api.getClientInfo(e.getClientId());
                supportRoutine(NewChannelGroupId, SupportChannelGroupId, client, targetChannel);
            }
        });

    }

    //targetChannelGroupId --> Client, who gets support
    //actorChannelGroupId --> Client, who should react by support
    private static void supportRoutine (int targetChannelGroupId, int actorChannelGroupId, Client client, Channel targetChannel) {

        MoveInSupportRoom(targetChannelGroupId, client, targetChannel);
        HandleSupport(targetChannelGroupId, actorChannelGroupId, targetChannel);
    }
    private static void MoveInSupportRoom (int ServerGroup, Client client, Channel channel) {
        if (client.isInServerGroup(ServerGroup)) {
            api.moveClient(client.getId(), channel.getId());
            logger.info(STR."moved \{client.getNickname()} to Room \{channel.getName()}");
        }
    }
    private static void HandleSupport( int targetGroupId, int actorGroupID, Channel targetChannel) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (CheckUserIsinChannel(targetChannel.getId(), api)) {

                    if (CheckClientsInGroup(actorGroupID, api)) {

                        PokeClientsInSpecifiedChannelGroup(targetGroupId, ACTORS_ARE_ONLINE, api);
                        PokeClientsInSpecifiedChannelGroup(actorGroupID, ACTOR_MESSAGE, api);

                    } else {

                        PokeClientsInSpecifiedChannelGroup(targetGroupId, NO_ACTORS_ARE_ONLINE, api);
                    }
                }
            }
        }, DELAY_TIME, PERIOD_TIME);
    }

    private static JSONObject loadConfigFromJson() {
        try {
            // load content of the Json-File
            String fileContent = new String(Files.readAllBytes(Paths.get(FILE_PATH)));

            // create Json-Object of the content
            return new JSONObject(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
