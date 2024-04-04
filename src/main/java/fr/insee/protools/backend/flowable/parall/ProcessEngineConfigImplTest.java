package fr.insee.protools.backend.flowable.parall;

import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.spring.SpringProcessEngineConfiguration;

public class ProcessEngineConfigImplTest extends SpringProcessEngineConfiguration {
    public ProcessEngineConfigImplTest() {

    }

    @Override
    protected JobServiceConfiguration instantiateJobServiceConfiguration() {
        return new CustomJobServiceConfiguration(ScopeTypes.BPMN);
    }
}
