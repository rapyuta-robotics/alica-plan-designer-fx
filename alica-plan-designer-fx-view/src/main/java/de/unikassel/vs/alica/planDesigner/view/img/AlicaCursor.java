package de.unikassel.vs.alica.planDesigner.view.img;

import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.filebrowser.FileTreeView;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;

public class AlicaCursor extends ImageCursor {

    public enum Type {
        //transitions
        transition,
        forbidden_transition,
        add_transition,
        bendpoint_transition,
        bendpoint_transition_delete,

        // state
        state,
        forbidden_state,
        add_state,

        //successstate
        successstate,
        forbidden_successstate,
        add_successstate,

        //failurestate
        failurestate,
        forbidden_failurestate,
        add_failurestate,

        //entrypoint
        entrypoint,
        forbidden_entrypoint,
        add_entrypoint,

        //behaviour
        behaviour,
        forbidden_behaviour,
        add_behaviour,

        //initstateconnection
        initstateconnection,
        forbidden_initstateconnection,
        add_initstateconnection,

        //synchronisation
        synchronisation,
        forbidden_synchronisation,
        add_synchronisation,

        //synctransition
        synctransition,
        forbidden_synctransition,
        add_synctransition,
        bendpoint_synctransition,

        //configuration
        configuration,
        forbidden_configuration,
        add_configuration,

        //plantypes
        tasks,
        plantype,
        masterplan,
        plan,

        //common
        add,
        forbidden,

        //folder
        folder,

        //default
        DEFAULT
    }

    /**
     * Cursor with specific hotspot
     * @param type
     * @param x
     * @param y
     */
    public AlicaCursor(Type type, int x, int y) {
        super(new AlicaIcon(type.name(), AlicaIcon.Size.SMALL), x, y);
    }
    public AlicaCursor(Type type) { super(new AlicaIcon(type.name(), AlicaIcon.Size.SMALL)); }
    public AlicaCursor(Type type, AlicaIcon.Size size) {
        super(new AlicaIcon(type.name(), size));
    }

    /**
     * This constructor only handles non-add/forbidden types of cursors
     */
    public AlicaCursor(String type, AlicaIcon.Size size, int x, int y) {
        super(new AlicaIcon(type, size), x, y);
    }

    /**
     * This constructor only handles non-add/forbidden types of cursors
     */
    public AlicaCursor(String type, AlicaIcon.Size size) {
        super(new AlicaIcon(type, size));
    }

    /**
     * This constructor only handles non-add/forbidden types of cursors
     */
    public AlicaCursor(String type) {
        super(new AlicaIcon(type, AlicaIcon.Size.SMALL));
    }
}
