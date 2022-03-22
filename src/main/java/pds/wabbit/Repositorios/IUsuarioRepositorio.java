package pds.wabbit.Repositorios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pds.wabbit.Entidades.Usuario;

public interface IUsuarioRepositorio extends JpaRepository<Usuario, String> {

    @Query("SELECT d from Usuario d WHERE d.email=:email AND d.eliminado=false")
    public Usuario buscarUsuarioPorEmail(@Param("email") String email);
    
    @Query("SELECT d from Usuario d WHERE d.eliminado=false")
    public Page<Usuario> usuariosActivos(Pageable pageable);

}
