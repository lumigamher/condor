package com.projectcondor.condor.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection; 

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // Este método debe devolver los roles o permisos (authorities) del usuario
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Aquí podrías implementar la lógica para devolver los roles o permisos
        return null;  // Devuelve los authorities como una colección de GrantedAuthority
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    // Método para establecer la contraseña
    public void setPassword(String password) {
        this.password = password;  // Asigna la contraseña al campo privado
    }
}
