package fr.insee.protools.backend.flowable.poc_utilisation_plusieurs_moteurs;

import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.spring.SpringProcessEngineConfiguration;

public class ProtoolsProcessEngineConfigImplTest extends SpringProcessEngineConfiguration {
    public ProtoolsProcessEngineConfigImplTest() {

    }

    @Override
    protected JobServiceConfiguration instantiateJobServiceConfiguration() {
        return new ProtoolsJobServiceConfiguration(ScopeTypes.BPMN);
    }
}
