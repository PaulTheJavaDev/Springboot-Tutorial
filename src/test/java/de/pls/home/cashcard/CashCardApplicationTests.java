package de.pls.home.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Makes it able to perform Web-Tests

// Makes interference from other Tests, Starts every Test with a clean state, e.g., after creating a CashCard in the Creation test; it gets cleaned
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    private CashCardRepository cashCardRepository;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {

        final String cashCardsURL = "/cashcards/";
        final Long idToSearchFor = 99L;
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

        /*
         The test supposed to fail did not fail because I mistakenly set
         'idToLookAfter' to 'idToSearchFor + 1', a now local variable. This made it 100, an ID that actually exists and has a value.
         */

        final long idToLookAfter = Integer.MAX_VALUE;
        final String cashCardsURL = "/cashcards/";
        final String URLToSearchFor = cashCardsURL + idToLookAfter;

        ResponseEntity<String> response = restTemplate.getForEntity(URLToSearchFor, String.class);

        // Test: Should return an error, since there is no CashCard with that id
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Test: Makes sure, that the Body isn't blank. 😁👌
        assertThat(response.getBody()).isBlank();

    }

    @DirtiesContext
    @Test
    void shouldCreateANewCashCard() {

        // Is not null, so the Test fails
        final double cashCardTestAmount = 250.00;

        CashCard cashCard = new CashCard(null, cashCardTestAmount);

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

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {

        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);


        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);

        List<Integer> ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        List<Double> amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);

    }

    @Test
    void shouldReturnAPageOfCashCards() {

        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        List<?> page = documentContext.read("$[*]");

        assertThat(page).hasSize(1);


    }

    @Test
    void shouldReturnASortedPageOfCashCards() {

        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read).hasSize(1);

        double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(150.00);

    }

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {

        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page).hasSize(3);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
    }

}