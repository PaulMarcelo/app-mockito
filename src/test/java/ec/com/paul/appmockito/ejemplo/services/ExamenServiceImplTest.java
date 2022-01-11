package ec.com.paul.appmockito.ejemplo.services;

import ec.com.paul.appmockito.ejemplo.models.Examen;
import ec.com.paul.appmockito.ejemplo.repositories.ExamenRepository;
import ec.com.paul.appmockito.ejemplo.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static ec.com.paul.appmockito.ejemplo.services.Datos.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock
    private ExamenRepository examenRepository;
    @Mock
    private PreguntaRepository preguntaRepository;

    @InjectMocks
    private ExamenServiceImpl service;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        this.examenRepository = mock(ExamenRepository.class);
//        this.preguntaRepository = mock(PreguntaRepository.class);
//        this.service = new ExamenServiceImpl(examenRepository, preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);

        Optional<Examen> examen = this.service.findExamenPorNombre("Matemáticas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matemáticas", examen.orElseThrow().getNombre());
    }

    @Test
    void testPreguntasExamen() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);
        when(this.preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);

        Examen examen = this.service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Aritmetica"));
    }

    @Test
    void testPreguntasExamenVerify() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);
        when(this.preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);
        Examen examen = this.service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Aritmetica"));
        verify(this.examenRepository).findAll();
        verify(this.preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testGuardarExamen() {
        //Given.- precondiciones en entorno de pruebas
        Examen newExamen = EXAMEN;
        newExamen.setPreguntas(PREGUNTAS);
        when(this.examenRepository.guardar(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        //When.- Metodos para probar
        Examen examen = this.service.guardar(newExamen);

        //Then.- validaciones
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());
        verify(examenRepository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testManejoExcepcion() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES_ID_NULL);
        when(this.preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            this.service.findExamenPorNombreConPreguntas("Matemáticas");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(this.examenRepository).findAll();
        verify(this.preguntaRepository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void testArgumentMatchers() {
//        when(this.examenRepository.findAll()).thenReturn(EXAMENES_ID_NULL);
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);
        when(this.preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);

//        this.service.findExamenPorNombreConPreguntas("Matemáticas");
        this.service.findExamenPorNombreConPreguntas("Lenguaje");

        verify(this.examenRepository).findAll();
        //verify(this.preguntaRepository).findPreguntasPorExamenId(argThat(arg->arg!=null &&  arg.equals(5L)));
        verify(this.preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg > 5L));
//        verify(this.preguntaRepository).findPreguntasPorExamenId(eq(5L));
    }

    @Test
    void testArgumentMatchers2() {
//        when(this.examenRepository.findAll()).thenReturn(EXAMENES);
        when(this.examenRepository.findAll()).thenReturn(EXAMENES_ID_NEGATIVOS);
        when(this.preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);

//        this.service.findExamenPorNombreConPreguntas("Matemáticas");
        this.service.findExamenPorNombreConPreguntas("Historias");

        verify(this.examenRepository).findAll();
        verify(this.preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsmatchers()));
    }

    @Test
    void testArgumentMatchers3() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES_ID_NEGATIVOS);
        when(this.preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);

        this.service.findExamenPorNombreConPreguntas("Historias");

        verify(this.examenRepository).findAll();
        verify(this.preguntaRepository).findPreguntasPorExamenId(argThat((arg) -> arg != null && arg > 0));
    }


    public static class MiArgsmatchers implements ArgumentMatcher<Long> {

        private Long arg;

        @Override
        public boolean matches(Long aLong) {
            this.arg = aLong;
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return "Es para un mensaje personalizado de error que imprime  mochito en caso de que falle el test, " +
                    arg + " debe se run numero positivo";
        }
    }

}