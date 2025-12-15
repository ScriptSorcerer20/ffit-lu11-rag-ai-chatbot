package ch.bzz.rag.controller;

import ch.bzz.rag.chat.ChatMode;
import ch.bzz.rag.service.ChatService;
import ch.bzz.rag.service.ChatService.ChatAnswer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        try {
            // TODO: implement logic
            ChatAnswer response = null;

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during crawling: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    public record ChatRequest(String question, ChatMode mode) {}
}
