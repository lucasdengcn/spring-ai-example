package com.example.demo.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DateTimeToolsTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCurrentDateTimeToolDefinition() {
        //
        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTime");
        ToolDefinition toolDefinition = ToolDefinition.builder(method)
                .name("currentDateTime")
                .description("Get the current date and time in the user's timezone")
                .inputSchema(JsonSchemaGenerator.generateForMethodInput(method))
                .build();
        //
        assertEquals("currentDateTime", toolDefinition.name());
        try {
            System.out.printf(objectMapper.writeValueAsString(toolDefinition));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}