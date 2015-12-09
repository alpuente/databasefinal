/**
 * Created by alpue_000 on 12/1/2015.
 */
import java.util.ArrayList;
import org.json.JSONObject;

public class Document {
    public ArrayList<JSONObject> data;
    public String name;

    public Document(String name) {
        this.data = new ArrayList<JSONObject>();
        this.name = name;
    }
}