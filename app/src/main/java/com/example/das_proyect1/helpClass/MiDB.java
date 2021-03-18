package com.example.das_proyect1.helpClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.Rutina;
import com.example.das_proyect1.helpClass.Usuario;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MiDB extends SQLiteOpenHelper {
    //Crearemos la conexion con la base de datos y todos los insert, update...

    private static final String DB_NAME = "db.sqlite";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;


    public MiDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db=this.getWritableDatabase();
    }
    public void cerrarConexion(){
        db.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Crear tablas cuando se cree por primera vez la base de datos
        db.execSQL("CREATE TABLE usuario( user TEXT NOT NULL UNIQUE, email TEXT, pass TEXT)");
        db.execSQL("CREATE TABLE rutina( id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, foto TEXT)");
        db.execSQL("CREATE TABLE ejercicio( id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, descripcion TEXT, foto TEXT, duracion TEXT)");
        db.execSQL("CREATE TABLE rutEjer( idRut INTEGER, idEjer INTEGER, FOREIGN KEY(idRut) REFERENCES rutina(id),FOREIGN KEY(idEjer) REFERENCES ejercicio(id) )");
        db.execSQL("CREATE TABLE userRut( idUser TEXT, idRut INTEGER, FOREIGN KEY(idUser) REFERENCES usuario(user),FOREIGN KEY(idRut) REFERENCES rutina(id) )");

        //añadirPrimerosElementos();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void añadirPrimerosElementos(){
        //Añadiremos los primeros elementos.
        Cursor c = db.rawQuery("select * from rutina",null);
        if(c==null || c.getCount()==0){
            //Añadir elementos a la base de datos
            añadirRutina("Todos","todos");
            añadirRutina("Ejercicios de brazo","rutinaEjerBrazo");
            añadirRutina("Estiramientos","rutinaEstiramiento");
            añadirRutina("Ejercicios abdominales","rutinaEjerAbdominal");
            añadirRutina("Ejercicios de pierna","rutinaEstiramiento");

            añadirEjercicio(1,"DeadBug","Levantar la pierna a 90º y el brazo contrario hasta la cabeza.Ir alternando las dos piernas","deadBug","60000");
            añadirEjercicio(2,"Plancha","Manten la siguiente posición","plancha","45000");
            añadirEjercicio(3,"Plancha con flexión","Dobla y estira alternativamente los brazos","planchaFlexion","45000");
            añadirEjercicio(4,"Giros Rusos","Haz el movimiento como se indica en la foto","giroRuso","45000");
            añadirEjercicio(5,"Bicycle Crunch","Haz el movimiento como se indica en la foto","bicycleCrunch","45000");
            añadirEjercicio(6,"One Arm Toe Touch Crunch","Haz el movimiento como se indica en la foto","oneArmToeTouchCrunch","45000");
            añadirEjercicio(7,"Mountain Climber","Haz el movimiento como se indica en la foto","mountainClimber","45000");
            añadirEjercicio(8,"Reverse Crunch","Haz el movimiento como se indica en la foto","reverseCrunch","45000");
            añadirEjercicio(9,"Sicsors Abs","Haz el movimiento como se indica en la foto","sicsorsAbs","45000");
            añadirEjercicio(10,"Rolling Plank","Haz el movimiento como se indica en la foto","rollingPlank","45000");
         

            añadirRutEjer(1,1);
            añadirRutEjer(1,2);
            añadirRutEjer(1,3);
            añadirRutEjer(1,4);
            añadirRutEjer(1,5);
            añadirRutEjer(1,6);
            añadirRutEjer(1,7);
            añadirRutEjer(1,8);
            añadirRutEjer(1,9);
            añadirRutEjer(1,10);
            añadirRutEjer(2,7);
            añadirRutEjer(2,3);
            añadirRutEjer(2,6);
            añadirRutEjer(2,8);
            añadirRutEjer(3,3);
            añadirRutEjer(3,9);
            añadirRutEjer(3,10);
            añadirRutEjer(3,7);
            añadirRutEjer(4,1);
            añadirRutEjer(4,2);
            añadirRutEjer(4,3);
            añadirRutEjer(4,4);
            añadirRutEjer(5,5);
            añadirRutEjer(5,1);
            añadirRutEjer(5,8);
            añadirRutEjer(5,3);
            añadirRutEjer(5,9);
        }

    }

    public boolean añadirRutina(String nombre, String foto){
        //Añadiremos una nueva rutina a la base de datos
        ContentValues cv = new ContentValues();
        //cv.put("id", id);
        Log.d("Logs","Añadir rutina "+nombre);
        cv.put("nombre", nombre);
        cv.put("foto", foto);
        Log.d("Logs","  "+nombre+"  "+foto);
        int resultado = (int) db.insert("rutina", null, cv);
        Log.d("Logs"," "+resultado);
        if(resultado== -1) {
            return false;
        }else{
            return true;
        }
    }

    public boolean añadirEjercicio(int id, String nombre, String descripcion, String foto, String duracion){
        //Añadiremos un nuevo ejercicio a la bbdd
        ContentValues cv = new ContentValues();
        //cv.put("id", id);
        cv.put("nombre", nombre);
        cv.put("descripcion", descripcion);
        cv.put("foto", foto);
        cv.put("duracion", duracion);
        int resultado = (int) db.insert("ejercicio", null, cv);
        Log.d("Logs"," "+resultado);
        if(resultado== -1) {
            return false;
        }else{
            return true;
        }
    }

    public boolean añadirUserRut(String idUser, int idRut){
        //Añadimos a la clase de conexion entre usuario y rutina
        ContentValues cv = new ContentValues();
        cv.put("idUser", idUser);
        cv.put("idRut", idRut);
        int resultado = (int) db.insert("userRut", null, cv);
        Log.d("Logs"," "+resultado);
        if(resultado== -1) {
            return false;
        }else{
            return true;
        }
    }

    public boolean añadirRutEjer(int idRut, int idEjer){
        //Añadimos a la clase de conexion entre rutina y ejercicio
        ContentValues cv = new ContentValues();
        cv.put("idRut", idRut);
        cv.put("idEjer", idEjer);
        int resultado = (int) db.insert("rutEjer", null, cv);
        Log.d("Logs"," "+resultado);
        if(resultado== -1) {
            return false;
        }else{
            return true;
        }
    }


    public boolean crearUsuario(String usuario, String email, String pass){
        //Crearemos un nuevo usuario
        boolean todoCorrecto=true;
        ContentValues cv = new ContentValues();
        cv.put("user", usuario);
        cv.put("email", email);
        cv.put("pass", pass);
        int resultado = (int) db.insert("usuario", null, cv);
        Log.d("Logs"," "+resultado);
        if(resultado== -1) {
            todoCorrecto=false;
        }

        //Al crear un usuario, tambien hay que asignarle las rutinas:
        añadirUserRut(usuario,1);
        añadirUserRut(usuario,2);
        añadirUserRut(usuario,3);
        añadirUserRut(usuario,4);
        añadirUserRut(usuario,5);

        return todoCorrecto;
    }

    public Usuario comprobarUsuario(String usuario, String pass){
        //Comprobamos is existe un usuario con ese usuario y contraseña. si es asi devolvemos el usaurio
        Usuario u=null;
        String select="select * from usuario where user='"+usuario+"' and pass='"+pass+"'";
        Cursor c = db.rawQuery(select,null);
        Log.d("Logs"," "+c.toString());
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            Log.d("Logs"," "+c.getString(c.getColumnIndex("user")));
            u = new Usuario(c.getString(c.getColumnIndex("user")),c.getString(c.getColumnIndex("email")),c.getString(c.getColumnIndex("pass")));
        }
        return u;
    }

    public ArrayList<Ejercicio> getEjerciciosDeLaRutina(int rutina){
        //devolvemos en un array list todos los ejercicios que pertenecen a esa rutina
        ArrayList<Ejercicio> lista= new ArrayList<Ejercicio>();
        String select="select * from ejercicio e inner join rutejer r on r.idEjer=e.id where r.idRut="+rutina;
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            do {
                int id=c.getInt(c.getColumnIndex("id"));
                String nombre=c.getString(c.getColumnIndex("nombre"));
                String descripcion=c.getString(c.getColumnIndex("descripcion"));
                String foto=c.getString(c.getColumnIndex("foto"));
                String duracion=c.getString(c.getColumnIndex("duracion"));
                Ejercicio e = new Ejercicio(id,nombre,descripcion,foto,duracion); //Cogemos todos los datos y creamos un ejercicio
                lista.add(e); //Añadimos a la lista ese ejercicio
            }while(c.moveToNext());
        }
        return lista;
    }

    public ArrayList<Rutina> getRutinasDelUsuario(String usuario) {
        //devolvemos en un arraylist de rutinas todas las rutinas que pertenecen a ese usuario
        ArrayList<Rutina> lista = new ArrayList<Rutina>();
        String select = "select * from Rutina r inner join userRut u on u.idRut=r.id where u.idUser='" + usuario + "'";
        Cursor c = db.rawQuery(select, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                int id = c.getInt(c.getColumnIndex("id"));
                String nombre = c.getString(c.getColumnIndex("nombre"));
                String foto = c.getString(c.getColumnIndex("foto"));
                Rutina r = new Rutina(id, nombre, foto); //Cogemos todos los elementos y con ellos creamos una rutina
                lista.add(r); //Añadimos a la lista la rutina
            } while (c.moveToNext());
        }
        return lista;
    }

    public ArrayList<Ejercicio> getTodosLosEjercicios(String usuario){
        //Devolvemos todos los ejercicios que pertenecen a un usuario en un array list de ejercicios
        ArrayList<Ejercicio> lista= new ArrayList<Ejercicio>();
        String select="select distinct e.id,e.nombre,e.descripcion,e.foto,e.duracion from ejercicio e inner join rutEjer r on r.idEjer=e.id inner join userRut u on u.idRut=r.idRut where u.idUser='"+usuario+"'";
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            do {
                int id=c.getInt(c.getColumnIndex("id"));
                String nombre=c.getString(c.getColumnIndex("nombre"));
                String descripcion=c.getString(c.getColumnIndex("descripcion"));
                String foto=c.getString(c.getColumnIndex("foto"));
                String duracion=c.getString(c.getColumnIndex("duracion"));
                Ejercicio e = new Ejercicio(id,nombre,descripcion,foto,duracion);
                lista.add(e);
            }while(c.moveToNext());
        }
        return lista;
    }

    public String getCorreoConUsuario(String usuario){
        //Pasando el usuario, vamos a coger el correo que le pertenece a dicho usuario
        String correo="";
        String select="select email from usuario where user='"+usuario+"'";
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            correo = c.getString(c.getColumnIndex("email"));
        }
        return correo;
    }

    public String getPassConUsuario(String usuario){
        //Pasando el usuario vamos a coger la contraseña que le pertenece. no hay mucha seguridad pero bnuenoa ajjajajja
        String correo="";
        String select="select pass from usuario where user='"+usuario+"'";
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            correo = c.getString(c.getColumnIndex("pass"));
        }
        return correo;
    }


    public boolean editarNombreDeUsuario(String userViejo, String userNuevo) {
        //Cambiaremos el nom bre de usuario
        //Comprobar si existe el usuario nuevo:
        String select="select user from usuario where user='"+userNuevo+"'";
        Cursor c = db.rawQuery(select,null);
        String existe="";
        boolean todobien=false;
        if(c!=null && c.getCount()>0) {//Ya esta en uso ese usuario por lo q no se va a poder crear
            Log.d("Logs","ya está en uso el usuario "+c);
        }else{//El usuario no existe, asiq modificamos su valor
            try{
                db.execSQL("UPDATE usuario SET user='"+userNuevo+"' WHERE user='"+userViejo+"'");
                todobien=true;
            }catch (Exception e){
                Log.d("Logs","ERROR AL ACTUALIZAR USUARIO");
            }
        }
        return todobien;
    }

    public boolean editarEmailDeUsuario(String user, String email) {
        //Editamos el mail del usuario. y devolvemos si se ha ejecutado bien o no
        boolean bien=false;
        try{
            db.execSQL("UPDATE usuario SET email='"+email+"' WHERE user='"+user+"'");
            bien=true;
        }catch(Exception e){
            Log.d("Logs","ERROR AL ACTUALIZAR EMAIL");
        }
        return bien;
    }
    public boolean editarPassDeUsuario(String user, String pass) {
        //Editamos la contraseña del usuario y devolvemos si se ha ejecutado bnien o no
        boolean bien=false;
        try{
            db.execSQL("UPDATE usuario SET pass='"+pass+"' WHERE user='"+user+"'");
            bien=true;
        }catch(Exception e){
            Log.d("Logs","ERROR AL ACTUALIZAR CONTRASEÑA");
        }
        return bien;
    }

    public String getNombreRutina(int id){
        //Cogemos el nombre de la rutina
        String nombre="";
        String select="select nombre from Rutina where id="+id;
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            nombre = c.getString(c.getColumnIndex("nombre"));
        }
        return nombre;
    }

    public void eliminarRutinaDelUsuario(String usuario, int idRutina){
        try{
            db.execSQL("DELETE from rutEjer where idRut="+idRutina);
        }catch(Exception e){
            Log.d("Logs","ERROR AL eliminar rutejer");
        }
        //Luego eliminamos la rutina
        try{
            db.execSQL("DELETE from userRut where idUser='"+usuario+"' and idRut="+idRutina);
        }catch(Exception e){
            Log.d("Logs","ERROR AL eliminar userrut");
        }
    }


    public ArrayList<String> getNombreTodosLosEjercicios(String usuario){
        //Devolvemos todos los ejercicios que pertenecen a un usuario en un array list de ejercicios
        ArrayList<String> lista= new ArrayList<String>();
        String select="select distinct e.id,e.nombre,e.descripcion,e.foto,e.duracion from ejercicio e inner join rutEjer r on r.idEjer=e.id inner join userRut u on u.idRut=r.idRut where u.idUser='"+usuario+"'";
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            do {
                String nombre=c.getString(c.getColumnIndex("nombre"));
                lista.add(nombre);
            }while(c.moveToNext());
        }
        return lista;
    }

    public void añadirRutinaConEjerciciosAlUsuario(String usuario, String nombre, ArrayList<String> ejercicios){
        añadirRutina(nombre,"todos");
        int idRutina=getIdRutina(nombre);
        añadirUserRut(usuario,idRutina);
        for (int i=0 ; i<ejercicios.size() ; i++) {
            int idEjercicio=getIdEjercicio(ejercicios.get(i));
            Log.d("Logs","AÑADIENDO A LA BD: idRutina: "+idRutina+  "  nombre: "+ejercicios.get(i)+ "  idEjercicio: "+ idEjercicio);
            if (idEjercicio!=0){
                añadirRutEjer(idRutina,idEjercicio);
            }
        }

    }

    public int getIdEjercicio(String nombre){
        //Devolvemos todos los ejercicios que pertenecen a un usuario en un array list de ejercicios
        int id=0;
        String select="select id from ejercicio where nombre='"+nombre+"'";
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            id=c.getInt(c.getColumnIndex("id"));
        }
        return id;
    }
    public int getIdRutina(String nombre){
        //Devolvemos todos los ejercicios que pertenecen a un usuario en un array list de ejercicios
        int id=0;
        String select="select id from rutina where nombre='"+nombre+"'";
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            id=c.getInt(c.getColumnIndex("id"));
        }
        return id;
    }
    public Rutina getRutinaConNombre(String nombre){
        //Devolvemos todos los ejercicios que pertenecen a un usuario en un array list de ejercicios
        Rutina r=null;
        String select="select * from rutina where nombre='"+nombre+"'";
        Cursor c = db.rawQuery(select,null);
        if(c!=null && c.getCount()>0){
            c.moveToFirst();
            int id = c.getInt(c.getColumnIndex("id"));
            String foto = c.getString(c.getColumnIndex("foto"));
            r = new Rutina(id, nombre, foto); //Cogemos todos los elementos y con ellos creamos una rutina
        }
        return r;
    }

}
//https://academiaandroid.com/proyecto-ejemplo-de-app-android-con-bbdd-sqlite/


