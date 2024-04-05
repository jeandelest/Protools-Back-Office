package fr.insee.protools.backend.flowable.poc_utilisation_plusieurs_moteurs;

import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.job.service.impl.asyncexecutor.DefaultJobManager;
import org.flowable.job.service.impl.persistence.entity.JobEntity;

public class ProtoolsJobManager extends DefaultJobManager {
    public ProtoolsJobManager(JobServiceConfiguration jobServiceConfiguration) {
       super(jobServiceConfiguration);
    }
/*
    @Override
    protected boolean isJobApplicableForExecutorExecution(JobEntity jobEntity) {
        return false;
    }
    */

}
