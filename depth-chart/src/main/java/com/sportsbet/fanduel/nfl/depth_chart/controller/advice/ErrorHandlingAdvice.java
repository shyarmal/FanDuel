package com.sportsbet.fanduel.nfl.depth_chart.controller.advice;

import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.ErrorStatus;
import com.sportsbet.fanduel.nfl.depth_chart.controller.dto.ResponseWrapper;
import com.sportsbet.fanduel.nfl.depth_chart.exception.NFLDepthChartException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandlingAdvice {

    @ResponseBody
    @ExceptionHandler(NFLDepthChartException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ResponseWrapper<ErrorStatus>> handleNFLDepthChartException(NFLDepthChartException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseWrapper<ErrorStatus>> handleValidationErrors(ValidationException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ResponseWrapper<ErrorStatus>> handleRuntimeError(RuntimeException e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ResponseEntity<ResponseWrapper<ErrorStatus>> buildResponse(HttpStatus httpStatus, String message) {
        ResponseWrapper<ErrorStatus> response = new ResponseWrapper<>(new ErrorStatus(message, httpStatus.value()));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
