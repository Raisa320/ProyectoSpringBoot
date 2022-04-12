
package pds.wabbit.Repositorios;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pds.wabbit.Entidades.Postulacion;
import pds.wabbit.Entidades.Proyecto;

public interface IPostulacionRepositorio  extends JpaRepository<Postulacion, String> {
    
    @Query("SELECT s.proyecto FROM Postulacion s WHERE s.usuario.id=:idUsuario")
    public List<Proyecto> proyectosPostuladosPorUsuario(@Param("idUsuario") String idUsuario);
    
    @Query("SELECT s.proyecto.id FROM Postulacion s WHERE s.usuario.id=:idUsuario")
    public List<String> idProyectosPostuladosPorUsuario(@Param("idUsuario") String idUsuario);
    
    @Query("SELECT s FROM Postulacion s WHERE s.usuario.id=:idUsuario AND s.proyecto.id=:idProyect")
    public List<Postulacion> postulacionProyectoUsuario(@Param("idUsuario") String idUsuario,@Param("idProyect") String idProyect);
}
