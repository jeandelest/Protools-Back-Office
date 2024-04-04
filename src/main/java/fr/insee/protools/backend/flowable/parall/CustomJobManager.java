package fr.insee.protools.backend.flowable.parall;

import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.job.service.impl.asyncexecutor.DefaultJobManager;
import org.flowable.job.service.impl.persistence.entity.JobEntity;

public class CustomJobManager extends DefaultJobManager {
    public CustomJobManager(JobServiceConfiguration jobServiceConfiguration) {
       super(jobServiceConfiguration);
    }

    protected boolean isJobApplicableForExecutorExecution(JobEntity jobEntity) {
        return false;
    }
}
