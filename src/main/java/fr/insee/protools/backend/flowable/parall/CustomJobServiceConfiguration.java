package fr.insee.protools.backend.flowable.parall;

import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.job.service.impl.asyncexecutor.DefaultJobManager;

public class CustomJobServiceConfiguration extends JobServiceConfiguration {
    public CustomJobServiceConfiguration(String engineName) {
        super(engineName);
    }

    public void initJobManager() {
        jobManager = new CustomJobManager(this);
        jobManager.setJobServiceConfiguration(this);
    }
}
