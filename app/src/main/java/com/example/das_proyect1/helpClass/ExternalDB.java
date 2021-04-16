package com.example.das_proyect1.helpClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
                String line="";
                String result="";
                while((line = bufferedReader.readLine()) != null){
                    result+= line;
                }
                inputStream.close();

                if (result.contains("Ha ocurrido")){
                    return false;
                }
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
                Log.d("Logs", "RESULT: "+result);

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

    public String getEjerciciosDeLaRutina(String rutina){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getEjerciciosDeLaRutina");
                parametrosJSON.put("rutina", rutina);
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
    public String getCorreoConUsuario(String usuario){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getCorreoConUsuario");
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

    public String getPassConUsuario(String usuario){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getPassConUsuario");
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

    public boolean eliminarRutinaDelUsuario(String usuario, String idRutina){
        //Comprobamos is existe un usuario con ese usuario y contraseña. si es asi devolvemos el usaurio


        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "eliminarRutinaDelUsuario");
                parametrosJSON.put("usuario", usuario);
                parametrosJSON.put("idRutina", idRutina);
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

                if (result.contains("Ha ocurrido al")){
                    return false;
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public String getNombreTodosLosEjercicios(String usuario){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getNombreTodosLosEjercicios");
                parametrosJSON.put("rutina", usuario);
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

    public boolean añadirRutina(String nombre){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "añadirRutina");
                parametrosJSON.put("nombre", nombre);
                parametrosJSON.put("foto", "todos");
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

                if (result.contains("Ha ocurrido al")){
                    return false;
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public String getIdRutina(String nombre){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getIdRutina");
                parametrosJSON.put("nombre", nombre);
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

                return result;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public boolean añadirUserRut(String idUser, String idRut){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "añadirUserRut");
                parametrosJSON.put("idUser", idUser);
                parametrosJSON.put("idRut", idRut);
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

                if (result.contains("Ha ocurrido al")){
                    return false;
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String getIdEjercicio(String nombre){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getIdEjercicio");
                parametrosJSON.put("nombre", nombre);
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

                return result;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public boolean añadirRutEjer(String idRut, String idEjer){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "añadirRutEjer");
                parametrosJSON.put("idRut", idRut);
                parametrosJSON.put("idEjer", idEjer);
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

                if (result.contains("Ha ocurrido al")){
                    return false;
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String getRutinaConId(String id){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getRutinaConId");
                parametrosJSON.put("id", id);
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

                return result;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }


    public boolean editarNombreDeUsuario(String userViejo, String userNuevo){
        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "editarNombreDeUsuario");
                parametrosJSON.put("userViejo", userViejo);
                parametrosJSON.put("userNuevo", userNuevo);
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

                if (result.contains("Ha ocurrido al")){
                    return false;
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean editarEmailDeUsuario(String user, String email){
        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "editarEmailDeUsuario");
                parametrosJSON.put("user", user);
                parametrosJSON.put("email", email);
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

                if (result.contains("Ha ocurrido al")){
                    return false;
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String getTodosLosEjercicios(String usuario){

        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "getTodosLosEjercicios");
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

    public boolean editarPassDeUsuario(String user, String pass){
        String direccion= "http://ec2-54-167-31-169.compute-1.amazonaws.com/nmonasterio003/WEB/consultas.php";
        try {
            URL destino = new URL(direccion);

            HttpURLConnection urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            JSONObject parametrosJSON= new JSONObject();
            try {
                parametrosJSON.put("tarea", "editarPassDeUsuario");
                parametrosJSON.put("user", user);
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

                if (result.contains("Ha ocurrido al")){
                    return false;
                }
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
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
            if (u!=null) {
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
            resultados = new Data.Builder()
                    .putString("rutinas",rutinas)
                    .build();
            return Result.success(resultados);
        }else if ("getEjerciciosDeLaRutina".equals(tarea)) {
            String rutina = getInputData().getString("rutina");
            String ejercicios=getEjerciciosDeLaRutina(rutina);
            resultados = new Data.Builder()
                    .putString("ejercicios",ejercicios)
                    .build();
            return Result.success(resultados);
        }else if ("getCorreoConUsuario".equals(tarea)) {
            String usuario = getInputData().getString("usuario");
            String email=getCorreoConUsuario(usuario);
            resultados = new Data.Builder()
                    .putString("email",email)
                    .build();
            return Result.success(resultados);
        }else if ("getPassConUsuario".equals(tarea)) {
            String usuario = getInputData().getString("usuario");
            String pass=getPassConUsuario(usuario);
            resultados = new Data.Builder()
                    .putString("pass",pass)
                    .build();
            return Result.success(resultados);
        }else if ("eliminarRutinaDelUsuario".equals(tarea)) {
            String usuario = getInputData().getString("usuario");
            String idRutina = getInputData().getString("idRutina");
            boolean result=eliminarRutinaDelUsuario(usuario,idRutina);
            resultados = new Data.Builder()
                    .putBoolean("resultado",result)
                    .build();
            return Result.success(resultados);
        }else if ("getNombreTodosLosEjercicios".equals(tarea)) {
            String usuario = getInputData().getString("usuario");
            String nombreEjercicios=getNombreTodosLosEjercicios(usuario);
            resultados = new Data.Builder()
                    .putString("nombreEjercicios",nombreEjercicios)
                    .build();
            return Result.success(resultados);
        }else if ("añadirRutina".equals(tarea)) {
            String nombre = getInputData().getString("nombre");
            boolean result=añadirRutina(nombre);
            if(result){
                String idRutina= getIdRutina(nombre);
                if(idRutina!=""){
                    String usuario = getInputData().getString("usuario");
                    result=añadirUserRut(usuario,idRutina);
                    resultados = new Data.Builder()
                            .putBoolean("resultado",result)
                            .putString("idRut",idRutina)
                            .build();
                    return Result.success(resultados);
                }
            }
            resultados = new Data.Builder()
                    .putBoolean("resultado",false)
                    .build();
            return Result.success(resultados);
        }else if ("añadirRutEjer".equals(tarea)) {
            String nombre = getInputData().getString("nombre");
            String idEjer= getIdEjercicio(nombre);
            if(idEjer!="") {
                String idRut = getInputData().getString("idRut");
                boolean result = añadirRutEjer(idRut, idEjer);
                resultados = new Data.Builder()
                        .putBoolean("resultado", result)
                        .build();
                return Result.success(resultados);
            }else{
                resultados = new Data.Builder()
                        .putBoolean("resultado", false)
                        .build();
                return Result.success(resultados);
            }
        }else if ("getRutinaConId".equals(tarea)) {
            String idRut = getInputData().getString("idRut");
            String rutina= getRutinaConId(idRut);
            resultados = new Data.Builder()
                        .putString("rutina", rutina)
                        .build();
            return Result.success(resultados);
        }else if ("getTodosLosEjercicios".equals(tarea)) {
            String usuario = getInputData().getString("usuario");
            String ejercicios=getTodosLosEjercicios(usuario);
            resultados = new Data.Builder()
                    .putString("ejercicios",ejercicios)
                    .build();
            return Result.success(resultados);
        }else if ("editarNombreDeUsuario".equals(tarea)) {
            String userViejo = getInputData().getString("userViejo");
            String userNuevo = getInputData().getString("userNuevo");
            resultado=editarNombreDeUsuario(userViejo, userNuevo);
            resultados = new Data.Builder()
                    .putBoolean("resultado",resultado)
                    .build();
            return Result.success(resultados);
        }else if ("editarEmailDeUsuario".equals(tarea)) {
            String user = getInputData().getString("user");
            String email = getInputData().getString("email");
            resultado=editarEmailDeUsuario(user, email);
            resultados = new Data.Builder()
                    .putBoolean("resultado",resultado)
                    .build();
            return Result.success(resultados);
        }else if ("editarPassDeUsuario".equals(tarea)) {
            String user = getInputData().getString("user");
            String pass = getInputData().getString("pass");
            resultado=editarPassDeUsuario(user, pass);
            resultados = new Data.Builder()
                    .putBoolean("resultado",resultado)
                    .build();
            return Result.success(resultados);
        }

        return null;
    }
}