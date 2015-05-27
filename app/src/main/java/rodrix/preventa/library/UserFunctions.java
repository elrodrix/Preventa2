package rodrix.preventa.library;

/**
 * Created by usuario on 19/05/2015.
 */

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.Context;
import android.provider.Settings;

public class UserFunctions {
    private JSONParser jsonParser;

    //URL of the php API
    private static String agregarURL = "http://arstudiosfx.com/app4/";
    private static String agregar_tag = "agregar";

    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    //Funcion agregar

    public JSONObject agregar(String producto, String cantidad){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", agregar_tag));
        params.add(new BasicNameValuePair("producto", producto));
        params.add(new BasicNameValuePair("cantidad", cantidad));
        JSONObject json = jsonParser.getJSONFromUrl(agregarURL,params);
        return json;
    }

}
