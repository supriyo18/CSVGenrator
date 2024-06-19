package com.fetchCSV.Tiger.Controller;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173/") // Adjust the frontend URL as necessary
public class BatchController {

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);
    @Autowired
    private JobLauncher jobLauncher;

    @Resource(name = "exportJob") // Use the name of your job bean
    private Job job;

    @GetMapping("/startBatch")
    public ResponseEntity<String> startBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(job, jobParameters);
            return ResponseEntity.ok("Batch job started successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error starting batch job: " + e.getMessage());
        }
    }

    @GetMapping("/downloadCsv")
    public ResponseEntity<FileSystemResource> downloadCsv() {
        try {
            FileSystemResource file = new FileSystemResource("src/main/resources/output.csv");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "output.csv");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(file.contentLength());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(file);
        } catch (IOException e) {
            logger.error("Error downloading CSV file: {}", e.getMessage());
            // Return an appropriate error response
            return ResponseEntity.notFound().build(); // For example, return 404 Not Found
        }
    }
}
