//package com.agrowmart.admin_seller_management.dto;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class ApiResponseDTO<T> {
//
//    private boolean success;
//    private String message;
//    private T data;
//    private Object errors;
//
//    public ApiResponseDTO() {}
//
//    public ApiResponseDTO(boolean success, String message) {
//        this.success = success;
//        this.message = message;
//    }
//
//    public ApiResponseDTO(boolean success, String message, T data) {
//        this.success = success;
//        this.message = message;
//        this.data = data;
//    }
//
//    public ApiResponseDTO(boolean success, String message, T data, Object errors) {
//        this.success = success;
//        this.message = message;
//        this.data = data;
//        this.errors = errors;
//    }
//
//    public boolean isSuccess() {
//        return success;
//    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public T getData() {
//        return data;
//    }
//
//    public void setData(T data) {
//        this.data = data;
//    }
//
//    public Object getErrors() {
//        return errors;
//    }
//
//    public void setErrors(Object errors) {
//        this.errors = errors;
//    }
//}

package com.agrowmart.admin_seller_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;
    private Object errors;

    public ApiResponseDTO() {}

    public ApiResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponseDTO(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponseDTO(boolean success, String message, T data, Object errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
