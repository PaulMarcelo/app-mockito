package ec.com.paul.appmockito.ejemplo.services;

import ec.com.paul.appmockito.ejemplo.models.Examen;
import ec.com.paul.appmockito.ejemplo.repositories.ExamenRepository;
import ec.com.paul.appmockito.ejemplo.repositories.ExamenRepositoryImpl;
import ec.com.paul.appmockito.ejemplo.repositories.PreguntaRepository;
import ec.com.paul.appmockito.ejemplo.repositories.PreguntarepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static ec.com.paul.appmockito.ejemplo.Datos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {

    @Spy
    private ExamenRepositoryImpl examenRepository;

    @Spy
    private PreguntarepositoryImpl preguntaRepository;

    @InjectMocks
    private ExamenServiceImpl service;

    @Test
    void testSpy() {
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);
        doReturn(PREGUNTAS).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen= this.service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5,examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Aritmetica"));
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());

    }
}