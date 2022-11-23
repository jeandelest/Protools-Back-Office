package com.protools.flowableDemo.services.coleman.context;

import org.springframework.stereotype.Service;

@Service
public interface PilotageCampaign {
    public abstract void createContext(PilotageCampaignContext context) throws Exception;
}
