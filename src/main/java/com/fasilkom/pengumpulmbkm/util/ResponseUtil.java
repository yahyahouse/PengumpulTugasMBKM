package com.fasilkom.pengumpulmbkm.util;

import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseUtil {

    public static ResponseEntity<BaseResponse> buildResponse(HttpStatus status, String message, Object data) {
        BaseResponse response = new BaseResponse();
        BaseResponse.Transaction transaction = new BaseResponse.Transaction();
        transaction.setStatus(String.valueOf(status.value()));
        transaction.setMessage(message);
        // transaction.setTransactionId(...) // Could be added if CorrelationId is available
        
        response.setTransaction(transaction);
        response.setData(data);
        
        return new ResponseEntity<>(response, status);
    }

    public static ResponseEntity<BaseResponse> ok(Object data) {
        return buildResponse(HttpStatus.OK, "Success", data);
    }

    public static ResponseEntity<BaseResponse> ok(String message, Object data) {
        return buildResponse(HttpStatus.OK, message, data);
    }

    public static ResponseEntity<BaseResponse> error(HttpStatus status, String message) {
        return buildResponse(status, message, null);
    }
}
