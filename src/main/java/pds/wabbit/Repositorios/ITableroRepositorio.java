
package pds.wabbit.Repositorios;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pds.wabbit.Entidades.Tablero;

public interface ITableroRepositorio  extends JpaRepository<Tablero, String> {
    
    @Query("SELECT r FROM Tablero r WHERE r.proyecto.id=:idProyecto")
    public List<Tablero> tareasProyecto(@Param("idProyecto") String idProyecto);
}
