package com.example;

import java.io.IOException;

public class Main extends Remote{

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

