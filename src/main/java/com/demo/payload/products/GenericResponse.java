package com.demo.payload.products;


public record GenericResponse (String msg){

    public GenericResponse(String msg){
        this.msg = msg;
    }
}
