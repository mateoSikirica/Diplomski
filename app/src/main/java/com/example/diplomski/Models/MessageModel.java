package com.example.diplomski.Models;

import android.content.Intent;
import android.net.Uri;

public class MessageModel {
    String uId, message, messageId, image;
    Long timestamp;

    public MessageModel(String uId, String message, Long timestamp) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public MessageModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public MessageModel(String uId, String message, String image) {
        this.uId = uId;
        this.message = message;
        this.image = image;
    }

    public MessageModel(String uId, String message, String image, Long timestamp) {
        this.uId = uId;
        this.message = message;
        this.image = image;
        this.timestamp = timestamp;
    }

    public MessageModel () {

    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
