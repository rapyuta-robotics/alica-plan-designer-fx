package de.unikassel.vs.alica.planDesigner.modelMixIns;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.unikassel.vs.alica.planDesigner.alicamodel.AbstractPlan;
import de.unikassel.vs.alica.planDesigner.alicamodel.Configuration;
import de.unikassel.vs.alica.planDesigner.deserialization.ExternalRefDeserializer;
import de.unikassel.vs.alica.planDesigner.serialization.ExternalRefSerializer;

public class ConfAbstractPlanWrapperMixIn {
    @JsonSerialize(using = ExternalRefSerializer.class)
    @JsonDeserialize(using = ExternalRefDeserializer.class)
    protected AbstractPlan abstractPlan;

    @JsonSerialize(using = ExternalRefSerializer.class)
    @JsonDeserialize(using = ExternalRefDeserializer.class)
    protected Configuration configuration;
}
