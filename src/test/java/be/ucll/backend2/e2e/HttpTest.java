package be.ucll.backend2.e2e;

import be.ucll.backend2.repository.ActorRepository;
import be.ucll.backend2.repository.DbInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Sql("classpath:schema.sql")
@ActiveProfiles("test")
public class HttpTest {
    @Autowired
    private WebTestClient client;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private DbInitializer dbInitializer;

    @BeforeEach
    public void setUp() {
        dbInitializer.initialize();
    }

    @Test
    public void given3ActorsInDb_whenInvokingGetActor_then3ActorsAreReturned() {
        client.get()
                .uri("/api/v1/actor")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json(
                        """
                        [
                          {
                            "id": 1,
                            "name": "Frances McDormand"
                          },
                          {
                            "id": 2,
                            "name": "Steve Buscemi"
                          },
                          {
                            "id": 3,
                            "name": "Woody Harrelson"
                          }
                        ]
                        """,
                        true
                );
    }

    @Test
    public void givenFrancesMcDormandInDb_whenInvokingGetActor1_thenFrancesMcDormandIsReturned() {
        client.get()
                .uri("/api/v1/actor/1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("""
                      {
                        "id": 1,
                        "name": "Frances McDormand"
                      }
                      """,
                    true);
    }

    @Test
    public void givenActorNotInDb_whenInvokingGetActor100_then404IsReturned() {
        client.get()
                .uri("/api/v1/actor/100")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .json("""
                      {
                        "message": "Actor not found for id: 100"
                      }
                      """,
                    true);
    }

    @Test
    public void givenNoActorJosBosmans_whenInvokingPostActor_thenActorJosBosmansIsSavedInDb() {
        client.post()
                .uri("/api/v1/actor")
                .header("Content-Type", "application/json")
                .bodyValue("""
                           {
                             "name": "Jos Bosmans"
                           }
                           """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .json("""
                      {
                        "name": "Jos Bosmans"
                      }
                      """);

        final var jos = actorRepository.findByName("Jos Bosmans");
        Assertions.assertTrue(jos.isPresent());
        Assertions.assertEquals("Jos Bosmans", jos.get().getName());
    }

    @Test
    public void givenActorFrancesMcDormand_whenInvokingDelete1_thenFrancesMcDormandIsRemovedFromDb() {
        client.delete()
                .uri("/api/v1/actor/1")
                .exchange()
                .expectStatus().is2xxSuccessful();

        Assertions.assertFalse(actorRepository.findById(1L).isPresent());
        Assertions.assertFalse(actorRepository.findByName("Frances McDormand").isPresent());
    }

    @Test
    public void givenActorFrancedMcDormand_whenInvokingPut1_thenActorIsUpdatedInDb() {
        client.put()
                .uri("/api/v1/actor/1")
                .header("Content-Type", "application/json")
                .bodyValue("""
                           {
                             "name": "Clement Peerens"
                           }
                           """)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("""
                      {
                        "name": "Clement Peerens"
                      }
                      """);

        final var clement = actorRepository.findById(1L);
        Assertions.assertTrue(clement.isPresent());
        Assertions.assertEquals("Clement Peerens", clement.get().getName());
    }

    @Test
    public void given2MoviesInDb_whenInvokingGetMovie_then2MoviesAreReturned() {
        client.get()
                .uri("/api/v1/movie")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("""
                      [
                        {
                          "id": 1,
                          "title": "Fargo",
                          "year": 1996,
                          "director": "Joel & Ethan Coen",
                          "actors": [
                            {
                              "id": 2,
                              "name": "Steve Buscemi"
                            },
                            {
                              "id": 1,
                              "name": "Frances McDormand"
                            }
                          ]
                        },
                        {
                          "id": 2,
                          "title": "Three Billboards Outside Ebbing, Missouri",
                          "year": 2017,
                          "director": "Martin McDonagh",
                          "actors": [
                            {
                              "id": 3,
                              "name": "Woody Harrelson"
                            },
                            {
                              "id": 1,
                              "name": "Frances McDormand"
                            }
                          ]
                        }
                      ]
                      """,
                    true);
    }

    @Test
    public void given2MoviesInDb_whenInvokingGetMoviesAfter2000_then1MovieIsReturned() {
        client.get()
                .uri("/api/v1/movie?startYear=2000")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("""
                      [
                        {
                          "id": 2,
                          "title": "Three Billboards Outside Ebbing, Missouri",
                          "year": 2017,
                          "director": "Martin McDonagh",
                          "actors": [
                            {
                              "id": 3,
                              "name": "Woody Harrelson"
                            },
                            {
                              "id": 1,
                              "name": "Frances McDormand"
                            }
                          ]
                        }
                      ]
                      """,
                      true);
    }

    @Test
    public void given2MoviesInDb_whenInvokingGetMoviesBefore2000_then1MovieIsReturned() {
        client.get()
                .uri("/api/v1/movie?endYear=2000")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("""
                      [
                        {
                          "id": 1,
                          "title": "Fargo",
                          "year": 1996,
                          "director": "Joel & Ethan Coen",
                          "actors": [
                            {
                              "id": 2,
                              "name": "Steve Buscemi"
                            },
                            {
                              "id": 1,
                              "name": "Frances McDormand"
                            }
                          ]
                        }
                      ]
                      """,
                      true);
    }

    @Test
    public void given2MoviesInDb_whenInvokingGetMoviesBetween2000And2010_thenNoMoviesAreReturned() {
        client.get()
                .uri("/api/v1/movie?startYear=2000&endYear=2010")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("[]", true);
    }
}
