package de.pls.home.cashcard;

import org.springframework.data.annotation.Id;

/**
 * CashCard is a Data Model, like a record in a Database.<br>
 * It has the following fields:
 * @param id Unique Identifier
 * @param amount Balance on card
 */
record CashCard(@Id Long id, Double amount) {}