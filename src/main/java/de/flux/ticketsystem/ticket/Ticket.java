package de.flux.ticketsystem.ticket;


import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tickets")
public class Ticket {
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Setter(value = AccessLevel.NONE)
  private @Id long id;
  @Column(name = "title", nullable = false)
  private @NotBlank String title;
  @Column(name = "description")
  private String description;
  @Column(name = "priority", nullable = false)
  private @NotNull @Enumerated(EnumType.STRING) Priority priority;
  @Column(name = "status", nullable = false)
  private @NotNull @Enumerated(EnumType.STRING) Status status;
  @Column(name = "created_date", updatable = false, nullable = false)
  private @CreatedDate LocalDateTime createdDate;
  @Column(name = "last_modified_date", nullable = false)
  private @LastModifiedDate LocalDateTime lastModifiedDate;

  enum Priority {
    LOW,
    MEDIUM,
    HIGH,
  }

  enum Status {
    NEW,
    IN_PROGRESS,
    DONE
  }
}
