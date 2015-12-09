/**
 * Created by appleowner on 11/25/15.
 */
//import org.json.simple.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Scanner;
import javax.print.Doc;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentStore {

    public static ArrayList<Document> documents; // keep track of all the documents that have been made

    public static ArrayList fileToJSON (String file) {
        //System.out.println("herrree");
        ArrayList<JSONObject> objects = new ArrayList<JSONObject>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                // process the line.
                String[] items = line.split("\\s+");
                JSONObject obj = new JSONObject();

                for (int i = 0; i < items.length; i += 2) {
                    String id = items[i].substring(0, items[i].length()-1); // strip semicolon

                    String value = items[i+1];
                    try {
                        obj.put(id, value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println(obj.toString());
                objects.add(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
    }

    public static Document checkDocs(String name) {
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            if (doc.name.contentEquals(name)) {
                return doc;
            }
        }
        return null;
    }

    public static ArrayList subQuery(String[] condition, ArrayList<JSONObject> objects) {
        // condition should always be array of length 3
        ArrayList<JSONObject> result = new ArrayList<JSONObject>();
        if (condition[1].contentEquals("<")) {

            for (int i = 0; i < objects.size(); i++) {
                //System.out.println("oh");
                JSONObject currentObject = objects.get(i);
                try {
                    int x = Integer.parseInt(currentObject.getString(condition[0]));

                    if (Integer.parseInt(condition[2]) > currentObject.getInt(condition[0])) {
                        result.add(currentObject);
                    }
                } catch(JSONException e) {
                    //System.out.println("exceptional");
                }
            }

        } else if (condition[1].contentEquals(">")) {

            for (int i = 0; i < objects.size(); i++) {
                //System.out.println("oh");
                JSONObject currentObject = objects.get(i);
                try {
                    int x = Integer.parseInt(currentObject.getString(condition[0]));
                    //System.out.println("x: " + x);
                    //System.out.println("condition[2]: " + condition[2]);
                    if (Integer.parseInt(condition[2]) < currentObject.getInt(condition[0])) {
                        //System.out.println(currentObject.toString());
                        result.add(currentObject);
                    }
                } catch(JSONException e) {
                    //System.out.println("exceptional");
                }
            }

        } else if (condition[1].contentEquals("=")) {
            for (int i = 0; i < objects.size(); i++) {
                //System.out.println("oh");
                JSONObject currentObject = objects.get(i);
                try {
                    int x = Integer.parseInt(currentObject.getString(condition[0]));
                    //System.out.println("x: " + x);
                    //System.out.println("condition[2]: " + condition[2]);
                    if (Integer.parseInt(condition[2]) == currentObject.getInt(condition[0])) {
                        //System.out.println(currentObject.toString());
                        result.add(currentObject);
                    }
                } catch(JSONException e) {
                    //System.out.println("exceptional");
                }
            }
        } else {
            System.out.println("Wow that isn't a valid query operator. Cool.");
            return null;
        }
        return result;
    }

    public static void query(String condition, String returnVals, ArrayList<JSONObject> objects) {

        String[] conditionArgs = condition.split("&|and"); // split the entire set of conditions into individual conditional
        //System.out.println("2nd condition: " + conditionArgs[0]);
        //System.out.println("condition: " + condition);
        //System.out.println(conditionArgs.length);
        //System.out.println("r vals " + returnVals);
        ArrayList<String[]> conditions = new ArrayList<String[]>();
        for (int j = 0; j < conditionArgs.length; j++) {
            conditionArgs[j] = conditionArgs[j].trim();
            conditions.add(conditionArgs[j].split(" "));
        }
        ArrayList<JSONObject> result = new ArrayList<JSONObject>();
        if (conditionArgs.length >  1) {
            result = subQuery(conditions.get(0), objects);
            for (int i = 1; i < conditions.size(); i++) {
                result = subQuery(conditions.get(i), result);
            }
        } else {
            //System.out.println("condition length " + condition.split(" ").length);
            String[] qArgs = condition.split(" ");
            if (qArgs.length > 1) {
                result = subQuery(condition.split(" "), objects);
            } else {
                result = objects;
            }
        }

        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                //System.out.println("DLLDLDLLD");
                JSONObject jo = result.get(i);
                String queryOutput = "";
                if (!returnVals.contentEquals("")) {
                    String[] fields = returnVals.split("\\+");
                    for (int j = 0; j < fields.length; j++) {
                        try {
                            queryOutput += fields[j].trim() + ": " + jo.getString(fields[j].trim()) + " ";
                            //System.out.print(fields[j].trim() + " " + jo.getString(fields[j].trim() + " "));
                        } catch (JSONException je) {

                        }
                    }
                    System.out.println(queryOutput);
                    System.out.print("\n");
                } else {
                    System.out.println(jo.toString());
                }
            }
        }

    }

    public static void aggregate(String function, String param, ArrayList<JSONObject> objects) {
        int sum = 0;
        int count = 0;
        int average = 0;
        int max = 0;
        boolean noneFound = true;
        if (function.contentEquals("sum")) {
            for (int i = 0; i < objects.size(); i++) {
                JSONObject currentObject = objects.get(i);
                try {
                    int x = Integer.parseInt(currentObject.getString(param));
                    sum += x;
                } catch(JSONException e) {
                    //System.out.println("exceptional");
                }
            }
            System.out.println(sum);
        } else if (function.contentEquals("avg")) {

            for (int i = 0; i < objects.size(); i++) {
                //System.out.println("oh")
                JSONObject currentObject = objects.get(i);
                try {
                    int x = Integer.parseInt(currentObject.getString(param));
                    //System.out.println("x: " + x);
                    sum += x;
                    count += 1;
                    //System.out.println(x);
                    //System.out.println("condition[2]: " + condition[2]);
                } catch(JSONException e) {
                    //System.out.println("exceptional");
                }
            }
            if (count != 0) {
                average = sum / count;
                System.out.println("avg: " + average);
            }
        } else if (function.contentEquals("max")) {
            for (int i = 0; i < objects.size(); i++) {
                JSONObject currentObject = objects.get(i);
                try {
                    int x = Integer.parseInt(currentObject.getString(param));
                    //System.out.println("x: " + x);

                    if (x > max) {
                        max = x;
                    }
                    //System.out.println("condition[2]: " + condition[2]);
                } catch(JSONException e) {
                    //System.out.println("exceptional");
                }
            }
            System.out.println(max);
        } else {
            System.out.println("Not a valid aggregate function");
        }
    }

    public static void cartesianProduct(String param1, String param2, ArrayList<JSONObject> objects) {
        System.out.println(param1 + param2);
        param1 = param1.trim();
        param2 = param2.trim();
        for (int i = 0; i < objects.size(); i++) {
            JSONObject obj = objects.get(i);
            try {
                for (int j = 0; j < objects.size(); j++) {
                    String first = obj.getString(param1);
                    String out = param1 + ": " + first + " ";
                    JSONObject obj2 =  objects.get(j);
                    try {
                        String second = obj2.getString(param2);
                        out += param2 + ": " + second;
                        System.out.println(out);
                    } catch (JSONException ex) {

                    }
                }
            } catch (JSONException je) {

            }
        }
    }


    public static void main(String[] args){
        documents = new ArrayList<Document>();
        //JSONObject obj = new JSONObject();
        boolean shouldRun = true;
        Scanner input = new Scanner(System.in);
        while (shouldRun) {
            System.out.println("Enter a database command");
            String action = input.nextLine();
            // System.out.println("desired action " + action);
            if (action.contentEquals("exit")) {
                shouldRun = false;
            }  else {

                String[] command = action.split("\\.");

                ArrayList<JSONObject> objects;

                Document doc = checkDocs(command[1]);
                if (doc != null) {
                    objects = doc.data;
                } else {
                    String fname = command[1] + ".txt";
                    objects = fileToJSON(fname);
                }

                if (command[2].contains("query")) {
                    // db.CS457.query(Manager = 555, SNum+Dept)
                    String[] queryVals = command[2].split("\\(|\\)|,");
                    if (queryVals.length > 2) {
                        if (queryVals[1].contentEquals("")) {
                            query("", queryVals[2], objects);
                        } else {
                            query(queryVals[1], queryVals[2], objects);
                        }
                    } else {
                        query(queryVals[1], "", objects);
                    }
                } else if (command[2].contains("sum") || command[2].contains("avg") || command[2].contains("max")) {
                    String[] queryVals = command[2].split("\\(|\\)|,");
                    aggregate(queryVals[0], queryVals[1], objects);
                } else if (command[2].contains("cartprod")){
                    System.out.println("cartesian product");
                    String[] queryVals = command[2].split("\\(|\\)|,");
                    cartesianProduct(queryVals[1],queryVals[2], objects);
                } else {
                    System.out.println("Not a valid DB command");
                }
            }
        }

    }
}
