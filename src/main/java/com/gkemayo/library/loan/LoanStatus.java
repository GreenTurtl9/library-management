package com.gkemayo.library.loan;

public enum LoanStatus {

    OPEN("OPEN"),
    CLOSE("CLOSE");

    private final String status;

    LoanStatus(String status){
        this.status = status;
    }

    public String getLoanStatus(){
        return this.status;
    }
}