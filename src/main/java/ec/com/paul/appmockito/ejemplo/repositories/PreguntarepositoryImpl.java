package ec.com.paul.appmockito.ejemplo.repositories;

import static ec.com.paul.appmockito.ejemplo.Datos.*;
import java.util.List;

public class PreguntarepositoryImpl implements PreguntaRepository {

    @Override
    public void guardarVarias(List<String> Preguntas) {
        System.out.println("PreguntarepositoryImpl.guardarVarias");
    }

    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println("PreguntarepositoryImpl.findPreguntasPorExamenId");
        return PREGUNTAS;
    }
}
