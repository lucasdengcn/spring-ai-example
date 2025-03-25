package com.example.demo.service;

import com.example.demo.pdf.PDFPageReport;
import com.example.demo.pdf.PdfElement;
import com.example.demo.pdf.PdfStructureService;
import com.example.demo.prompt.PromptQueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PdfVLInstructService {

    private final PdfStructureService pdfStructureService;

    private final OpenAiChatModel openAiChatModel;

    public PdfVLInstructService(PdfStructureService pdfStructureService, OpenAiChatModel openAiChatModel) {
        this.pdfStructureService = pdfStructureService;
        this.openAiChatModel = openAiChatModel;
    }

    public void processPdf(String pdfFilePath, int pageNumber) throws IOException {
        File pdfFile = new File(pdfFilePath);
        // Step 1: Use PdfStructureService to get PDFPageReport
        PDFPageReport pageReport = pdfStructureService.generatePageReport(pdfFile, pageNumber);

        // Step 2: build PDF anchor text with PDF text elements and image elements
        String anchorText = buildAnchorText(pageReport);

        // Step 3: Build query prompt with PromptQueryBuilder
        String promptText = PromptQueryBuilder.buildPlainTextPrompt(anchorText);
        // log.info("Prompt Text: {}\n {}", promptText.length(), promptText);

        // Step 4: call LLM
        String response = executeWithOCRModel(promptText, pageReport.getPageBase64PNG());

        // Step 5: Save response to resource folder
        saveResponseToFile(response);
    }

    private String buildAnchorText(PDFPageReport pageReport) {
        // 实现构建 PDF 锚文本的逻辑
        List<PdfElement> elements = new ArrayList<>();
        elements.addAll(pageReport.getTextElements());
        elements.addAll(pageReport.getImageElements());
        //
        elements.sort((e1, e2) -> {
            if (e1.getY() == e2.getY()) {
                return Float.compare(e1.getX(), e2.getX());
            } else {
                return Float.compare(e2.getY(), e1.getY());
            }
        });
        StringBuilder sb = new StringBuilder(2048);
        int length = 0;
        for (PdfElement element : elements) {
            String s = element.toString();
            length += s.length() + 2;
            if (length > 2048){
                break;
            }
            sb.append(s).append("\n");
        }
        //
        return sb.toString();
    }

    private String executeWithOCRModel(String promptText, byte[] imageData) {
        // 实现构建消息的逻辑
        String model = "bsahane/Qwen2.5-VL-7B-Instruct:Q4_K_M_benxh";
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .temperature(0.0).maxTokens(3000).model(model)
                .build();
        //
        ChatClient chatClient = ChatClient.builder(openAiChatModel).defaultOptions(chatOptions).build();
        String content = chatClient
                .prompt()
                .user(u -> {
                    u.text(promptText).media(Media.builder().data(imageData).mimeType(MimeTypeUtils.IMAGE_PNG).build());
                })
                .call().content();
        //
        log.info("Chat Response: {}", content);
        return content;
    }

    private void saveResponseToFile(String response) throws IOException {
        // 实现保存响应到文件的逻辑
        String filePath = Paths.get("src/main/resources/output.txt").toString();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(response.getBytes());
        }
    }
}