package com.protools.flowableDemo.services.coleman.context.providers;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface NomenclatureValueProvider {
    public abstract Collection<?> getNomenclatureValue(String nomenclatureId);
}
