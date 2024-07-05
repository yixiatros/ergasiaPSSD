package com.example.ergasiapssd;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PdfController {

    @GetMapping("/studentPDF")
    public ResponseEntity<byte[]> getStudentPdf() throws IOException {
        ClassPathResource pdfFile = new ClassPathResource("static/Help/StudentHelp.pdf");

        byte[] pdfBytes = pdfFile.getInputStream().readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "StudentHelp.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/teacherPDF")
    public ResponseEntity<byte[]> getTeacherPdf() throws IOException {
        ClassPathResource pdfFile = new ClassPathResource("static/Help/TeacherHelp.pdf");

        byte[] pdfBytes = pdfFile.getInputStream().readAllBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "TeacherHelp.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
