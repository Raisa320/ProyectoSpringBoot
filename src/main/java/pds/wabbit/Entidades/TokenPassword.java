/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pds.wabbit.Entidades;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

/**
 *
 * @author Asus
 */
@Entity
@Data
public class TokenPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp time_Stamp;

    @Column(updatable = false)
    @Basic(optional = false)
    private LocalDateTime expiraEn;

    @ManyToOne
    private Usuario usuario;

    @Transient
    private boolean estaExpirado;
    
    public boolean estaExpirado() {
        return getExpiraEn().isBefore(LocalDateTime.now()); 
    }
}
