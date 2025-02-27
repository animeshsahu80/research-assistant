package com.research.assistant;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ResearchRequest {
    private String content;
    private String operation;
    public String getOperation() {
        return operation;
    }
    public String getContent() {
        return content;
    }


}
