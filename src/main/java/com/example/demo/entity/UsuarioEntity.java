package com.example.demo.entity;
import com.example.demo.domain.Rol;
import jakarta.persistence.*;

@Entity //la clase se guarda en una tabla 
@Table (name = "usuarios") 
public class UsuarioEntity {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) //permite que SQL genere un ID automaticamente. 
    private Long id;

    @Column (nullable = false,unique = true)
    private String email; 

    @Column (name= "password_hash",nullable = false)
    private String passwordHash; 

    @Enumerated (EnumType.STRING) //guarda el rol como texto 
    @Column(nullable = false)
    private Rol rol; 

    public UsuarioEntity(){
    }

    public Long getId(){
        return id; 
    }
    public void setId(Long id){
        this.id = id; 
    }
    public String getEmail(){
        return email; 
    }
    public void setEmail(String email){
        this.email = email; 
    }
    public String getPasswordHash(){
        return passwordHash;
    }
   public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
}
    public Rol getRol(){
        return rol;
    }
   public void setRol(Rol rol) {
    this.rol = rol;
}



}
