package com.sportsbet.fanduel.nfl.depth_chart.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {

    private T data;
    private ErrorStatus errorStatus;

    public ResponseWrapper(T data) {
        this.data = data;
    }

    public ResponseWrapper(ErrorStatus errorStatus) {
        this.errorStatus = errorStatus;
    }

    public ResponseWrapper(T data, ErrorStatus errorStatus) {
        this.data = data;
        this.errorStatus = errorStatus;
    }

    public T getData() {
        return data;
    }

    public ErrorStatus getError() {
        return errorStatus;
    }
}
