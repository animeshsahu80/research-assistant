package com.research.assistant;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
public class ResearchController {
    public  final  ResearchService researchService;

    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @PostMapping("/process")
    ResponseEntity<String> processContent(@RequestBody ResearchRequest researchRequest){
        String result= researchService.processContent(researchRequest);
        return ResponseEntity.ok(result);
    }

}
