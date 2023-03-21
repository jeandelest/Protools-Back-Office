package fr.insee.protools.backend.service.nomenclature;

public interface NomenclatureService {

    /**
     * Returns the nomenclature associated with <nomenclatureId> stored at <folderPath></folderPath>
     * @param nomenclatureId
     * @param folderPath
     * @return the nomenclature content retrieved from external source
     */
    String getNomenclatureContent(String nomenclatureId,String folderPath);

}
