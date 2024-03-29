package com.gib;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class Monitoring extends Remote{
    static final int TIMER_DELAY_MS = 1000;
    static final int TIMER_PERIOD_MS = 30 * 60 * 1000;
    static Logger logger = LoggerFactory.getLogger(Monitoring.class);
    public static void handleMessages() {

        // Get our own client ID by running the "whoami" command
        final int clientId = Main.api.whoAmI().getId();

        api.registerEvent(TS3EventType.TEXT_PRIVATE);
        // Register the event listener
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onTextMessage(TextMessageEvent e) {
                // Only react to channel messages not sent by the query itself
                if (e.getTargetMode() == TextMessageTargetMode.CLIENT && e.getInvokerId() != clientId) {
                    String message = e.getMessage().toLowerCase();
                    if (message.equals("!ping")) {
                        // Answer "!ping" with "pong"
                        api.sendPrivateMessage(e.getInvokerId(), "pong");
                    }
                }
            }
        });
    }
    public static void baseMonitoring () {
        // basic monitroing to control, if the bot is online
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (query.isConnected()) {
                    logger.info("Bot is online");
                } else {
                    logger.info("Bot is offline");
                }
            }
        }, TIMER_DELAY_MS, TIMER_PERIOD_MS);
    }
}
