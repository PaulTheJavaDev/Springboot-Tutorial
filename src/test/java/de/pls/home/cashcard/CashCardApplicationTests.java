package de.pls.home.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Makes it able to perform Web-Tests
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {

    private final Long idToSearchFor = 99L;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CashCardRepository cashCardRepository;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {

        final String cashCardsURL = "/cashcards/";
        final String URLToSearchFor = cashCardsURL + idToSearchFor;
        ResponseEntity<String> responseToCashCardsURL = restTemplate.getForEntity(URLToSearchFor, String.class);

        // Test: HTTP Status is 'OK'
        assertThat(responseToCashCardsURL.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(responseToCashCardsURL.getBody());

        // Test: id in the url is the id to search for.
        assertThat(json.read("$.id", Long.class)).isEqualTo(idToSearchFor);

        // Test: If a CashCard with the id from the URL is found, look for the amount in that CashCard.
        final Double amountToSearchFor = 123.45;
        assertThat(json.read("$.amount", Double.class)).isEqualTo(amountToSearchFor);

    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {

        final long idToLookAfter = 346;
        final String cashCardsURL = "/cashcards/";
        final String URLToSearchFor = cashCardsURL + idToLookAfter;

        ResponseEntity<String> response = restTemplate.getForEntity(URLToSearchFor, String.class);

        // Test: Should return an error, since there is no CashCard with that id
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Test: Makes sure, that the Body isn't blank. 😁👌
        assertThat(response.getBody()).isBlank();

    }

    @Test
    void shouldCreateANewCashCard() {

        // Is not null, so the Test fails
        final Long cashCardTestId = null;
        final double cashCardTestAmount = 250.00;

        CashCard cashCard = new CashCard(cashCardTestId, cashCardTestAmount);

        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", cashCard, Void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();

        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(cashCardTestAmount);

    }

}

// Lehrer spezifische Rollen und Aufgaben, was darf ich Benutzer überhaupt sehen?
// Verständnis davon ein mal überarbeiten e.g wir nur eine Liste von Fächern zurückgegeben, wenn ja, was sind Fächer Enums, Strings?
// unregelmäßige Wochen, nicht immer derselbe Stundenplan