package ec.com.paul.appmockito.ejemplo.repositories;

import ec.com.paul.appmockito.ejemplo.models.Examen;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ExamenRepositoryImpl implements ExamenRepository {

    @Override
    public List<Examen> findAll() {
        return Arrays.asList(
                new Examen(5L, "Matemáticas"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "historia")
        );
    }

    @Override
    public Examen guardar(Examen examen) {
        return null;
    }
}
