package com.groupthree.shopsphere.dto.responses;

public class AuthResponse {
    private String message;
    public String status;

    public AuthResponse(){}
    public AuthResponse(String status,String message ){
        this.message=message;
        this.status =status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {return status;}

}
