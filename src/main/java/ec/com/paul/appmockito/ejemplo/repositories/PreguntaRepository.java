package ec.com.paul.appmockito.ejemplo.repositories;

import java.util.List;

public interface PreguntaRepository {
    void guardarVarias(List<String> Preguntas);
    List<String> findPreguntasPorExamenId(Long id);
}
