/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pds.wabbit.Servicios;

import static java.nio.charset.StandardCharsets.US_ASCII;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import pds.wabbit.Entidades.TokenPassword;
import pds.wabbit.Entidades.Usuario;
import pds.wabbit.Repositorios.ITokenPassword;

/**
 *
 * @author Asus
 */
@Service
public class TokenPasswordServicio {

    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);

    @Value("${my.token.validity}")
    private int tokenValidityInSeconds;

    @Autowired
    private ITokenPassword tokenRepositorio;

    public TokenPassword crearSecureToken(Usuario usuario) {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII);
        TokenPassword secureToken = new TokenPassword();
        secureToken.setToken(tokenValue);
        secureToken.setUsuario(usuario);
        secureToken.setExpiraEn(LocalDateTime.now().plusSeconds(getTokenValidezEnSegundos()));
        tokenRepositorio.save(secureToken);
        return secureToken;
    }

    public int getTokenValidezEnSegundos() {
        return tokenValidityInSeconds;
    }

    public TokenPassword findByToken(String token) {
        return tokenRepositorio.findByToken(token);
    }

    public void removeTokenById(Long id) {
        tokenRepositorio.deleteById(id);
    }
    
    public boolean tokenValido(String token){
        TokenPassword secureToken = findByToken(token);
        if(Objects.isNull(secureToken) || !token.equals(secureToken.getToken())){
            return false;
        }else if(secureToken.estaExpirado()){
            removeTokenById(secureToken.getId());
            return false;
        }
        return true;
    }
}
