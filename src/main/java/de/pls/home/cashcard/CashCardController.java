package de.pls.home.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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

    public CashCardController(CashCardRepository cashCardRepository) {

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

    /**
     *
     * @param newCashCardRequest Body that contains the data submitted to the API.<br>
     *                           Spring automatically deserializes the data into a CashCard.
     */
    @PostMapping
    private ResponseEntity<Void> createCashCard(
            @RequestBody CashCard newCashCardRequest,
            UriComponentsBuilder ucb
    ) {

        final String path = "cashcards/{id}";

        // CThis stores a new CashCard and returns it with a generated ID.
        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);

        URI locationOfNewCashCard = ucb
                .path(path)
                .buildAndExpand(savedCashCard.id())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                ));
        return ResponseEntity.ok(page.getContent());
    }

}
