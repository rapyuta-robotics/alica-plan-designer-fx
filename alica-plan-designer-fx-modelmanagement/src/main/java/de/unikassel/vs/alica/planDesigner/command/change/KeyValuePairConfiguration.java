package de.unikassel.vs.alica.planDesigner.command.change;

import de.unikassel.vs.alica.planDesigner.alicamodel.Configuration;
import de.unikassel.vs.alica.planDesigner.command.Command;
import de.unikassel.vs.alica.planDesigner.events.ModelEvent;
import de.unikassel.vs.alica.planDesigner.events.ModelEventType;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelModificationQuery;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Types;

public class KeyValuePairConfiguration extends Command {
    private final boolean add;
    private final Pair pair;
    private final Configuration configuration;

    public KeyValuePairConfiguration(ModelManager manager, ModelModificationQuery mmq, boolean add) {
        super(manager, mmq);

        this.add = add;
        this.pair = new Pair(mmq.getAttributeName(), mmq.getAttributeType());

        this.configuration = (Configuration) modelManager.getPlanElement(mmq.getElementId());
    }

    @Override
    public void doCommand() {
        if (add) {
            add();
        } else {
            remove();
        }
    }

    @Override
    public void undoCommand() {
        if (add) {
            remove();
        } else {
            add();
        }
    }

    private void add() {
        configuration.putKeyValuePair(pair.getKey(), pair.getValue());
        ModelEvent modelEvent = new ModelEvent(ModelEventType.ELEMENT_CONNECTED, configuration, Types.CONFIGURATION);
        modelEvent.setChangedAttribute(pair.getKey());
        modelEvent.setNewValue(pair.getValue());
        modelManager.fireEvent(modelEvent);
    }

    private void remove() {
        configuration.removeKeyValuePair(pair.getKey());
        ModelEvent modelEvent = new ModelEvent(ModelEventType.ELEMENT_DISCONNECTED, configuration, Types.CONFIGURATION);
        modelEvent.setChangedAttribute(pair.getKey());
        modelEvent.setNewValue(pair.getValue());
        modelManager.fireEvent(modelEvent);
    }

    private class Pair {
        private final String value;
        private final String key;

        private Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }
    }
}
