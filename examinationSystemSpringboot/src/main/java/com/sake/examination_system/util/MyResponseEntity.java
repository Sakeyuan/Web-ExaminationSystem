package com.sake.examination_system.util;

public class MyResponseEntity<T> {
    private int code;
    private String message ;
    private int total;
    private T data;
    private Object other;

    public void ok(){
        this.setCode(CodeNums.SUCCESS);
    }

    public void ok(int code,String message){
        this.code = code;
        this.message = message;
    }

    public  void error(){
        this.setCode(CodeNums.ERROR);
    }

    public void error(int code,String message){
        this.code = code;
        this.message = message;
    }

    public MyResponseEntity() {
        this.code = 5000;
        this.total = 0;
        this.message = null;
        this.data = null;
    }

    public MyResponseEntity(int code, String message) {
        this.code = code;
        this.message = message;
        this.total = 0; // Assuming default value for total
        this.data = null; // Assuming default value for data
    }

    public MyResponseEntity(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.total = 0;
        this.data = data;
    }

    // 泛型类的构造函数
    public MyResponseEntity(int code, String message, int total, T data) {
        this.code = code;
        this.message = message;
        this.total = total;
        this.data = data;
        this.other = null;
    }

    // 带有默认参数的构造函数
    public MyResponseEntity(int code, String message, int total, T data, Object other) {
        this.code = code;
        this.message = message;
        this.total = total;
        this.data = data;
        this.other = other;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", total=" + total +
                ", data=" + data +
                ", other=" + other +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getOther() {
        return other;
    }

    public void setOther(Object other) {
        this.other = other;
    }
}
