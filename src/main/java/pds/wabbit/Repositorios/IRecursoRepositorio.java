
package pds.wabbit.Repositorios;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pds.wabbit.Entidades.Recurso;

public interface IRecursoRepositorio  extends JpaRepository<Recurso, String> {
    
    @Query("SELECT r FROM Recurso r WHERE r.proyecto.id=:idProyecto")
    public List<Recurso> recursosProyecto(@Param("idProyecto") String idProyecto);
}
