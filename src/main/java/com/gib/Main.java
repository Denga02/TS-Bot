package com.gib;

import java.io.IOException;

public class Main extends Remote{

    //public static final String FILE_PATH = "D:\\dbaum\\Documents\\Wot_Clan\\config.json";
    public static final String FILE_PATH = "/opt/ts_bot/json_files/config.json";

    public static void main(String[] args) throws IOException, InterruptedException {

        Connect.prod();
        api = query.getApi();

        ClientConnect.HandleClientConnect();

        Monitoring.baseMonitoring();
        Monitoring.handleMessages();

        Mover.afkMover(false, null, 20, true,"╚Irgendwann wieder da");
        Mover.afkMover(true, "Willkommen", 1, false, "╚Irgendwann wieder da");

        ChatBot.handleMessages();

        Support.HandleSupport();


    }
}

