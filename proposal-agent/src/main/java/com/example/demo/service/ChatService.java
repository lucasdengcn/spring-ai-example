package com.example.demo.service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.model.AnalysisResult;
import com.example.demo.model.AnalysisResultMessage;
import com.example.demo.model.ChatMessage;
import com.example.demo.tool.CustomerTools;

import reactor.core.publisher.Flux;

@Service
public class ChatService {

    private final OllamaChatModel chatModel;

    List<ChatMessage> messages = new ArrayList<>();
    AnalysisResultMessage analysisResultMessage = null;

    public ChatService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
        messages.add(new ChatMessage("Analyzing document structure..."));
        messages.add(new ChatMessage("Extracting coverage information..."));
        messages.add(new ChatMessage("Evaluating risk factors..."));
        messages.add(new ChatMessage("Analyzing document structure..."));
        //
        Map<String, Double> insuranceMap = Map.of(
                "Auto Insurance", 45.0,
                "Home Insurance", 30.0,
                "Health Insurance", 20.0,
                "Life Insurance", 5.0);
        Map<String, Double> riskMap = Map.of(
                "Market Risk", 6.2,
                "Health Risk", 8.1,
                "Liability Risk", 4.5,
                "Natural Disaster Risk", 3.8);
        analysisResultMessage = new AnalysisResultMessage("Analysis complete!",
                new AnalysisResult(insuranceMap, riskMap));
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
                                String text = t.getResult().getOutput().getText();
                                emitter.send(new ChatMessage(text));
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

    public void streamAnalysis(SseEmitter emitter) {
        //
        Flux.interval(Duration.ofSeconds(1))
                .take(messages.size())
                .map(i -> messages.get(i.intValue()))
                .doOnNext(message -> {
                    try {
                        emitter.send(message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnComplete(() -> {
                    try {
                        emitter.send(analysisResultMessage);
                        emitter.complete();
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                })
                .doOnError(emitter::completeWithError)
                .subscribe();
    }
}