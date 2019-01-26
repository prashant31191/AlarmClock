package com.better.alarm.model;

import com.github.androidutils.statemachine.HandlerFactory;
import com.github.androidutils.statemachine.IHandler;
import com.github.androidutils.statemachine.ImmutableMessage;
import com.github.androidutils.statemachine.Message;
import com.github.androidutils.statemachine.MessageHandler;

public class ImmedeateHandlerFactory implements HandlerFactory {
    @Override
    public IHandler create(final MessageHandler messageHandler) {
        return new IHandler() {
            @Override
            public void sendMessageAtFrontOfQueue(Message message) {
                messageHandler.handleMessage(message);
            }

            @Override
            public void sendMessage(Message message) {
                messageHandler.handleMessage(message);
            }

            @Override
            public ImmutableMessage obtainMessage(int what, Object obj) {
                return obtainMessage(what).withObj(obj);
            }

            @Override
            public ImmutableMessage obtainMessage(int what) {
                return ImmutableMessage.builder().what(what).handler(this).build();
            }
        };
    }
}
