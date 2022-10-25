package de.flux.ticketsystem.ticket;

import org.springframework.data.jpa.domain.Specification;

public class TicketSpecifications {

  public static Specification<Ticket> hasTitle(final String title) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("title"), title);
  }

  public static Specification<Ticket> containsTitle(final String title) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), title);
  }

  public static Specification<Ticket> hasPriority(final Ticket.Priority priority) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("priority"), priority);
  }

  public static Specification<Ticket> hasStatus(final Ticket.Status status) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
  }
}
