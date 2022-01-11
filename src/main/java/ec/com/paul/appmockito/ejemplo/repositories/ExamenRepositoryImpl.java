package ec.com.paul.appmockito.ejemplo.repositories;

import ec.com.paul.appmockito.ejemplo.models.Examen;

import static ec.com.paul.appmockito.ejemplo.Datos.*;
import java.util.List;

public class ExamenRepositoryImpl implements ExamenRepository {

    @Override
    public List<Examen> findAll() {
//        return Arrays.asList(
//                new Examen(5L, "Matemáticas"),
//                new Examen(6L, "Lenguaje"),
//                new Examen(7L, "historia")
//        );
        System.out.println("ExamenRepositoryImpl.findAll");
        return EXAMENES;
    }

    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImpl.guardar");
        return EXAMEN;
    }
}
