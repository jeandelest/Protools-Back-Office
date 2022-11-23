package com.protools.flowableDemo.services.coleman.context;

import org.springframework.stereotype.Service;

@Service
public interface QuestionnaireCampaign {
    public abstract void createContext(QuestionnaireCampaignContext context) throws Exception;
}
