package fr.insee.protools.backend.service.utils.data;

public class CtxExamples {

    public static final Long ctx_partition1 = 99L;

    //CTX
    public static final String ctx_no_part =
            """
                {
                  "id": "AAC2023A00"
                }
            """;

    public static final String ctx_idCampagne_idPartition =
            """
                {
                  "id": "AAC2023A00",
                  "partitions": [{
                    "id": 99
                  }]
                }
            """;

    public static final String ctx_idCampagne_1emptyPartition =
            """
                {
                  "id": "AAC2023A00",
                  "partitions": [{
                  }]
                }
            """;

    public static final String ctx_idCampagne_idPartition_typeLogement =
            """
                {
                  "id": "AAC2023A00",
                  "partitions": [{
                    "id": 99,
                    "typeEchantillon" : "LOGEMENT"
                  }]
                }
            """;

    public static final String ctx_idCampagne_idPartition_typeIndividu =
            """
                {
                  "id": "AAC2023A00",
                  "partitions": [{
                    "id": 99,
                    "typeEchantillon" : "individu"
                  }]
                }
            """;
    private CtxExamples(){}
}
