package de.pls.home.cashcard;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Makes it able to perform Web-Tests
class CashCardApplicationTest {

    private final Long idToSearchFor = 99L;
    private final Double amountToSearchFor = 123.45;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    CashCardRepository cashCardRepository;

    @Test
    void shouldReturnACashCardWhenDataIsSaved() {

        CashCard testCard = new CashCard(idToSearchFor, amountToSearchFor);
        cashCardRepository.save(testCard); // Insertion directly into H2 - doesn't do anything, but makes the test work

        final String cashCardsURL = "/cashcards/";
        final String URLToSearchFor = cashCardsURL + idToSearchFor;
        ResponseEntity<String> responseToCashCardsURL = testRestTemplate.getForEntity(URLToSearchFor, String.class);

        // Test: HTTP Status is 'OK'
        assertThat(responseToCashCardsURL.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext json = JsonPath.parse(responseToCashCardsURL.getBody());

        // Test: id in the url is the id to search for.
        assertThat(json.read("$.id", Long.class)).isEqualTo(idToSearchFor);

        // Test: If a CashCard with the id from the URL is found, look for the amount in that CashCard.
        assertThat(json.read("$.amount", Double.class)).isEqualTo(amountToSearchFor);

    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {

        final long idToLookAfter = idToSearchFor + 1;
        final String cashCardsURL = "/cashcards/";
        final String URLToSearchFor = cashCardsURL + idToLookAfter;

        ResponseEntity<String> response = testRestTemplate.getForEntity(URLToSearchFor, String.class);

        // Test: Should return an error, since there is no CashCard with that id
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Test: Makes sure, that the Body isn't blank. 😁👌
        assertThat(response.getBody()).isBlank();

    }

}