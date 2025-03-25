package com.example.demo.prompt;

public class PromptQueryBuilder {

    // retrieve plain text of PDF
    public static String buildPlainTextPrompt(String pdfText) {
        StringBuilder prompt = new StringBuilder(1024);
        prompt.append("Below is the image of one page of a document, as well as some raw textual content that was previously extracted for it. \n");
        prompt.append("Just return the plain text representation of this document as if you were reading it naturally.\n");
        prompt.append("Do not hallucinate.\n");
        prompt.append("RAW_TEXT_START\n");
        prompt.append(pdfText);
        prompt.append("\nRAW_TEXT_END");
        return prompt.toString();
    }

}
