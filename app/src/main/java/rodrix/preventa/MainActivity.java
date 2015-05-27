package rodrix.preventa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//agregados por mi
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rodrix.preventa.library.DatabaseHandler;
import rodrix.preventa.library.JSONParser;
import rodrix.preventa.library.UserFunctions;
import android.database.sqlite.SQLiteDatabase;


public class MainActivity extends ActionBarActivity {

    EditText etProducto, etCantidad, etProducto2, etCantidad2;
    Button btAgregar, btInicio, btFin;

    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etProducto = (EditText) findViewById(R.id.etProducto);
        etCantidad = (EditText) findViewById(R.id.etCantidad);
        btAgregar = (Button) findViewById(R.id.btAgrear);

        etProducto2 = (EditText) findViewById(R.id.etProducto2);
        etCantidad2 = (EditText) findViewById(R.id.etCantidad2);
        btInicio = (Button) findViewById(R.id.btInicio);
        btFin = (Button) findViewById(R.id.btFin);


        btAgregar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if((!etProducto.getText().toString().equals("")) && (!etCantidad.getText().toString().equals(""))){
                    NetAsync(view);
                }else{
                    Toast.makeText(getApplicationContext(), "Debe ingresar valores", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler admin = new DatabaseHandler(getApplicationContext());
                SQLiteDatabase db = admin.getWritableDatabase();

                Cursor fila = db.rawQuery("SELECT * FROM registro ORDER BY producto ASC", null);
                if (fila.moveToFirst()) {
                    etProducto2.setText(fila.getString(0));
                    etCantidad2.setText(fila.getString(1));
                } else {
                    Toast.makeText(getApplicationContext(), "No hay registros", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void NetAsync(View view) {
        new NetCheck().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class NetCheck extends AsyncTask<String, String, Boolean> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setMessage("Cargando..");
            nDialog.setTitle("Verificando Conexion");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            //obtenemos estadp actual del dispositivo y verificamos conexion a internet
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()){
                try{
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200){
                        return true;
                    }
                }catch (MalformedURLException e1){
                    e1.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean th) {
            if (th == true){
                nDialog.dismiss();
                new ProcessAgregar().execute();
            }else{
                nDialog.dismiss();
                GuardarRegistro();


                /*
                //agrego datos a sqlite
                String producto = etProducto.getText().toString();
                String cantidad = etCantidad.getText().toString();
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                db.registro(producto, cantidad);*/

                Toast.makeText(getApplicationContext(), "Error de conexion. Datos guardados.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void GuardarRegistro() {
        String producto = etProducto.getText().toString();
        String cantidad = etCantidad.getText().toString();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db.registro(producto, cantidad);
    }


    private class ProcessAgregar extends AsyncTask<String, String, JSONObject>{

        private ProgressDialog pDialog;
        String pproducto, pcantidad;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            etProducto = (EditText) findViewById(R.id.etProducto);
            etCantidad = (EditText) findViewById(R.id.etCantidad);

            pproducto = etProducto.getText().toString();
            pcantidad = etCantidad.getText().toString();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Conectando a Servidor");
            pDialog.setMessage("Agregando..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.agregar(pproducto, pcantidad);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null){
                    String res = json.getString(KEY_SUCCESS);
                    String red = json.getString(KEY_ERROR);
                    if (Integer.parseInt(res) == 1){
                        pDialog.setTitle("Obteniendo Datos");
                        pDialog.setMessage("Cargando info");
                        Toast.makeText(getApplicationContext(),"Pedido Registrado", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }else{
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error al MYSQL", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "ERROR de APP", Toast.LENGTH_SHORT).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
