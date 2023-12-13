package fr.insee.protools.backend.service.sabiane.delegate;

import fr.insee.protools.backend.service.rem.dto.PhoneNumberDto;
import fr.insee.protools.backend.service.rem.dto.REMPhoneSource;
import fr.insee.protools.backend.service.sabiane.pilotage.dto.SabianePhoneNumberDto;
import fr.insee.protools.backend.service.sabiane.pilotage.dto.SabianeTitle;
import fr.insee.protools.backend.service.sabiane.pilotage.dto.Source;
import fr.insee.protools.backend.service.utils.TestWithContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SabianePilotageCreateSUTaskTest extends TestWithContext {


    @Test
    @DisplayName("convertREMGenderToSabianeCivilityTitle should return MISS when param '2' ; 'MISTER' when param is '1' and throw in other cases")
    void convertREMGenderToSabianeCivilityTitle_should_ReturnCorrectValues() {
        assertThat(SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("1")).isEqualTo(SabianeTitle.MISTER);
        assertThat(SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("1").getSabianeTitle()).isEqualTo("Mister");
        assertThat(SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("1").getFrenchCivility()).isEqualTo("M");

        assertThat(SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("2")).isEqualTo(SabianeTitle.MISS);
        assertThat(SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("2").getSabianeTitle()).isEqualTo("MISS");
        assertThat(SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("2").getFrenchCivility()).isEqualTo("MME");


        assertThrows(IllegalStateException.class, () -> SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle(" 2"));
        assertThrows(IllegalStateException.class, () -> SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle(" 2 "));
        assertThrows(IllegalStateException.class, () -> SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("3"));
        assertThrows(IllegalStateException.class, () -> SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("23JZKEOSJF"));
        assertThrows(IllegalStateException.class, () -> SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("0"));
        assertThrows(IllegalStateException.class, () -> SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("-1"));
        assertThrows(IllegalStateException.class, () -> SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("-2"));
        assertThrows(IllegalStateException.class, () -> SabianePilotageCreateSUTask.convertREMGenderToSabianeCivilityTitle("& ukdslw,kvlk,l"));
    }

    @Test
    void createSabianePhoneList_should_ReturnCorrectValues_2favorites_initial() {
        //Prepare
        List<PhoneNumberDto> remPhones1 =
                List.of(
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("01").favorite(Boolean.TRUE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("02").favorite(Boolean.TRUE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("03").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.DIRECTORY).number("04").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.DIRECTORY).number("05").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INTERVIEWER).number("06").favorite(Boolean.TRUE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INTERVIEWER).number("07").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INTERVIEWER).number("08").favorite(Boolean.TRUE).build());
        Set<SabianePhoneNumberDto> expectedSabiane1=
                Set.of(
                        SabianePhoneNumberDto.builder().source(Source.FISCAL).number("01").favorite(Boolean.TRUE).build(),
                        SabianePhoneNumberDto.builder().source(Source.DIRECTORY).number("02").favorite(Boolean.TRUE).build(),
                        SabianePhoneNumberDto.builder().source(Source.INTERVIEWER).number("06").favorite(Boolean.TRUE).build(),
                        SabianePhoneNumberDto.builder().source(Source.INTERVIEWER).number("07").favorite(Boolean.FALSE).build(),
                        SabianePhoneNumberDto.builder().source(Source.INTERVIEWER).number("08").favorite(Boolean.TRUE).build()
                        );
        //Execute the unit under test
        List<SabianePhoneNumberDto> res = SabianePilotageCreateSUTask.createSabianePhoneList(new ArrayList<>(remPhones1));
        //Post conditions
        assertEquals(expectedSabiane1.size(),res.size());
        assertTrue(res.containsAll(expectedSabiane1));

    }
    @Test
    void createSabianePhoneList_should_ReturnCorrectValues_noFavorite() {
        //Prepare
        List<PhoneNumberDto> remPhones1 =
                List.of(
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("01").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("02").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("03").favorite(Boolean.FALSE).build()
                        );
        Set<SabianePhoneNumberDto> expectedSabiane1=
                Set.of(
                        SabianePhoneNumberDto.builder().source(Source.FISCAL).number("01").favorite(Boolean.FALSE).build(),
                        SabianePhoneNumberDto.builder().source(Source.DIRECTORY).number("02").favorite(Boolean.FALSE).build()
                );
        //Execute the unit under test
        List<SabianePhoneNumberDto> res = SabianePilotageCreateSUTask.createSabianePhoneList(new ArrayList<>(remPhones1));
        //Post conditions
        assertEquals(expectedSabiane1.size(),res.size());
        assertTrue(res.containsAll(expectedSabiane1));
    }

    @Test
    void createSabianePhoneList_should_ReturnCorrectValues_1Favorite() {
        //Prepare
        List<PhoneNumberDto> remPhones1 =
                List.of(
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("01").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("02").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("03").favorite(Boolean.TRUE).build()
                );
        Set<SabianePhoneNumberDto> expectedSabiane1=
                Set.of(
                        SabianePhoneNumberDto.builder().source(Source.FISCAL).number("03").favorite(Boolean.TRUE).build(),
                        SabianePhoneNumberDto.builder().source(Source.DIRECTORY).number("01").favorite(Boolean.FALSE).build()
                );
        //Execute the unit under test
        List<SabianePhoneNumberDto> res = SabianePilotageCreateSUTask.createSabianePhoneList(new ArrayList<>(remPhones1));
        //Post conditions
        assertEquals(expectedSabiane1.size(),res.size());
        assertTrue(res.containsAll(expectedSabiane1));
    }

    @Test
    void createSabianePhoneList_should_ReturnCorrectValues_1InterviewerFavorite() {
        //Prepare
        List<PhoneNumberDto> remPhones1 =
                List.of(
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("01").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("02").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INITIAL).number("03").favorite(Boolean.FALSE).build(),
                        PhoneNumberDto.builder().source(REMPhoneSource.INTERVIEWER).number("04").favorite(Boolean.TRUE).build()
                );
        Set<SabianePhoneNumberDto> expectedSabiane1=
                Set.of(
                        SabianePhoneNumberDto.builder().source(Source.FISCAL).number("01").favorite(Boolean.FALSE).build(),
                        SabianePhoneNumberDto.builder().source(Source.DIRECTORY).number("02").favorite(Boolean.FALSE).build(),
                        SabianePhoneNumberDto.builder().source(Source.INTERVIEWER).number("04").favorite(Boolean.TRUE).build()

                );
        //Execute the unit under test
        List<SabianePhoneNumberDto> res = SabianePilotageCreateSUTask.createSabianePhoneList(new ArrayList<>(remPhones1));
        assertEquals(expectedSabiane1.size(),res.size());
        assertTrue(res.containsAll(expectedSabiane1));
    }
}