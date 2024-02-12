package com.example;

import java.io.IOException;

public class Main extends Remote{
    private static int SUPP_GROUP_ID = 36;
    private static int NEW_USER_GROUP_ID = 37;

    public static void main(String[] args) throws IOException, InterruptedException {

        Connect.local("Test");
        api = query.getApi();

        Monitoring.baseMonitoring();
        Monitoring.handleMessages();

        Mover.afkMover(false, null, 1, false,"╚Irgendwann wieder da");
        Mover.afkMover(true, "Willkommen", 1, false, "╚Irgendwann wieder da");

        ChatBot.handleMessages();

        ClientConnect.HandleClientConnect();






    }
}

