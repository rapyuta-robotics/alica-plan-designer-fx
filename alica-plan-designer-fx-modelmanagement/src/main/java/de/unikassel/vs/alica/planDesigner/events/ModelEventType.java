package de.unikassel.vs.alica.planDesigner.events;

public enum ModelEventType {
    FOLDER_DELETED,
    ELEMENT_CREATED,
    ELEMENT_CREATED_AND_ADDED,
    ELEMENT_ADDED,
    ELEMENT_REMOVED,
    ELEMENT_REMOVED_AND_DELETED,
    ELEMENT_DELETED,
    ELEMENT_PARSED,
    ELEMENT_SERIALIZED,
    ELEMENT_CONNECTED,
    ELEMENT_DISCONNECTED,
    ELEMENT_CHANGED_POSITION,
    ELEMENT_ATTRIBUTE_CHANGED,
}
