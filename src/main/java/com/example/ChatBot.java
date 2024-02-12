package com.example;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import java.io.*;

public class ChatBot extends Remote {
    static Logger logger = LoggerFactory.getLogger(ChatBot.class);
    private static boolean RecognizedCommand = true;
    private static final String FILE_PATH = "C:\\Users\\dbaum\\Documents\\Teamspeak\\GIB\\config.json";

    public static void handleMessages() {
        // Get our own client ID by running the "whoami" command
        final int clientId = api.whoAmI().getId();

        api.registerEvent(TS3EventType.TEXT_PRIVATE);
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onTextMessage(TextMessageEvent e) {
                if (e.getTargetMode() == TextMessageTargetMode.CLIENT && e.getInvokerId() != clientId) {
                    String command = e.getMessage().toLowerCase();

                    //query change channel if is not in the same as the invoker
                    if (api.getClientInfo(clientId).getChannelId() != api.getClientInfo(e.getInvokerId()).getChannelId()) {
                        api.moveQuery(api.getClientInfo(e.getInvokerId()).getChannelId());
                    }

                    //set RecognizedCommand true before each passage of the Switches
                    RecognizedCommand = true;

                    switch (command) {
                        case "!kevin":
                            printKevin();
                            break;
                        case "!steven":
                            printSteven();
                            break;
                        case "!stefan":
                            Main.api.sendChannelMessage("Bin mal essen!");
                            break;
                        case "!help":
                            printHelp(e.getInvokerId());
                            break;
                        case "!chief":
                            Main.api.sendChannelMessage("Micha sagt: Wir haben CChieftain noch zuhause!!");
                            break;
                        case "!danny":
                            Main.api.sendChannelMessage("Dannnnnnny!!");
                            break;
                        case "!karsten":
                            Main.api.sendChannelMessage("Kaaaarsten!!");
                            break;
                        case "!jonas":
                            Main.api.sendChannelMessage("WAAAS!!");
                            break;
                        case "!changelog":
                            printChangeLog(e.getInvokerId());
                            break;
                        case "!online":
                            printOnline(e.getInvokerId());
                            break;
                        case "!supp":
                            handleSupp();
                            break;
                        default:
                            api.sendPrivateMessage(e.getInvokerId(), "Falscher Befehl! Gebe !help ein, um alle Befehle einzusehen");
                            RecognizedCommand = false;
                    }

                    if (RecognizedCommand) {
                        chatBotLogger(e.getInvokerId(), command);
                    } else {
                        chatBotLogger(e.getInvokerId(), "Falscher Befehl");
                    }
                }
            }

            private void printHelp(int id) {
                api.sendPrivateMessage(id,
                        """
                        Überischt aller Befehle:
                        !help --> gibt eine Übersicht aller Befehle
                        !changelog --> übersicht, was im letzten update geändert wurde
                        !online --> zeigt wer alles online ist und in was für einem Raum sich dieser befindet
                        !kevin | !steven | !stefan | !chief | !karsten | !jonas --> nutzen auf eigene Gefahr
                        """
                );
            }

            private void printChangeLog(int id) {
                api.sendPrivateMessage(id,
                        """
                        Version 2.2
                        rebuild support
                        added config file for counter
                        """
                );
            }

            private void printKevin() {
                try {
                    JSONObject jsonObject = readJSONObjectFromFile(FILE_PATH);
                    assert jsonObject != null;
                    int counterKevin = jsonObject.getJSONObject("counter").getInt("kevin");
                    jsonObject.getJSONObject("counter").put("kevin", counterKevin + 1);
                    writeJSONObjectToFile(jsonObject, FILE_PATH);

                    Main.api.sendChannelMessage(
                            "Kevin! Brünette mit fetten Hupen ist für dich unterwegs\n"
                                    + "Kevin war wieder rallig! Zum " + (counterKevin + 1) + ". mal kam heute eine Nutte vorbei!"
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void printSteven() {
                try {
                    JSONObject jsonObject = readJSONObjectFromFile(FILE_PATH);
                    assert jsonObject != null;
                    int counterSteven = jsonObject.getJSONObject("counter").getInt("steven");
                    jsonObject.getJSONObject("counter").put("steven", counterSteven + 1);
                    writeJSONObjectToFile(jsonObject, FILE_PATH);

                    Main.api.sendChannelMessage(
                            "Abgelehnt!!\n" + "Du bist der " + (counterSteven + 1) + ". der fragt..."
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void printOnline(int id) {
                for (Client c : Main.api.getClients()) {
                    if (!c.isServerQueryClient()) {
                        Main.api.sendPrivateMessage(id,
                                "User " + c.getNickname() + " is in channel "
                                        + Main.getChannelNameById(c.getChannelId()));
                    }
                }
            }

            private void chatBotLogger(int invokerID, String command) {
                logger.info("ChatBot: User " + Main.api.getClientInfo(invokerID).getNickname() + " activated " + command);
            }

            private void handleSupp() {
                // Implement your support handling logic here
            }
        });
    }

    private static JSONObject readJSONObjectFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            // Wenn die Datei nicht existiert, erstellen wir ein neues JSON-Objekt mit Standardwerten
            JSONObject defaultJSONObject = new JSONObject();
            JSONObject counterJSONObject = new JSONObject();
            counterJSONObject.put("kevin", 0);
            counterJSONObject.put("steven", 0);
            defaultJSONObject.put("counter", counterJSONObject);

            // Schreiben des Standard-JSON-Objekts in die Datei
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                fileWriter.write(defaultJSONObject.toString());
            }

            // Rückgabe des Standard-JSON-Objekts
            return defaultJSONObject;
        } else {
            // Wenn die Datei existiert, lesen wir das JSON-Objekt aus der Datei und geben es zurück
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return new JSONObject(stringBuilder.toString());
            }
        }
    }

    private static void writeJSONObjectToFile(JSONObject jsonObject, String filePath) throws IOException {
        // Schreiben des JSON-Objekts in die Datei
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonObject.toString());
        }
    }
}
