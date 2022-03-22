/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pds.wabbit.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import pds.wabbit.Entidades.TokenPassword;

/**
 *
 * @author Asus
 */
public interface ITokenPassword extends JpaRepository<TokenPassword, Long>{
    
    public TokenPassword findByToken(String token);
    
    
}
