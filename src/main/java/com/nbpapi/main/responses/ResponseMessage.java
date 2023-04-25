package com.nbpapi.main.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.coyote.Response;

@Getter
@Setter
public class ResponseMessage {
    public ResponseMessage(String message) {
        this.message = message;
    }
    private String message;
}
