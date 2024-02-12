package com.example;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnect extends Remote {
    static Logger logger = LoggerFactory.getLogger(ClientConnect.class);
    public static void HandleClientConnect() {
        api.registerEvent(TS3EventType.SERVER);
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientJoin(ClientJoinEvent e) {
                Client client = api.getClientInfo(e.getClientId());

                //send welcome message
                SendMessage(client);

                //Handle new User
                Support.HandleNewUser();
            }
        });
    }
    private static void SendMessage (Client client) {
        api.sendPrivateMessage(client.getId(), STR."Willkommen [B]\{client.getNickname()}[/B]!");
        logger.info(STR."ClientConncetion: \{client.getNickname()} joined the server");
    }
}
