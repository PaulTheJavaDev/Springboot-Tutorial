package de.pls.home.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * This Interface, also known as a Data Access Layer, tells Spring Data to handle database operations for {@link CashCard}.<br>
 * It gets Methods like findById(), findAll(), save() and delete().
 * <br>
 * <br>
 * Pagination is the Process of turning a chunk of data in multiple pieces of more manageable data.
 */
interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {}