package de.pls.home.cashcard;

import org.springframework.data.repository.CrudRepository;

/**
 * This Interface, also known as a Data Access Layer, tells Spring Data to handle database operations for {@link CashCard}.<br>
 * It gets Methods like findById(), findAll(), save() and delete().
 */
interface CashCardRepository extends CrudRepository<CashCard, Long> {}