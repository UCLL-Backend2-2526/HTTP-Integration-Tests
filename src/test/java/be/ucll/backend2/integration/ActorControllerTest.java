package be.ucll.backend2.integration;

import be.ucll.backend2.controller.ActorController;
import be.ucll.backend2.exception.ActorNotFoundException;
import be.ucll.backend2.service.ActorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(ActorController.class)
@ActiveProfiles("test")
public class ActorControllerTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private ActorService actorService;

    @Test
    public void givenActorWithIdExists_whenDeleteActorIsCalled_thenActorIsDeleted()
            throws ActorNotFoundException {
        client.delete()
                .uri("/api/v1/actor/{id}", 1L)
                .exchange()
                .expectStatus().isNoContent();

        Mockito.verify(actorService).deleteActor(1L);
    }

    @Test
    public void givenActorWithIdDoesNotExist_whenDeleteActorIsCalled_then404IsReturned() throws ActorNotFoundException {
        Mockito.doThrow(new ActorNotFoundException(1L)).when(actorService).deleteActor(1L);

        client.delete()
                .uri("/api/v1/actor/{id}", 1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().json("""
                                   {
                                     "message": "Actor not found for id: 1"
                                   }
                                   """,
                        true);
    }

    // Oefening:
    // - Implementeer controller test(s) voor GET /api/v1/actor
    // - Implementeer controller test(s) voor GET /api/v1/actor/{id}
    // - Implementeer controller test(s) voor POST /api/v1/actor
    // - Implementeer controller test(s) voor PUT /api/v1/actor/{id}
    // Let op: zorg ervoor dat elk geval getest is (happy cases en unhappy cases!)
}
