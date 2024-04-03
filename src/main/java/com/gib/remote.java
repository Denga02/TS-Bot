package com.gib;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

abstract class Remote {
    static Logger logger = LoggerFactory.getLogger(Remote.class);
    final public static TS3Config config = new TS3Config();
    public static TS3Query query;
    public static TS3Api api;

    //method to get channel name through channel ID
    public static String getChannelNameById(int channelId) {
        List<Channel> channels = api.getChannels();
        for (Channel channel : channels) {
            if (channel.getId() == channelId) {
                return channel.getName();
            }
        }
        //return null if  don´t find channel with this ID
        return null;
    }

    public static void MessageToAllClients(String message, TS3Api api) {
        //loop through all clients except the query
        for (Client c : api.getClients()) {
            if(!c.isServerQueryClient())
            {
                api.sendPrivateMessage(c.getId(),message);
                logger.info("send Private Message" + message + " to all Clients");
            }
        }
    }

    public static boolean CheckClientsInGroup(int groupId, TS3Api api) {

        for (Client c : api.getClients()) {
            if (c.isInServerGroup(groupId)) {
                return true;
            }
        }
        return false;
    }
    public static List<Client> getClientsFromSpecificGroup(int groupId, TS3Api api) {
        List<Client> list = new ArrayList<>();

        for (Client c : api.getClients()) {
            if (c.isInServerGroup(groupId)) {
                list.add(c);
            }
        }
        return list;
    }
    public static boolean CheckUserIsinChannel(int channelId, TS3Api api) {
        for ( Client c : api.getClients()) {
            if(c.getChannelId() == channelId) {
                return true;
            }
        }
        return false;
    }

    public static void PokeClientsInSpecifiedChannelGroup(int groupId, String message, TS3Api api) {
        for (Client c : getClientsFromSpecificGroup(groupId, api)) {
            api.pokeClient(c.getId(), message);
            logger.info("poked Clients in Group" + groupId + "with message " + message);
        }
    }
}
