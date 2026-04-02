package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.dto.AiRecommendationResponse;
import com.example.TAXK.demo.dto.ChatRequest;
import com.example.TAXK.demo.dto.ChatResponse;
import com.example.TAXK.demo.service.AiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    /**
     * POST /api/ai/chat
     * Body: {"message": "Should I sell my AAPL?"}
     * The AI fetches portfolio context and history via tools, then gives advice.
     */
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        try {
            String reply = aiService.chat(request.getMessage());
            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("AI service unavailable: " + e.getMessage());
        }
    }

    /**
     * POST /api/ai/recommend
     * No request body needed — the AI gathers portfolio + news context automatically.
     * Returns: {"stocks": [...], "reasons": {"AAPL": "...", "TSLA": "..."}}
     */
    @PostMapping("/recommend")
    public ResponseEntity<?> recommend() {
        try {
            AiRecommendationResponse result = aiService.getRecommendation();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("AI service unavailable: " + e.getMessage());
        }
    }
}
