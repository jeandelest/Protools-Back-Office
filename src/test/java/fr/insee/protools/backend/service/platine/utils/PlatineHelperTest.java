package fr.insee.protools.backend.service.platine.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlatineHelperTest {

    @Test
    void computePilotagePartitionID_should_returnConcat(){
        assertEquals("A1",PlatineHelper.computePilotagePartitionID("A",1l));
        assertEquals("AAAAAAAAA1",PlatineHelper.computePilotagePartitionID("AAAAAAAAA",1l));
        assertEquals(null,PlatineHelper.computePilotagePartitionID(null,1l));
        assertEquals(null,PlatineHelper.computePilotagePartitionID("1",null));
        assertEquals(null,PlatineHelper.computePilotagePartitionID(null,null));
    }
}