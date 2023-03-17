package fr.insee.protools.backend.service.platine.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MetadataDto {
    String Enq_LibelleEnquete;
    String Enq_ObjectifsCourts;
    String Enq_CaractereObligatoire;
    String Enq_NumeroVisa;
    String Enq_MinistereTutelle;
    String Enq_ParutionJo;
    String Enq_DateParutionJo;
    String Enq_RespOperationnel;
    String Enq_RespTraitement;
    String Enq_AnneeVisa;
    String Enq_QualiteStatistique;
    String Enq_TestNonLabellise;
    String Loi_statistique;
    String Loi_rgpd;
    String Loi_informatique;
}
