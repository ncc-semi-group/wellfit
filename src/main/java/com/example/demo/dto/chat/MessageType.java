package com.example.demo.dto.chat;

public enum MessageType {
    CREATE("님이 입장했습니다."), TALK("talk"), IMAGE("image"), DELETE("님이 퇴장했습니다."), ENTER("enter"), EXIT("exit"), READ("read"); ;
    public final String message;
    MessageType(String message){
        this.message = message;
    }
}