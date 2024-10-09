package be.ucll.backend2.service;

import be.ucll.backend2.exception.ActorNotFoundException;
import be.ucll.backend2.repository.ActorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

// MockitoExtension: enables @Mock en @InjectMocks
@ExtendWith(MockitoExtension.class)
public class ActorServiceTest {
    // @Mock: zet mock op voor elke test
    @Mock
    private ActorRepository actorRepository;

    // @InjectMocks: maak een actorService voor elke test, voorzie een mock alle argumenten
    @InjectMocks
    private ActorService actorService;

    @Test
    public void givenNoActorExistsWithId_whenActorIsDeleted_thenActorNotFoundExceptionIsThrown() {
        // Mock existsById methode
        Mockito.when(actorRepository.existsById(1L)).thenReturn(false);
        // of:
        // Mockito.doReturn(false).when(actorRepository).existsById(1L);

        // Call de methode onder test (moet throwen)
        final var exception = Assertions.assertThrows(ActorNotFoundException.class, () -> actorService.deleteActor(1L));

        // Check of exception klopt
        Assertions.assertEquals("Actor not found for id: 1", exception.getMessage());
    }

    @Test
    public void givenActorExistsWithId_whenActorIsDeleted_thenActorIsDeleted() throws ActorNotFoundException {
        // Mock existsById methode
        Mockito.when(actorRepository.existsById(1L)).thenReturn(true);
        // of:
        // Mockito.doReturn(true).when(actorRepository).existsById(1L);

        // Call de methode onder test
        actorService.deleteActor(1L);

        // Kijk na of de deleteById methode gecalld is
        Mockito.verify(actorRepository).deleteById(1L);
    }

    // Oefening:
    // - Implementeer service test(s) voor getActorById
    // - Implementeer service test(s) voor createActor
    // - Implementeer service test(s) voor getAllActors
    // - Implementeer service test(s) voor updateActor
    // Let op: zorg ervoor dat elk geval getest is (happy cases en unhappy cases!)
}
