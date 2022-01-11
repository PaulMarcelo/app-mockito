package ec.com.paul.appmockito.ejemplo.services;

import ec.com.paul.appmockito.ejemplo.models.Examen;

import java.util.List;
import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findExamenPorNombre(
            String nombre);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen guardar(Examen examen);

}
