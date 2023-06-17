package com.armdoctor.config;

import com.armdoctor.exeptions.UserNotFoundExeption;
import com.armdoctor.model.DoctorEntity;
import com.armdoctor.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
public class CustomUserDetails implements UserDetailsService {
    @Autowired
    private DoctorRepository doctorRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        DoctorEntity doctorEntity = null;
        try {
            List<DoctorEntity> entityList = doctorRepository.getByEmail(s);
            doctorEntity = entityList.get(0);
        }catch (Exception e){
            throw new UserNotFoundExeption("wrong email " + s);
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        return new User(doctorEntity.getEmail(), doctorEntity.getPassword(),authorities);
    }
}
