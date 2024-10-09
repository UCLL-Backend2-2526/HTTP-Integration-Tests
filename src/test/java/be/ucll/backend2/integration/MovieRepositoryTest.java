package be.ucll.backend2.integration;

import be.ucll.backend2.model.Movie;
import be.ucll.backend2.repository.MovieRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class MovieRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    public void givenThereAreMoviesAfter2000_whenFindByYearAfterIsCalled_thenMoviesAfter2000AreReturned() {
        entityManager.persist(
                new Movie("The Shawshank Redemption",
                        "Frank Darabont",
                        1994));
        entityManager.persist(
                new Movie("The Godfather",
                        "Francis Ford Coppola",
                        1972));
        entityManager.persist(
                new Movie("The Dark Knight",
                        "Christopher Nolan",
                        2008));
        entityManager.flush();

        final var movies = movieRepository.findByYearAfter(2000);

        Assertions.assertEquals(1, movies.size());
        Assertions.assertEquals("The Dark Knight", movies.getFirst().getTitle());
        Assertions.assertEquals("Christopher Nolan", movies.getFirst().getDirector());
        Assertions.assertEquals(2008, movies.getFirst().getYear());
    }

    @Test
    public void givenThereAreNoMoviesInDb_whenFindByYearAfterIsCalled_thenEmptyListIsReturned() {
        final var movies = movieRepository.findByYearAfter(2000);

        Assertions.assertTrue(movies.isEmpty());
    }
}
