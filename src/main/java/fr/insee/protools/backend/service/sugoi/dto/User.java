package fr.insee.protools.backend.service.sugoi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class User implements Serializable {

  //private String lastName;
  //private String firstName;
  //private String mail;
  private String username;
  //private byte[] certificate;
  //private Organization organization;

  // Don't set a default empty list to distinguish case null (not provided) and
  // empty
  //private List<Group> groups;
  // Don't set a default empty list to distinguish case null (not provided) and
  // empty
  private List<Habilitation> habilitations;
  //private PostalAddress address;
  //private Map<String, Object> metadatas = new HashMap<>();
  //private Map<String, Object> attributes = new HashMap<>();


}