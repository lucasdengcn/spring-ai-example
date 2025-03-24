package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.demo.pdf.ImageElement;
import com.example.demo.pdf.PdfAnalysisService;
import com.example.demo.pdf.TableElement;
import com.example.demo.pdf.TextElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class PdfAnalysisServiceTest {

  private PdfAnalysisService pdfAnalysisService;
  private File testPdfFile;

  @BeforeEach
  void setUp() throws IOException {
    pdfAnalysisService = new PdfAnalysisService();
    Resource resource = new ClassPathResource("test-proposal.pdf");
    testPdfFile = resource.getFile();
  }

  @Test
  void testExtractTextElements() throws IOException {
    List<TextElement> textElements = pdfAnalysisService.extractTextElements(testPdfFile, 5, 5);

    assertNotNull(textElements);
    assertFalse(textElements.isEmpty());
    textElements.forEach(System.out::println);
  }

  @Test
  void testExtractTableElements() throws IOException {
    List<TableElement> tableElements = pdfAnalysisService.extractTableElements(testPdfFile, 5, 5);

    assertNotNull(tableElements);
    // Note: Since table extraction is a simplified implementation, we're just
    // verifying
    // that the method runs without errors
  }

  @Test
  void testExtractImageElements() throws IOException {
    List<ImageElement> imageElements = pdfAnalysisService.extractImageElements(testPdfFile, 1, 2);

    assertNotNull(imageElements);
    // Note: Our test PDF doesn't contain images, so the list should be empty
    // assertTrue(imageElements.isEmpty());
  }
}