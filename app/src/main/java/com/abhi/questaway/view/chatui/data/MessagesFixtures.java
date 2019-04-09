package com.abhi.questaway.view.chatui.data;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

public class MessagesFixtures {


    public static Message getTextMessage(String text, boolean isBot) {
        return new Message(getRandomId(), getUser(isBot), text);
    }

    public static Message getImageMessage() {
        Message message = new Message(getRandomId(), getUser(false), null);
        message.setImage(new Message.Image(getRandomImage()));
        return message;
    }
    static SecureRandom rnd = new SecureRandom();


    static String getRandomImage() {
        return images.get(rnd.nextInt(images.size()));
    }
    static final ArrayList<String> images = new ArrayList<String>() {
        {
            add("https://habrastorage.org/getpro/habr/post_images/e4b/067/b17/e4b067b17a3e414083f7420351db272b.jpg");
            add("https://cdn.pixabay.com/photo/2017/12/25/17/48/waters-3038803_1280.jpg");
        }
    };

    private static User getUser(boolean isBot) {
        return new User(
                isBot ? "0": "1",
                isBot ? "Bot": "Me",
                isBot ? avatars.get(0): avatars.get(1),
                true
        );

    }

    static ArrayList<String> avatars = new ArrayList<String>() {
        {
            add("http://i.imgur.com/pv1tBmT.png");
            add("http://i.imgur.com/R3Jm1CL.png");
        }
    };



    static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }
}
