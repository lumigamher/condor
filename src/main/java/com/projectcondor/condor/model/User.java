package com.projectcondor.condor.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;


@Data
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    private String address;
    
    @Column(name = "birth_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime birthDate;

    private String gender;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // logica para implentar los roles o permisos cuando se neseciten
        return null;  // Devuelve los authorities como una colección de GrantedAuthority
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}