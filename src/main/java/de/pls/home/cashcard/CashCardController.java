package de.pls.home.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * {@link CashCardController} is a REST API.<br>
 * This class listens for incoming HTTP Requests.<br>
 * <br>
 * When someone calls {@code /cashcards/123} the {@link CashCardController}:<br>
 * - Extracts the {@code 123} as {@code requestId}<br>
 * - Calls the {@link CashCardRepository} to find that {@link CashCard}<br>
 * - Returns a {@code 200 OK} if found, or {@code 404 Not Found} if it doesn't exist
 */
@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {

        this.cashCardRepository = cashCardRepository;

    }

    @GetMapping("/{requestedId}")
    ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {

        Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);

        if (cashCardOptional.isPresent()) {

            return ResponseEntity.ok(cashCardOptional.get());

        } else {

            return ResponseEntity.notFound().build();

        }

    }

}
