package de.unikassel.vs.alica.defaultPlugin;

import de.unikassel.vs.alica.generator.IPluginCodeGenerator;
import de.unikassel.vs.alica.generator.plugin.IPlugin;
import de.unikassel.vs.alica.planDesigner.modelmanagement.IConditionCreator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * This plugin is the default implementation of {@link IPlugin}.
 * It contains an empty UI and
 * the {@link DefaultPluginCodeGenerator} which generates NOOP Code (for own implementation)
 */
public class DefaultPlugin implements IPlugin<Void> {

    private static final String name = "DefaultPlugin";

    private File pluginJar;
    private DefaultPluginCodeGenerator defaultConstraintCodeGenerator;

    public DefaultPlugin() {
        defaultConstraintCodeGenerator = new DefaultPluginCodeGenerator();
    }

    public IPluginCodeGenerator getPluginCodeGenerator() {
        return defaultConstraintCodeGenerator;
    }

    public Parent getPluginUI() throws IOException {
        // Now interactive UI available, so there is no UI Controller instance set.
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("ui.fxml"));
        return fxmlLoader.load();
    }

    @Override
    public IConditionCreator getConditionCreator() {
        return new DefaultConditionCreator();
    }

    public String getName() {
        return name;
    }

    public void setPluginFile(File pluginJar) {
        this.pluginJar = pluginJar;
    }

    public File getPluginFile() {
        return pluginJar;
    }

    public void setProtectedRegions(Map<String, String> protectedRegions) {
        defaultConstraintCodeGenerator.setProtectedRegions(protectedRegions);
    }
}
