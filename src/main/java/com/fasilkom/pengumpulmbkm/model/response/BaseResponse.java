package com.fasilkom.pengumpulmbkm.model.response;

import lombok.Data;

@Data
public class BaseResponse {
    private Transaction transaction;
    private Object data;

    @Data
    public static class Transaction {
        private String transactionId;
        private String status;
        private String message;
    }
}
