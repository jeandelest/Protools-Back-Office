package fr.insee.protools.backend.service.nomenclature;

public interface NomenclatureService {

    /**
     * Returns the nomenclature associated with nomenclatureId>
     * @param nomenclatureId
     * @return the nomenclature content retrieved from external source
     */
    String getNomenclatureContent(String nomenclatureId);


}
