package com.example.demo.pdf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;

@Service
public class PdfAnalysisService {

    public List<TextElement> extractTextElements(File pdfFile, int startPage, int endPage) throws IOException {
        List<TextElement> textElements = new ArrayList<>();
        try (PDDocument document = loadPdfDocument(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper() {
                @Override
                protected void processTextPosition(TextPosition text) {
                    textElements.add(new TextElement(
                            text.getUnicode(),
                            text.getXDirAdj(),
                            text.getYDirAdj(),
                            text.getWidth(),
                            text.getHeight(),
                            getCurrentPageNo()));
                }
            };
            stripper.setSortByPosition(true);
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            stripper.getText(document);
        }
        return textElements;
    }

    public List<ImageElement> extractImageElements(File pdfFile, int startPage, int endPage) throws IOException {
        List<ImageElement> imageElements = new ArrayList<>();
        try (PDDocument document = loadPdfDocument(pdfFile)) {
            for (int pageIndex = startPage - 1; pageIndex < endPage; pageIndex++) {
                PDPage page = document.getPage(pageIndex);
                ImageExtractor imageExtractor = new ImageExtractor();
                imageExtractor.setCurrentPageNo(pageIndex + 1);
                imageExtractor.processPage(page);
                imageElements.addAll(imageExtractor.getImages());
            }
        }
        return imageElements;
    }

    private class ImageExtractor extends PDFStreamEngine {
        private final List<ImageElement> images = new ArrayList<>();
        private int currentPageNo;

        public ImageExtractor() {
            addOperator(new Concatenate(this));
            addOperator(new DrawObject(this));
            addOperator(new Save(this));
            addOperator(new Restore(this));
            addOperator(new SetMatrix(this));
        }

        public void setCurrentPageNo(int pageNo) {
            this.currentPageNo = pageNo;
        }

        public List<ImageElement> getImages() {
            return images;
        }

        @Override
        protected void processOperator(org.apache.pdfbox.contentstream.operator.Operator operator,
                List<org.apache.pdfbox.cos.COSBase> operands) throws IOException {
            String operation = operator.getName();
            if ("Do".equals(operation)) {
                COSBase cosBase = operands.get(0);
                PDXObject xobject = getResources().getXObject((COSName) cosBase);
                if (xobject instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject) xobject;
                    Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
                    // 修复：确保图像坐标和尺寸计算正确
                    float x = ctm.getTranslateX();
                    float y = ctm.getTranslateY();
                    float width = image.getWidth() * ctm.getScaleX();
                    float height = image.getHeight() * ctm.getScaleY();
                    images.add(new ImageElement(
                            x,
                            y,
                            width,
                            height,
                            currentPageNo,
                            image.getStream().toByteArray()));
                }
            }
            super.processOperator(operator, operands);
        }
    }

    public List<TableElement> extractTableElements(File pdfFile, int startPage, int endPage) throws IOException {
        List<TableElement> tables = new ArrayList<>();
        try (PDDocument document = loadPdfDocument(pdfFile)) {
            for (int pageIndex = startPage - 1; pageIndex < endPage; pageIndex++) {
                PDPage page = document.getPage(pageIndex);
                List<TextElement> textElements = extractTextElements(pdfFile, startPage, endPage);
                Map<Float, List<String>> rowMap = new TreeMap<>();
                for (TextElement text : textElements) {
                    if (text.getPageNumber() == pageIndex + 1) {
                        rowMap.computeIfAbsent(text.getY(), k -> new ArrayList<>()).add(text.getText());
                    }
                }
                List<List<String>> cells = new ArrayList<>(rowMap.values());
                if (!cells.isEmpty()) {
                    tables.add(new TableElement(cells, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight(), pageIndex + 1));
                }
            }
        }
        return tables;
    }

    public List<PageHeaderFooter> extractHeaderFooter(File pdfFile, int startPage, int endPage) throws IOException {
        List<PageHeaderFooter> headerFooters = new ArrayList<>();
        try (PDDocument document = loadPdfDocument(pdfFile)) {
            for (int pageIndex = startPage - 1; pageIndex < endPage; pageIndex++) {
                PDPage page = document.getPage(pageIndex);
                PDRectangle mediaBox = page.getMediaBox();
                float pageHeight = mediaBox.getHeight();

                float headerRegionHeight = pageHeight * 0.1f;
                float footerRegionHeight = pageHeight * 0.1f;

                final StringBuilder headerText = new StringBuilder();
                final StringBuilder footerText = new StringBuilder();
                final int pageNo = pageIndex + 1;

                PDFTextStripper stripper = new PDFTextStripper() {
                    @Override
                    protected void processTextPosition(TextPosition text) {
                        float y = text.getYDirAdj();
                        if (y <= headerRegionHeight) {
                            headerText.append(text.getUnicode());
                        } else if (y >= (pageHeight - footerRegionHeight)) {
                            footerText.append(text.getUnicode());
                        }
                    }
                };

                stripper.setStartPage(pageNo);
                stripper.setEndPage(pageNo);
                stripper.getText(document);

                headerFooters.add(new PageHeaderFooter(
                        headerText.toString().trim(),
                        footerText.toString().trim(),
                        pageNo));
            }
        }
        return headerFooters;
    }

    private PDDocument loadPdfDocument(File pdfFile) throws IOException {
        if (pdfFile.length() > 10 * 1024 * 1024) { // For files larger than 10MB, use non-sequential loading
            return Loader.loadPDF(pdfFile, IOUtils.createTempFileOnlyStreamCache());
        } else {
            return Loader.loadPDF(pdfFile);
        }
    }
}