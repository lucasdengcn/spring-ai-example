package com.example.demo.chat;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.function.Function;

public interface SseEmitterTask extends Function<SseEmitter, Boolean> {
}
