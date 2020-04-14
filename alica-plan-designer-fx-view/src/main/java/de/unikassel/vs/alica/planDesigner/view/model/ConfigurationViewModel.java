package de.unikassel.vs.alica.planDesigner.view.model;

import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiModificationHandler;
import de.unikassel.vs.alica.planDesigner.view.Types;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Arrays;
import java.util.Map;

public class ConfigurationViewModel extends SerializableViewModel{
    protected final ObservableMap<String, String> parameters = FXCollections.observableHashMap();

    public ConfigurationViewModel(long id, String name) {
        super(id, name, Types.CONFIGURATION);

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment", "relativeDirectory"));

        this.parameters.put("","");
    }

    public void registerListener(IGuiModificationHandler handler) {
        super.registerListener(handler);
        // Note: Listener for configMap is added in ConfigurationsTab
    }

    public ObservableMap<String, String> getParameters() {
        return FXCollections.unmodifiableObservableMap(parameters);
    }

    /**
     * Used for any kind of modification of the parameters: Insert, Change, Remove, etc.
     *
     * Never insert/remove parameter with empty key - its a special parameter for entering new parameters in the UI.
     * @param newEntry
     * @param oldEntry
     */
    public void modifyParameter(Map.Entry<String, String> newEntry, Map.Entry<String, String> oldEntry) {
        // Insert new entry (no old entry)
        if (oldEntry == null) {
            if (newEntry.getKey() != "") {
                this.parameters.put(newEntry.getKey(), newEntry.getValue());
            }
            return;
        }

        // Remove parameter (no new entry)
        if (newEntry == null) {
            if (oldEntry.getKey() != "") {
                this.parameters.remove(oldEntry.getKey());
            }
            return;
        }

        // Modify existing parameter (old and new entry given)
        if (newEntry.getKey() == oldEntry.getKey()) {
            if (newEntry.getKey() != "") {
                this.parameters.put(newEntry.getKey(), newEntry.getValue());
            }
            return;
        } else {
            if (oldEntry.getKey() != "") {
                this.parameters.remove(oldEntry.getKey());
            }
            if (newEntry.getKey() != "") {
                this.parameters.put(newEntry.getKey(), newEntry.getValue());
            }
        }

        if (!this.parameters.containsKey("")) {
            this.parameters.put("","");
        }
    }
}

