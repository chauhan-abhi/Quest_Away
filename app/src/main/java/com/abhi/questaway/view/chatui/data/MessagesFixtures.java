package com.abhi.questaway.view.chatui.data;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

public class MessagesFixtures {


    public static Message getTextMessage(String text, boolean isBot) {
        return new Message(getRandomId(), getUser(isBot), text);
    }

    public static Message getImageMessage(String uri) {
        Message message = new Message(getRandomId(), getUser(false), null);
        message.setImage(new Message.Image(uri));
        return message;
    }

    private static User getUser(boolean isBot) {
        return new User(
                isBot ? "0": "1",
                isBot ? "Bot": "Me",
                isBot ? avatars.get(0) : avatars.get(1),
                true
        );

    }

    static ArrayList<String> avatars = new ArrayList<String>() {
        {
            add("https://s3-us-west-2.amazonaws.com/elevateblue/public/bot_haus.png");
            add("https://cdn.dribbble.com/users/295247/screenshots/3268208/meet_cj.png");
        }
    };



    static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }
}
