package be.ucll.backend2.integration;

import be.ucll.backend2.controller.ActorController;
import be.ucll.backend2.exception.ActorNotFoundException;
import be.ucll.backend2.service.ActorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(ActorController.class)
public class ActorControllerTest {

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private ActorService actorService;

    @Test
    public void givenActorWithIdExists_whenDeleteActorIsCalled_thenActorIsDeleted()
            throws ActorNotFoundException {
        client.delete()
                .uri("/api/v1/actors/{id}", 1L)
                .exchange()
                .expectStatus().isNoContent();

        Mockito.verify(actorService).deleteActor(1L);
    }

    @Test
    public void givenActorWithIdDoesNotExist_whenDeleteActorIsCalled_then404IsReturned() throws ActorNotFoundException {
        Mockito.doThrow(new ActorNotFoundException(1L)).when(actorService).deleteActor(1L);

        client.delete()
                .uri("/api/v1/actors/{id}", 1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().json("""
                                   {
                                     "message": "Actor not found for id: 1"
                                   }
                                   """,
                        JsonCompareMode.STRICT);
    }

}
