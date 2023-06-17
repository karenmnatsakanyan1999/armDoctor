package com.armdoctor.service;

import com.armdoctor.dto.requestDto.DoctorDTO;
import com.armdoctor.exeptions.ApiException;
import com.armdoctor.model.DoctorEntity;

import java.util.List;

public interface DoctorService {
    DoctorEntity createUser(DoctorDTO doctorDTO) throws ApiException;
    List<DoctorEntity> getByUsername(String email) throws ApiException;
    DoctorEntity verifyUser(String email, String verifyCode) throws ApiException;
    DoctorEntity changePassword(String oldPassword, String newPassword, String confirmPassword, String email) throws ApiException;
    DoctorEntity sendToken(String email) throws ApiException;
    Boolean verifyToken(String email,String token) throws ApiException;
    DoctorEntity forgotPassword(String email, String password, String confirmPassword) throws ApiException;
    DoctorEntity update(DoctorDTO doctorDTO) throws ApiException;
    void delete(Integer id) throws ApiException;
}
