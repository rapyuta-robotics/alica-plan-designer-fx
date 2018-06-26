package de.uni_kassel.vs.cn.planDesigner.deserialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.uni_kassel.vs.cn.planDesigner.alicamodel.PlanElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FileArrayDeserializer extends StdDeserializer<ArrayList<PlanElement>> {

    public FileArrayDeserializer() {
        this(null);
    }

    public FileArrayDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ArrayList<PlanElement> deserialize(
            JsonParser jsonparser,
            DeserializationContext context)
            throws IOException, JsonProcessingException {
        TreeNode tree = jsonparser.getCodec().readTree(jsonparser);
        Iterator<String> iter = tree.fieldNames();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
        return new ArrayList<PlanElement>();
    }
}