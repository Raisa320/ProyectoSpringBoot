package pds.wabbit.Repositorios;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pds.wabbit.Entidades.Votos;

public interface IVotoRepositorio extends JpaRepository<Votos, String> {
    
    @Query("SELECT s FROM Votos s WHERE s.usuario.id=:idUsuario AND s.proyecto.id=:idProyect")
    public List<Votos> votosProyectoUsuario(@Param("idUsuario") String idUsuario,@Param("idProyect") String idProyect);
    
//    @Query("SELECT s FROM Votos s WHERE s.usuario.id=:idUsuario AND s.pregunta.id=:idForo")
//    public List<Votos> votosForoUsuario(@Param("idUsuario") String idUsuario,@Param("idForo") String idForo);
    
    @Query("SELECT s.proyecto.id FROM Votos s WHERE s.usuario.id=:idUsuario  AND s.proyecto.id!=null")
    public List<String> idProyectosVotosPorUsuario(@Param("idUsuario") String idUsuario);
    
//    @Query("SELECT s.pregunta.id FROM Votos s WHERE s.usuario.id=:idUsuario AND s.pregunta.id!=null")
//    public List<String> idForoVotosPorUsuario(@Param("idUsuario") String idUsuario);
    
}
