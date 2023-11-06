package fr.insee.protools.backend.service.sugoi.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class Habilitation implements Serializable {
  @Getter(AccessLevel.NONE)
  private String id;
  private String application;
  private String role;
  private String property;

  public Habilitation(String application, String role, String property) {
    this.application = application;
    this.role = role;
    this.property = property;
  }

  public String getId() {
    // raw id if specified, or build id from app role and property
    return id != null
        ? id
        : (this.property != null
            ? this.property + "_" + this.role + "_" + this.application
            : this.role + "_" + this.application);
  }
}