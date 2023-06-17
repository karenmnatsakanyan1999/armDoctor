package com.armdoctor.service.impl;

import com.armdoctor.dto.requestDto.DoctorDTO;
import com.armdoctor.enums.Status;
import com.armdoctor.exeptions.ApiException;
import com.armdoctor.exeptions.ResourceAlreadyExistException;
import com.armdoctor.exeptions.UserNotFoundExeption;
import com.armdoctor.exeptions.UserValidationException;
import com.armdoctor.model.DoctorEntity;
import com.armdoctor.model.HospitalEntity;
import com.armdoctor.repository.DoctorRepository;
import com.armdoctor.repository.HospitalRepository;
import com.armdoctor.service.DoctorService;
import com.armdoctor.util.ArmDoctorMailSender;
import com.armdoctor.util.TokenGenerate;
import com.armdoctor.util.DoctorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceIMPL implements DoctorService {
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private ArmDoctorMailSender mailSender;

    @Override
    public DoctorEntity createUser(DoctorDTO doctorDTO) throws ApiException {
        DoctorValidation.validateFields(doctorDTO);
        DoctorValidation.validatePassword(doctorDTO.getPassword());
        validateDuplicate(doctorDTO);
        String verifyCode = TokenGenerate.generateVerifyCode();
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setId(0);
        doctorEntity.setName(doctorDTO.getName());
        doctorEntity.setSurname(doctorDTO.getSurname());
        doctorEntity.setYear(doctorDTO.getYear());
        doctorEntity.setEmail(doctorDTO.getEmail());
        doctorEntity.setPassword(passwordEncoder.encode(doctorDTO.getPassword()));
        doctorEntity.setVerifyCode(verifyCode);
        doctorEntity.setStatus(Status.INACTIVE);
        doctorEntity.setRole(doctorDTO.getRole());
        doctorEntity.setProfession(doctorDTO.getProfession());
        doctorEntity.setWorkTime(doctorDTO.getWorkTime());
        List<HospitalEntity> hospitalEntities = new ArrayList<>();
        for (int i = 0; i <doctorDTO.getHospitals().size() ; i++) {
            String s = doctorDTO.getHospitals().get(i);
            hospitalEntities.add(hospitalRepository.getByName(s));
        }
//        doctorEntity.setHospitalEntities(hospitalEntities);
        try {
            doctorRepository.save(doctorEntity);
        } catch (Exception e) {
            throw new ApiException("problem during saving user");
        }
        mailSender.sendEmail(doctorDTO.getEmail(), "your verify code", "Your verify code is" + verifyCode);
        return doctorEntity;
    }

    @Override
    public List<DoctorEntity> getByUsername(String email) throws ApiException {
        List<DoctorEntity> entityList = null;
        try {
            entityList = doctorRepository.getByEmail(email);
        } catch (Exception e) {
            throw new ApiException("problem during getting user");
        }
        return entityList;
    }

    @Override
    public DoctorEntity verifyUser(String email, String verifyCode) throws ApiException {
        DoctorEntity doctorEntity = null;
        try {
            doctorEntity = doctorRepository.getByEmailAndVerifyCode(email, verifyCode);
            if (doctorEntity == null) {
                throw new UserValidationException("wrong verify code " + verifyCode);
            } else {
                doctorEntity.setStatus(Status.ACTIVE);
                doctorEntity.setVerifyCode(null);
                doctorRepository.save(doctorEntity);
            }
        } catch (Exception e) {
            throw new ApiException("problem during verifying user");
        }
        return doctorEntity;
    }

    @Override
    public DoctorEntity changePassword(String oldPassword, String newPassword, String confirmPassword, String email) throws ApiException {
        DoctorEntity doctorEntity = null;
        DoctorValidation.validatePassword(newPassword);
        if (!confirmPassword.equals(newPassword)) {
            throw new UserValidationException("Passwords don't match");
        }
        try {
            doctorEntity = doctorRepository.findByEmail(email);
        } catch (Exception e) {
            throw new ApiException("problem during changing password");
        }
        if (!doctorEntity.getPassword().equals(passwordEncoder.encode(oldPassword))) {
            throw new UserValidationException("wrong old Password");
        }
        doctorEntity.setPassword(passwordEncoder.encode(newPassword));
        try {
            doctorRepository.save(doctorEntity);
        } catch (Exception e) {
            throw new ApiException("problem during changing password");
        }
        return doctorEntity;
    }

    @Override
    public DoctorEntity sendToken(String email) throws ApiException {
        DoctorEntity doctorEntity = null;
        try {
            doctorEntity = doctorRepository.findByEmail(email);
        } catch (Exception e) {
            throw new ApiException("problem during sending email");
        }
        if (doctorEntity == null) {
            throw new UserNotFoundExeption("wrong email " + email);
        }
        String resetToken = TokenGenerate.generateResetToken();
        doctorEntity.setResetToken(resetToken);
        doctorRepository.save(doctorEntity);
        mailSender.sendEmail(doctorEntity.getEmail(), "Reset Token", "Your reset token - " + resetToken);
        return doctorEntity;
    }

    @Override
    public Boolean verifyToken(String email, String token) throws ApiException {
        DoctorEntity doctorEntity = null;
        try {
            doctorEntity = doctorRepository.findByEmail(email);
        } catch (Exception e) {
            throw new ApiException("problem during verifying token");
        }
        if (!doctorEntity.getResetToken().equals(token)) {
            throw new UserValidationException("wrong reset token " + token);
        }
        return true;
    }

    @Override
    public DoctorEntity forgotPassword(String email, String password, String confirmPassword) throws ApiException {
        DoctorEntity doctorEntity = null;
        DoctorValidation.validatePassword(password);
        if (!password.equals(confirmPassword)) {
            throw new UserValidationException("passwords don't match");
        }
        try {
            doctorEntity = doctorRepository.findByEmail(email);
        } catch (Exception e) {
            throw new ApiException("problem during changing password");
        }
        if (doctorEntity.getResetToken() == null) {
            throw new ApiException("problem during changing password");
        }
        doctorEntity.setResetToken(null);
        doctorEntity.setPassword(passwordEncoder.encode(confirmPassword));
        doctorRepository.save(doctorEntity);
        return doctorEntity;
    }

    @Override
    public DoctorEntity update(DoctorDTO doctorDTO) throws ApiException {
        validateDuplicate(doctorDTO);
        DoctorValidation.validateFields(doctorDTO);
        Optional<DoctorEntity> optionalUser = doctorRepository.findById(doctorDTO.getId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundExeption("User not found with given ID");
        }
        DoctorEntity doctorEntity = optionalUser.get();
        doctorEntity.setName(doctorDTO.getName());
        doctorEntity.setSurname(doctorDTO.getSurname());
        doctorEntity.setYear(doctorDTO.getYear());
        doctorEntity.setEmail(doctorDTO.getEmail() == null ? doctorEntity.getEmail() : doctorDTO.getEmail());
        try {
            doctorRepository.save(doctorEntity);
        } catch (Exception e) {
            throw new ApiException("Problem during updating user");
        }
        return doctorEntity;
    }

    @Override
    public void delete(Integer id) throws ApiException {
        Optional<DoctorEntity> byId = doctorRepository.findById(id);
        if (byId.isEmpty()) {
            throw new UserNotFoundExeption("User not found with given Id");
        }

        try {
            doctorRepository.deleteById(id);
        } catch (Exception e) {
            throw new ApiException("Problem during deleting user");
        }
    }

    private void validateDuplicate(DoctorDTO doctorDTO) {
        if (doctorDTO.getId() == null) {
            List<DoctorEntity> doctorEntityList = doctorRepository.getByEmail(doctorDTO.getEmail());
            if (!doctorEntityList.isEmpty()) {
                throw new ResourceAlreadyExistException("User already exists");
            }
        } else {
            DoctorEntity doctorEntity = doctorRepository.getByEmailAndIdNot(doctorDTO.getEmail(), doctorDTO.getId());
            if (doctorEntity != null) {
                throw new ResourceAlreadyExistException("There is another User with this email");
            }
        }

    }
}
