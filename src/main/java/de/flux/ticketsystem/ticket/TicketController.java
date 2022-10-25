package de.flux.ticketsystem.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@RepositoryRestController
public class TicketController {
  private final TicketRepository ticketRepo;
  private final RepositoryEntityLinks entityLinks;

  public TicketController(
      @Autowired TicketRepository ticketRepo,
      @Autowired RepositoryEntityLinks entityLinks) {
    this.ticketRepo = ticketRepo;
    this.entityLinks = entityLinks;
  }

  @RequestMapping(method = GET, value = "/tickets")
  public ResponseEntity<CollectionModel<EntityModel<Ticket>>> getRecentTickets(
      @RequestParam(required = false) final String filter,
      @RequestParam(required = false) final String title,
      @RequestParam(required = false) final Ticket.Status status,
      @RequestParam(required = false) final Ticket.Priority priority,
      Pageable page) {
    if (filter != null) {
      Optional<TicketsFilter> ticketsFilter = TicketsFilter.fromString(filter);
      if (ticketsFilter.isPresent() && ticketsFilter.get().equals(TicketsFilter.RECENT)) {
        Sort sortByCreatedAndModifiedDesc = Sort
            .by("lastModifiedDate")
            .and(Sort.by("createdDate").descending())
            .descending();
        page = PageRequest.of(page.getPageNumber(), page.getPageSize(), sortByCreatedAndModifiedDesc);
      }
    }
    List<Specification<Ticket>> ticketSpecs = new ArrayList<>();
    if (title != null) {
      if (title.contains("*")) {
        String filteredTitle = title.replaceAll("\\*", "%");
        ticketSpecs.add(TicketSpecifications.containsTitle(filteredTitle));
      } else {
        ticketSpecs.add(TicketSpecifications.hasTitle(title));
      }
    }
    if (status != null) {
      ticketSpecs.add(TicketSpecifications.hasStatus(status));
    }
    if (priority != null) {
      ticketSpecs.add(TicketSpecifications.hasPriority(priority));
    }

    Page<Ticket> ticketPage;
    if (!ticketSpecs.isEmpty()) {
      Specification<Ticket> specification = ticketSpecs.get(0);
      ticketSpecs.forEach(specification::and);
      ticketPage = ticketRepo.findAll(specification, page);
    } else {
      ticketPage = ticketRepo.findAll(page);
    }

    List<EntityModel<Ticket>> entityModels = ticketPage.getContent()
        .stream()
        .map(ticket -> EntityModel.of(
            ticket,
            entityLinks.linkToItemResource(Ticket.class, ticket.getId()),
            entityLinks.linkToItemResource(Ticket.class, ticket.getId()).withSelfRel()))
        .toList();

    PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
        ticketPage.getSize(), ticketPage.getNumber(), ticketPage.getTotalElements(), ticketPage.getTotalPages());

    return ResponseEntity.ok(PagedModel.of(entityModels, pageMetadata));
  }

  /**
   * Contains all filter possibilities supported by the '/tickets' endpoint.
   */
  enum TicketsFilter {
    RECENT("recent");     // return the most recent entries (either created or last modified)

    private final String text;
    private static final Map<String, TicketsFilter> stringToEnum = Arrays.stream(values()).collect(
        Collectors.toMap(Objects::toString, filter -> filter)
    );

    TicketsFilter(String text) {
      this.text = text;
    }

    /**
     * @param filter the text from which a {@link TicketsFilter} is created.
     * @return the corresponding {@link TicketsFilter} or nothing.
     */
    public static Optional<TicketsFilter> fromString(String filter) {
      return Optional.ofNullable(stringToEnum.get(filter));
    }

    @Override
    public String toString() {
      return text;
    }
  }
}

