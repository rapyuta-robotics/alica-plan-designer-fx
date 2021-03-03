package de.unikassel.vs.alica.stdCheckPlugin;

import de.unikassel.vs.alica.generator.IPluginCodeGenerator;
import de.unikassel.vs.alica.generator.plugin.IPlugin;
import de.unikassel.vs.alica.planDesigner.modelmanagement.IConditionCreator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * This plugin is the Standard Checks implementation of {@link IPlugin}.
 * It contains a UI for choosing the corresponding standard check and
 * the {@link StdCheckPluginCodeGenerator} which generates the corresponding
 * checks in C++.
 */
public class StdCheckPlugin implements IPlugin<Void> {

    private static final String name = "StdCheckPlugin";

    private File pluginJar;
    private StdCheckPluginCodeGenerator stdCheckConstraintCodeGenerator;
    private StdCheckConditionCreator stdCheckConditionCreator;

    public StdCheckPlugin() {
        stdCheckConstraintCodeGenerator = new StdCheckPluginCodeGenerator();
        stdCheckConditionCreator = new StdCheckConditionCreator();
    }

    public IPluginCodeGenerator getPluginCodeGenerator() {
        return stdCheckConstraintCodeGenerator;
    }

    public IConditionCreator getConditionCreator() {
        return stdCheckConditionCreator;
    }

    public Parent getPluginUI() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("ui.fxml"));
        fxmlLoader.setController(new StdCheckPluginController());
        return fxmlLoader.load();
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
        stdCheckConstraintCodeGenerator.setProtectedRegions(protectedRegions);
    }
}
