package fr.insee.protools.backend.flowable.poc_utilisation_plusieurs_moteurs;

import org.flowable.job.service.JobServiceConfiguration;

public class ProtoolsJobServiceConfiguration extends JobServiceConfiguration {
    public ProtoolsJobServiceConfiguration(String engineName) {
        super(engineName);
    }

    public void initJobManager() {
        jobManager = new ProtoolsJobManager(this);
        jobManager.setJobServiceConfiguration(this);
    }
}
