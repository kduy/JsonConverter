package main.java.com.cloudrail.utils.Visitor;

import main.java.com.cloudrail.utils.Node.JsonArrayNode;
import main.java.com.cloudrail.utils.Node.JsonObjectNode;
import main.java.com.cloudrail.utils.Node.PrimaryNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Created by kidio on 17/11/15.
 */
public class PrintVistor implements Visitor {
    @Override
    public String visit(JsonArrayNode jsonArrayNode, int tagLevel) {
        String tags = repeat("\t",tagLevel);
        JSONArray jsonArray = jsonArrayNode.getJsonArray();

        StringBuilder builder = new StringBuilder("");
        builder.append(tags+"{\n");
        builder.append(tags+"\t\"type\": \"Array\",\n");
        builder.append(tags+"\t\"items\": [ \n");
        for (Object element : jsonArray) {
            builder.append(travel(element,tagLevel+2)+",");
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length()-2);
        builder.append(tags+"\t]\n");
        builder.append(tags+"}");

        return  builder.toString();
    }

    @Override
    public String visit(JsonObjectNode jsonObjectNode, int tagLevel) {
        String tags = repeat("\t",tagLevel);
        JSONObject jsonObject = jsonObjectNode.getJsonObject();
        StringBuilder builder = new StringBuilder("");

        Iterator<Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
        builder.append("{\n");

        // type
        builder.append(tags+"\t\"type\": \"Object\",\n");

        // required
        builder.append(tags+"\t\"required\": [\n");
        while (iterator.hasNext()){
            builder.append(tags+"\t\t\""+iterator.next().getKey()+"\",\n");
        }
        builder.deleteCharAt(builder.length()-2);
        builder.append(tags+"\t],\n");

        // properties
        builder.append(tags+"\t\"properties\":{\n");
        iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Object> temp = iterator.next();
            builder.append(tags+"\t\t\""+temp.getKey()+"\":");
            builder.append(travel(temp.getValue(),tagLevel+2)+",");
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length()-2);
        builder.append(tags+"\t}\n");

        builder.append(tags+"}");

        return  builder.toString();

    }

    private String travel(Object temp, int tagLevel) {
        if (temp instanceof JSONArray){
            return (new JsonArrayNode((JSONArray) temp)).accept(this, tagLevel);
        }else if (temp instanceof JSONObject){
            return (new JsonObjectNode((JSONObject)temp)).accept(this,tagLevel);
        }else {
            return (new PrimaryNode(temp.toString())).accept(this,tagLevel);
        }
    }

    @Override
    public String visit(PrimaryNode primaryNode, int tagLevel) {
        String tags = repeat("\t",tagLevel);
        String value = primaryNode.getValue();

        StringBuilder builder = new StringBuilder("");
        builder.append(tags+"{\n");
        builder.append(tags+"\t\"type\": \"String\",\n");
        builder.append(tags+"\t\"tags\": [\n");
        builder.append(tags+"\t\t\"<"+value+">\"\n");
        builder.append(tags+"\t]\n");
        builder.append(tags+"}");

        return  builder.toString();
    }

    private static String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }
}