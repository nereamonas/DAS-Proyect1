package com.example.das_proyect1.helpClass;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
/**
public class ExternalDB extends Worker {

    public ExternalDB(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    @NonNull
    @Override
    public Result doWork() {




        return null;
    }
**/
public class ExternalDB {

    public ExternalDB() {

    }


    public boolean crearUsuario(String usuario, String email, String pass){
        //Crearemos un nuevo usuario

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/crearUsuario.php";
        Object HttpURLConnectionurlConnection = null;
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("pass", pass);
                parametrosJSON.put("email", email);
                parametrosJSON.put("usuario", usuario);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            urlConnection.setRequestProperty("Content-Type","application/json");

            PrintWriter out= new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();

            int statusCode= urlConnection.getResponseCode();
            if(statusCode== 200){
                BufferedInputStream inputStream= new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while((line = bufferedReader.readLine()) != null){
                    result+= line;
                }
                inputStream.close();

                return true;
            }
        }catch(Exception e){

        }
        return false;
    }

    public Usuario comprobarUsuario(String usuario, String pass){
        //Comprobamos is existe un usuario con ese usuario y contraseña. si es asi devolvemos el usaurio


        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/login.php";
        Object HttpURLConnectionurlConnection = null;
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("pass", pass);
                parametrosJSON.put("usuario", usuario);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            urlConnection.setRequestProperty("Content-Type","application/json");

            PrintWriter out= new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();

            int statusCode= urlConnection.getResponseCode();


            if(statusCode== 200){
                BufferedInputStream inputStream= new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result="";
                while((line = bufferedReader.readLine()) != null){
                    result+= line;
                }
                inputStream.close();

                JSONParser parser= new JSONParser();
                JSONObject json= (JSONObject) parser.parse(result);
                Usuario u=null;
                try {
                    String userR= (String) json.get("user");
                    String emailR= (String)json.get("email");
                    String passR= (String) json.get("pass");
                    u = new Usuario(userR,emailR,passR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return u;
            }
        }catch(Exception e){

        }
    return null;
    }
}