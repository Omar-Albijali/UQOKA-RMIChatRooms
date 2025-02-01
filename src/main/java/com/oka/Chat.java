package com.oka;

import java.util.ArrayList;
import java.util.Collection;

public class Chat extends ArrayList<Chat.Message> {

    public static class Message{
        long date;
        String name;
        String message;

        public Message(long date, String name, String message) {
            this.date = date;
            this.name = name;
            this.message = message;
        }

        public String getName() {
            return name;
        }


        public String getMessage() {
            return message;
        }


        public long getDate() {
            return date;
        }
    }

}
