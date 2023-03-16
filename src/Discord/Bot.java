package Discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Bot implements EventListener {

    private JDA jda;

    public Bot() throws InterruptedException {
        jda = JDABuilder.createDefault("Nzg5NDg4NTY4NDgwOTU2NDE2.GomVRp.04Ml0GBzPw-Byemb8C0buyo_P6dop8dexKnMW0")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new ReadyEvent(jda))
                .build();

        jda.awaitReady();
    }

    @Override
    public void onEvent(GenericEvent event)
    {
        if (event instanceof ReadyEvent)
            System.out.println("API is ready!");
    }



}
