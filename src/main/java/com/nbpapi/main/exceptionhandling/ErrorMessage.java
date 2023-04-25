package com.nbpapi.main.exceptionhandling;

import javax.swing.*;
import java.util.Date;

// the structure for message response in case of an error
public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String message;
    private String description;

    // constructor with all variables of this class
    public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }

    // getters for all members of the class
    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

}
