package de.unikassel.vs.alica.stdCheckPlugin;

import de.unikassel.vs.alica.generator.IConstraintCodeGenerator;
import de.unikassel.vs.alica.generator.plugin.IPlugin;
import de.unikassel.vs.alica.planDesigner.alicamodel.Condition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * This plugin is the default implementation of {@link IPlugin}.
 * It contains an empty UI and
 * the {@link StdCheckConstraintCodeGenerator} which generates NOOP Code (for own implementation)
 */
public class StdCheckPlugin implements IPlugin<Void> {

    private static final String name = "StdCheckPlugin";

    private File pluginJar;
    private StdCheckConstraintCodeGenerator stdCheckConstraintCodeGenerator;

    public StdCheckPlugin() {
        stdCheckConstraintCodeGenerator = new StdCheckConstraintCodeGenerator();
    }

    public IConstraintCodeGenerator getConstraintCodeGenerator() {
        return stdCheckConstraintCodeGenerator;
    }

    public Parent getPluginUI() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getClassLoader().getResource("ui.fxml"));
        fxmlLoader.setController(new StdCheckPluginController());
//        try {
            return fxmlLoader.load();
//        } catch (IOException e) {
//            ErrorWindowController.createErrorWindow(I18NRepo.getInstance().getString("label.error.plugin.missingui"), e);
//        }
//        return null;
    }

    public void writePluginValuesToCondition(Condition condition) {
        // default doesn't need any implementation here
    }

    public Void readPluginValuesFromCondition(Condition condition) {
        // default doesn't need any implementation here
        return null;
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
