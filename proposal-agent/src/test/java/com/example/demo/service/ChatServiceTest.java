package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.model.AnalysisResultMessage;
import com.example.demo.model.ChatMessage;
import com.example.demo.tools.CustomerTools;

import reactor.core.publisher.Flux;

@SpringBootTest
class ChatServiceTest {

  @Mock
  private OllamaChatModel chatModel;

  @Mock
  private ChatClient chatClient;

  @Mock
  private ChatResponse chatResponse;

  private ChatService chatService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    chatService = new ChatService(chatModel);
  }

//  @Test
//  void testChat() {
//    String testMessage = "Hello";
//    String expectedResponse = "Hi there!";
//
//    when(ChatClient.create(any())).thenReturn(chatClient);
//    when(chatClient.prompt(testMessage)).thenReturn(chatClient);
//    when(chatClient.tools(any())).thenReturn(chatClient);
//    when(chatClient.toolContext(any())).thenReturn(chatClient);
//    when(chatClient.call()).thenReturn(chatResponse);
//    when(chatResponse.getContent()).thenReturn(expectedResponse);
//
//    String result = chatService.chat(testMessage);
//    assertEquals(expectedResponse, result);
//
//    verify(chatClient).prompt(testMessage);
//    verify(chatClient).tools(any(CustomerTools.class));
//    verify(chatClient).toolContext(Map.of("tenantId", "fake01"));
//  }
//
//  @Test
//  void testStreamChat() throws IOException {
//    String testMessage = "Hello";
//    SseEmitter emitter = mock(SseEmitter.class);
//
//    when(ChatClient.create(any())).thenReturn(chatClient);
//    when(chatClient.prompt(testMessage)).thenReturn(chatClient);
//    when(chatClient.tools(any())).thenReturn(chatClient);
//    when(chatClient.toolContext(any())).thenReturn(chatClient);
//    when(chatClient.stream()).thenReturn(chatClient);
//    when(chatClient.chatResponse()).thenReturn(Flux.just(chatResponse));
//
//    chatService.streamChat(testMessage, emitter);
//
//    verify(chatClient).prompt(testMessage);
//    verify(chatClient).tools(any(CustomerTools.class));
//    verify(chatClient).toolContext(Map.of("tenantId", "fake01"));
//    verify(chatClient).stream();
//    verify(chatClient).chatResponse();
//  }

  @Test
  void testStreamAnalysis() throws IOException {
    SseEmitter emitter = mock(SseEmitter.class);

    chatService.streamAnalysis(emitter);
    try {
      Thread.sleep(Duration.ofSeconds(1));
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // Verify that all messages are sent
    verify(emitter, times(4)).send(any(ChatMessage.class));
    verify(emitter).send(any(AnalysisResultMessage.class));
    verify(emitter).complete();
  }

  @Test
  void testStreamChatError() throws IOException {
    String testMessage = "Hello";
    SseEmitter emitter = mock(SseEmitter.class);
    RuntimeException testException = new RuntimeException("Test error");

    when(ChatClient.create(any())).thenThrow(testException);

    chatService.streamChat(testMessage, emitter);

    verify(emitter).completeWithError(testException);
  }

  @Test
  void testStreamAnalysisError() throws IOException {
    SseEmitter emitter = mock(SseEmitter.class);
    IOException testException = new IOException("Test error");

    doThrow(testException).when(emitter).send(any(ChatMessage.class));

    chatService.streamAnalysis(emitter);

    verify(emitter).completeWithError(any(RuntimeException.class));
  }

  @Test
  void test_flux() {
      Flux.interval(Duration.ofSeconds(1)).take(2).map(i -> i.intValue())
                      .doOnNext(System.out::println).subscribe();
      try {
          Thread.sleep(10000);
      } catch (InterruptedException e) {
          throw new RuntimeException(e);
      }
  }

}