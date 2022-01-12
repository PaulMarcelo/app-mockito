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

import java.util.Collections;
import java.util.Optional;

import static ec.com.paul.appmockito.ejemplo.Datos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock
    private ExamenRepositoryImpl examenRepository;
    @Mock
    private PreguntarepositoryImpl preguntaRepository;

    @InjectMocks
    private ExamenServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;

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

    @Test
    void testArgumentCaptor() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);
        //when(this.preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);

        this.service.findExamenPorNombreConPreguntas("Matemáticas");
        //   ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(this.preguntaRepository).findPreguntasPorExamenId(captor.capture());
        assertEquals(5L, captor.getValue());

    }


    @Test
    void testDoThrow() {
        Examen examen = EXAMEN;
        examen.setPreguntas(PREGUNTAS);
        //when(this.preguntaRepository.guardarVarias()).thenThrow(IllegalArgumentException.class);
        doThrow(IllegalArgumentException.class).when(this.preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            this.service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);
        //when(this.preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);
        doAnswer((invocation) -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? PREGUNTAS : null;
        }).when(this.preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = this.service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
         
    }

    @Test
    void testDoCallrealMethod() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);
        //when(this.preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);
        doCallRealMethod().when(this.preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = this.service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
    }

    @Test
    void testSpy() {
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntarepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);

        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(PREGUNTAS);
        doReturn(PREGUNTAS).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen= examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5,examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("Aritmetica"));
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testOrdenInvocacion() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);

        this.service.findExamenPorNombreConPreguntas("Matemáticas");
        this.service.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(preguntaRepository);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    void testOrdenInvocacion2() {
        when(this.examenRepository.findAll()).thenReturn(EXAMENES);

        this.service.findExamenPorNombreConPreguntas("Matemáticas");
        this.service.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(examenRepository, preguntaRepository);
        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    void testNumeroInvocaciones() {
        when(examenRepository.findAll()).thenReturn(EXAMENES);
        this.service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMost(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumeroInvocaciones2() {
        when(examenRepository.findAll()).thenReturn(EXAMENES);
        this.service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntaRepository, times(2)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMost(2)).findPreguntasPorExamenId(5L);
        //verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumeroInvocaciones3() {
        when(examenRepository.findAll()).thenReturn(Collections.emptyList());
        this.service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntaRepository, never()).findPreguntasPorExamenId(5L);
        verifyNoInteractions(preguntaRepository);
    }




}