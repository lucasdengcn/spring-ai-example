package com.example.demo.chat;

import com.example.demo.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/analysis")
@Slf4j
public class AnalysisStreamController {

    private final ChatService chatService;

    public AnalysisStreamController(ChatService chatService) {
        this.chatService = chatService;
    }

    @CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAnalysis() {
        log.info("Streaming analysis...");
        SseEmitter emitter = new SseEmitter();
        chatService.streamAnalysis(emitter);
        return emitter;
    }
}