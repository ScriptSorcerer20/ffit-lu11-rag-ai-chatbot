package ch.bzz.rag.controller;

import ch.bzz.rag.service.WikiCrawlerPipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/crawl")
public class CrawlerController {

    private final WikiCrawlerPipelineService pipeline;

    @PostMapping("/wiki")
    public ResponseEntity<String> crawl(@RequestBody WikiCrawlRequest request) {
        try {
            // TODO: implement logic
            return ResponseEntity.ok("Crawling finished successfully");
        } catch (Exception e) {
            log.error("Error during crawling: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    public record WikiCrawlRequest(String namespaceUrl, String regexFilter, boolean overwriteExisting) {}
}
