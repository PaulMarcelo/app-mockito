package ec.com.paul.appmockito.ejemplo.repositories;

import ec.com.paul.appmockito.ejemplo.models.Examen;

import java.util.List;

public interface ExamenRepository {
    Examen guardar(Examen examen);

    List<Examen> findAll();
}
