package com.armdoctor.exeptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponce {
    public String message;
    public Integer errorCode;

    public ErrorResponce(String message) {
        this.message = message;
    }
}
