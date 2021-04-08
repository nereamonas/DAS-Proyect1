package com.example.das_proyect1.helpClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
public class ExternalDB extends Worker{

    public ExternalDB(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
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
                parametrosJSON.put("user", usuario);
                parametrosJSON.put("email", email);
                parametrosJSON.put("pass", pass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");

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
            e.printStackTrace();
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
                parametrosJSON.put("user", usuario);
                parametrosJSON.put("pass", pass);
            } catch (Exception e) {
                e.printStackTrace();
            }
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");

            PrintWriter out= new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();

            int statusCode= urlConnection.getResponseCode();


            if(statusCode== 200){
                BufferedInputStream inputStream= new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line="";
                String result="";
                while((line = bufferedReader.readLine()) != null){
                    result+= line;
                }
                inputStream.close();
                Usuario u=null;
                JSONParser parser= new JSONParser();
                JSONObject json =null;
                try {
                     json = (JSONObject) parser.parse(result);
                }catch (Exception e){
                    Log.d("Logs","1 "+e);
                }
                try {
                    String userR= (String) json.get("user");
                    String emailR= (String)json.get("email");
                    String passR= (String) json.get("pass");
                    u = new Usuario(userR,emailR,passR);
                }catch (Exception e){
                    Log.d("Logs","3 "+e);
                }


                return u;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    return null;
    }

    public String getRutinasDelUsuario(String usuario){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getRutinasDelUsuario");
                parametrosJSON.put("usuario", usuario);
            } catch (Exception e) {
                e.printStackTrace();
            }
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");

            PrintWriter out= new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();

            int statusCode= urlConnection.getResponseCode();
            if(statusCode== 200){
                BufferedInputStream inputStream= new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line="";
                String result="";
                while((line = bufferedReader.readLine()) != null){
                    result+= line;
                }
                inputStream.close();

                Log.d("Logs",result);
                return result;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Result doWork() {
        //IMPLEMENTAR LA TAREA
        String tarea= getInputData().getString("tarea");
        boolean resultado=false;
        Data resultados =null;
        if ("crearUsuario".equals(tarea)) {
            String user = getInputData().getString("user");
            String mail = getInputData().getString("mail");
            String pass = getInputData().getString("pass");
            resultado=crearUsuario(user, mail, pass);
            resultados = new Data.Builder()
                    .putBoolean("resultado",resultado)
                    .build();
            return Result.success(resultados);
        }else if ("comprobarUsuario".equals(tarea)) {
            String user = getInputData().getString("user");
            String pass = getInputData().getString("pass");
            Usuario u=comprobarUsuario(user, pass);
            //Map<String,Object> map=new HashMap();
            //Object object=(Object) u;
            //map.put("usuario",object);
            if (user!=null) {
                try {
                    resultados = new Data.Builder()
                            .putBoolean("resultado",true)
                            .putString("user", u.getUser())
                            .putString("pass",u.getPass())
                            .putString("email",u.getEmail())
                            //.putAll(map)
                            .build();
                } catch (Exception e) {
                    Log.d("Logs", "" + e);
                }
            }else{
                resultados = new Data.Builder()
                        .putBoolean("resultado",false)
                        .build();
            }
            return Result.success(resultados);
        }else if ("getRutinasDelUsuario".equals(tarea)) {
            String usuario = getInputData().getString("usuario");
            String rutinas=getRutinasDelUsuario(usuario);
            Log.d("Logs",rutinas);
            return Result.success(resultados);
        }

        return null;
    }
}