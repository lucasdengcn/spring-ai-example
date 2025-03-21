package com.example.demo.service;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.tools.CustomerTools;

@Service
public class ChatService {

    private final OllamaChatModel chatModel;

    public ChatService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String message) {
        String content = ChatClient.create(chatModel)
                .prompt(message)
                .tools(new CustomerTools())
                .toolContext(Map.of("tenantId", "fake01"))
                .call().content();
        return content;
    }

    public void streamChat(String message, SseEmitter emitter) {
        try {
            ChatClient.create(chatModel)
                    .prompt(message)
                    .tools(new CustomerTools())
                    .toolContext(Map.of("tenantId", "fake01"))
                    .stream().chatResponse().doOnNext(new Consumer<ChatResponse>() {
                        @Override
                        public void accept(ChatResponse t) {
                            try {
                                emitter.send(t.getResult().getOutput().getText());
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        }
                    }).doOnComplete(() -> emitter.complete())
                    .doOnError(throwable -> emitter.completeWithError(throwable))
                    .subscribe();

        } catch (Exception e) {
            emitter.completeWithError(e);
        }
    }
}