package com.example.demo.tool;

import com.example.demo.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;

@Slf4j
public class CustomerTools {


    @Tool(name = "getCustomer", description = "Retrieve customer information via name", resultConverter = CustomToolCallResultConverter.class)
    public Customer getCustomer(String name, ToolContext context) {
        log.info("Getting customer information via name for: {}, context:{}", name, context);
        return new Customer(name, "1234567890@example.com");
    }

    @Tool(name = "getCustomerByEmail", description = "Retrieve customer information via email", returnDirect = true)
    public Customer getCustomerByEmail(String email, ToolContext context) {
        log.info("Getting customer information via email for: {}, context:{}", email, context);
        return new Customer("Deom", email);
    }

}
