package com.armdoctor.util;

import com.armdoctor.dto.requestDto.DoctorDTO;
import com.armdoctor.exeptions.UserValidationException;


public class DoctorValidation {
    public static void validateFields(DoctorDTO doctorDTO){
        if (doctorDTO.getName()==null|| doctorDTO.getName().isBlank()){
            throw new UserValidationException("User's name can not be null or empty");
        }
        if (doctorDTO.getSurname()==null|| doctorDTO.getSurname().isBlank()){
            throw new UserValidationException("User's surname can not be null or empty");
        }
        if (doctorDTO.getYear()==null|| doctorDTO.getYear()<1910|| doctorDTO.getYear()>2020){
            throw new UserValidationException("User's age must be between 1910 - 2020");
        }
    }
    public static void validatePassword(String password){
        if (password==null||password.isBlank()){
            throw new UserValidationException("Password can not be null or empty");
        }
        if (password.length()<6){
            throw new UserValidationException("Password must be more than 6 charecters");
        }
        int countOfUppercase = 0;
        int countOfDigits = 0;
        for (int i = 0; i <password.length() ; i++) {
            char c = password.charAt(i);
            if (Character.isDigit(c)){
                countOfDigits++;
            } else if (Character.isUpperCase(c)) {
                countOfUppercase++;
            }
        }
        if (countOfUppercase<1){
            throw new UserValidationException("password must contain at least one uppercase");
        }
        if (countOfDigits<2){
            throw new UserValidationException("password must contain more then 2 digits");
        }
    }
}
