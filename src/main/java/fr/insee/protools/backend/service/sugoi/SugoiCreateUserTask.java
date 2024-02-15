package fr.insee.protools.backend.service.sugoi;

import fr.insee.protools.backend.dto.sugoi.Habilitation;
import fr.insee.protools.backend.dto.sugoi.User;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.utils.password.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_DIRECTORYACCESS_ID_CONTACT;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_DIRECTORYACCESS_PWD_CONTACT;

@Slf4j
@Component
@RequiredArgsConstructor
public class SugoiCreateUserTask implements JavaDelegate, DelegateContextVerifier {

    protected static final Habilitation PLATINE_HABILITATION = new Habilitation("platine", "repondant", "");
    protected static final User createSugoiUserBody = User.builder().habilitations(List.of(PLATINE_HABILITATION)).build();

    private final SugoiService sugoiService;
    private final PasswordService passwordService;

    @Override
    public void execute(DelegateExecution execution) {
        log.debug("ProcessInstanceId={} begin", execution.getProcessInstanceId());

        //Create User
        User createdUser = sugoiService.postCreateUsers(createSugoiUserBody);
        //init password
        String userPassword = passwordService.generatePassword();
        sugoiService.postInitPassword(createdUser.getUsername(), userPassword);

        execution.setVariableLocal(VARNAME_DIRECTORYACCESS_ID_CONTACT, createdUser.getUsername());
        //TODO : find a way to not store password in protools variables
        execution.setVariableLocal(VARNAME_DIRECTORYACCESS_PWD_CONTACT, userPassword);

        log.info("ProcessInstanceId={} username={} end", execution.getProcessInstanceId(), createdUser.getUsername());
    }

}
