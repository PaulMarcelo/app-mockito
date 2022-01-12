package ec.com.paul.appmockito.ejemplo.services;

import ec.com.paul.appmockito.ejemplo.models.Examen;
import ec.com.paul.appmockito.ejemplo.repositories.ExamenRepository;
import ec.com.paul.appmockito.ejemplo.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepository examRepository, PreguntaRepository preguntaRepository) {
        this.examRepository = examRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return this.examRepository.findAll().stream().filter(item ->
                item.getNombre().contains(nombre)).findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = this.findExamenPorNombre(nombre);
        Examen examen = null;
        if (examenOptional.isPresent()) {
            examen = examenOptional.orElseThrow();
            List<String> preguntas = this.preguntaRepository.findPreguntasPorExamenId(examen.getId());
            this.preguntaRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            this.preguntaRepository.guardarVarias(examen.getPreguntas());
        }
        return this.examRepository.guardar(examen);
    }
}
