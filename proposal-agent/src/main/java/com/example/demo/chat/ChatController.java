package com.example.demo.chat;

import com.example.demo.tool.CustomerTools;
import com.example.demo.tool.DateTimeTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@RestController
public class ChatController {

    private final OllamaChatModel chatModel;
    private final DateTimeTools dateTimeTools;
    private final ChatClient weatherChatClient;

    @Autowired
    public ChatController(OllamaChatModel chatModel, ToolCallbackProvider toolCallbackProvider) {
        this.chatModel = chatModel;
        this.dateTimeTools = new DateTimeTools();
        //
        Arrays.stream(toolCallbackProvider.getToolCallbacks()).forEach(new Consumer<FunctionCallback>() {
            @Override
            public void accept(FunctionCallback functionCallback) {

            }
        });

        weatherChatClient = ChatClient.builder(chatModel).defaultTools(toolCallbackProvider).build();
    }

    @GetMapping("/ai/customer")
    public Map<String,String> customerInfo(@RequestParam(value = "name", defaultValue = "lucas") String name) {
        String message = "Please help to get this customer info via name, " + name;
        String content = ChatClient.create(chatModel)
                    .prompt(message)
                        .tools(new CustomerTools())
                .toolContext(Map.of("tenantId", "fake01"))
                .call().content();
        return Map.of("generation", content);
    }

    @GetMapping("/ai/customerByEmail")
    public Map<String,String> customerInfoV2(@RequestParam(value = "email", defaultValue = "lucas@example.com") String email) {
        String message = "Please help to get this customer info via email, " + email;
        String content = ChatClient.create(chatModel)
                .prompt(message)
                .tools(new CustomerTools())
                .toolContext(Map.of("tenantId", "fake01"))
                .call().content();
        return Map.of("generation", content);
    }

    @GetMapping("/ai/weather")
    public Map<String,String> weather(@RequestParam(value = "city", defaultValue = "Shenzhen") String city) {
        String message = "What's the weather like in " + city + " China?";
        // String content = ChatClient.create(chatModel).prompt(message).tools(WeatherTools.CURRENT_WEATHER).call().content();
        String content = weatherChatClient.prompt(message).call().content();
        return Map.of("generation", content);
    }

    @GetMapping("/ai/datetime")
    public Map<String,String> datetime(@RequestParam(value = "message", defaultValue = "What day is tomorrow?") String message) {
        String content = ChatClient.create(chatModel).prompt(message).tools(this.dateTimeTools).call().content();
        return Map.of("generation", content);
    }

    @PostMapping("/ai/alarm")
    public Map<String,String> setAlarm(@RequestParam(value = "message", defaultValue = "What is the time now? Can you set an alarm 10 minutes from now?") String message) {
        String content = ChatClient.create(chatModel).prompt(message).tools(this.dateTimeTools).call().content();
        return Map.of("generation", content);
    }

    @GetMapping("/ai/generate")
    public Map<String,String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.chatModel.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> stream = this.chatModel.stream(prompt);
        return stream;
    }

}
