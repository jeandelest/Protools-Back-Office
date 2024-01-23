package fr.insee.protools.backend.dto.sugoi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class User implements Serializable {

    private String username;
    // Don't set a default empty list to distinguish case null (not provided) and
    // empty
    private List<Habilitation> habilitations;
}