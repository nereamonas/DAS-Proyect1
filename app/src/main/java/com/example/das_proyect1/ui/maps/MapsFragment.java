package com.example.das_proyect1.ui.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;


public class MapsFragment extends BaseFragment implements GoogleMap.OnPolylineClickListener{

    //Clase google maps. Se ha inplementado un mapa donde desde tu ubicación actual puedes calcular la distancia a cualquier ubicacion que elijas en el mapa

    //Inicializamos todas las variables que usaremos
   // private MutableLiveData<String> mText;
    private GoogleMap map;
    private GeoApiContext mGeoApiContext=null;
    private LatLng posUsuario;
    private ArrayList<PolylineData> mPolylineData= new ArrayList<>();
    private Marker selectMarker=null;
    private LocationCallback actualizador=null;
    private LocationRequest peticion =null;
    private FusedLocationProviderClient proveedordelocalizacion =null;
    private SupportMapFragment mapFragment;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    //Cuando clicamos encima de la marca, preguntaremos a ver si quiere calcular la distancia a ese punto

                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext()); //Abrimos un dialogo para preguntar si quiere calcular la distancia
                    dialogo.setTitle(getString(R.string.maps_calculardistancia));
                    dialogo.setMessage(getString(R.string.maps_quierescalcularelcaminohastaestepunto));
                    //dialogo.setCancelable(false);
                    dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {  //En el caso de SI
                        public void onClick(DialogInterface dialogo1, int id) {
                            selectMarker=marker; //el marcador actual lo guardamos
                            calculateDirections(marker); //Calcularemos las direcciones
                        }
                    });
                    dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {  //En el caso de no, no haremos nada
                        public void onClick(DialogInterface dialogo1, int id) {

                        }
                    });
                    dialogo.show(); //Mostramos el dialogo
                }

            });
            Double[] cordenadaActual = getCordenadas();  //cogemos las cordenadas actuales del usuario
            posUsuario= new LatLng(cordenadaActual[0],cordenadaActual[1]); //Guardamos las cordenaddas como la posicion del usuario


            Log.d("Logs", "lo q recibe: " + cordenadaActual[0] + "  " + cordenadaActual[1]);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //Poner el circulito azul de mi localizacion
            map.setMyLocationEnabled(true);


            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {  //Cuando toques cualquier punto del mapa, queremos preguntar si quiere crear un marcador en ese punto
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.d("Logs", "Has clicado el punto: " + latLng.latitude + "  " + latLng.longitude);

                    alertaCrearMarca(latLng); //Llamamos al metodo que creara una alerta preguntando si quiere crear un punto en esas cordenadas
                }
            });
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //mText = new MutableLiveData<>();
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Bundle mapBundle = null;
        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }
        mapFragment.onCreate(mapBundle);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if(mGeoApiContext==null){
            mGeoApiContext= new GeoApiContext.Builder().apiKey("AIzaSyACvDLNNsrOwo4oqDuBRUoW2eedc6DL5R8").build();  //Le pasamos nuestro apikey de maps
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapBundle = outState.getBundle("MapViewBundleKey"); //Cogemos el almacenado
        if (mapBundle == null) { //Si es null creamos uno nuevo
            mapBundle = new Bundle();
            outState.putBundle("MapViewBundleKey", mapBundle);
        }
        mapFragment.onSaveInstanceState(mapBundle);
    }

    public void alertaCrearMarca(LatLng latLng) {  //Creamos una alerta para preguntar si quiere crear un marcador en el punto seleccionado
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
        dialogo.setTitle(getString(R.string.maps_ponerMarca));
        dialogo.setMessage(getString(R.string.maps_quieresPonerUnaMarcaEnLasCordenadas)+ latLng.latitude + " " + latLng.longitude);
        //dialogo.setCancelable(false);
        dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {  //En el caso de decir que si quiere crear un marcador
            public void onClick(DialogInterface dialogo1, int id) {
                crearPunto(latLng); //Llamamos al metodo crearPunto que se encargará de crear el punto en las cordenadas seleccionadas por el usuario
            }
        });
        dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {  //En el caso de que no quiera crear marcador no se hará nada
            public void onClick(DialogInterface dialogo1, int id) {
            }
        });
        dialogo.show();
    }

    public Double[] getCordenadas() {  //Metodo que returnea las cordenadas
        Double[] cordenadas = new Double[2];
        cordenadas[0] = Double.valueOf(43.0504629);  //Ponemos unas por defecto
        cordenadas[1] = Double.valueOf(-3.0070026);  //Ponemos una x defecto
        //Pedimos permiso de access_fine_location
        boolean permiso = false;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("Logs", "NO ESTA CONCEDIDO");
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO
                Log.d("Logs", "DECIR XQ ES NECESARIO");
            } else {
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
                Log.d("Logs", "NO SE HA CONCEDIDO");
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);  //ACTIVITY_RECOGNITION

            Log.d("Logs", "PEDIR PERMISO");
            permiso = true;
        } else {
            Log.d("Logs", "YA LO TIENE");
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            permiso = true;
        }
        //Si tiene el permiso procedemos:
        if (permiso) {
            proveedordelocalizacion =
                    LocationServices.getFusedLocationProviderClient(getContext());

            proveedordelocalizacion.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d("Logs", "BN");
                                LatLng posicion = new LatLng(location.getLatitude(), location.getLongitude());
                                crearPuntoIni(posicion);
                            } else {
                                Log.d("Logs", "BN PERO MAL");
                            }
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Logs", "FATAL");
                            getCordenadas();
                        }
                    });


            //cambios de posicion)

            peticion = LocationRequest.create();
            peticion.setInterval(1000);
            peticion.setFastestInterval(5000);

            peticion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            actualizador = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult != null) {
                        Log.d("Logs", "DOS - BN");
                        cordenadas[0] = locationResult.getLastLocation().getLatitude();
                        cordenadas[1] = locationResult.getLastLocation().getLongitude();
                        Log.d("Logs", "" + cordenadas[0] + "  " + cordenadas[1]);
                        actualizarPunto(cordenadas);
                    } else {
                        Log.d("Logs", "DOS - BN PERO MAL");
                    }
                }
            };

            proveedordelocalizacion.requestLocationUpdates(peticion, actualizador, null);
        } else {

        }
        Log.d("Logs", "antes del return: " + cordenadas[0] + "  " + cordenadas[1]);
        return cordenadas;

    }

    public void crearPuntoIni(LatLng posicion) {  //Creamos el punto inicial desde donde se van a calcular todas las distancias a los marcadores puestos posteriormente
        map.addMarker(new MarkerOptions().position(posicion).title(getString(R.string.maps_puntoInicial)));  //Añadimos marcador con el nombre punto inicial al clicar sobre el
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15)); //Movemos la camara, para que se centre en el punto
    }

    public void crearPunto(LatLng posicion) { //Creamos un punto. igual q el punto inicial, pero tendra otro titulo diferente
        map.addMarker(new MarkerOptions().position(posicion).title(getString(R.string.maps_tocaparacalculareltrayecto)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));
    }

    public void actualizarPunto(Double[] cordenadas) {
        CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(cordenadas[0], cordenadas[1]), 15);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    private void calculateDirections(Marker marker){ //Calcular la direcciones entre el punto del usuario hasta el marcador seleccionado
        Log.d("Logs", "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );  //LagLng destino
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext); //Creamos las direcciones

        directions.alternatives(true); //Queremos tambien rutas alternaticas
        directions.origin(
                new com.google.maps.model.LatLng(
                        posUsuario.latitude,
                        posUsuario.longitude
                ));  //Marcamos cual es el origen

        Log.d("Logs", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("Logs", "calculateDirections: routes: " + result.routes[0].toString());
                Log.d("Logs", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d("Logs", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d("Logs", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result); //Añadimos el polyline
            }

            @Override
            public void onFailure(Throwable e) { //si falla mostramos log
                Log.e("Logs", "calculateDirections: Failed to get directions: " + e.getMessage() );
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){ //Añadimos la linea al mapa
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("Logs", "run: result routes: " + result.routes.length);

                if(mPolylineData.size()>0){
                    mPolylineData= new ArrayList<>();
                }
                int cont=1;
                for(DirectionsRoute route: result.routes){  //Recorremos todas las rutas posibles que tengamos
                    Log.d("Logs", "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath)); //Añadimos al mapa la linea
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.grey)); //Sera de color gris
                    polyline.setClickable(true); //Es clicable, para hacer que cuando se clicke se ponga de color azul
                    mPolylineData.add(new PolylineData(polyline,route.legs[0]));
                    onPolylineClick(polyline);
                    selectMarker.setVisible(false);
                    if(cont==1){  //En la primera que nos ofrezca la cogeremos por defecto y lo ponemos de color azul para q resalte mas
                        polyline.setColor(ContextCompat.getColor(getActivity(), R.color.blue));
                        polyline.setZIndex(1);
                    }
                    cont++;

                }
            }
        });
    }


    @Override
    public void onPolylineClick(Polyline polyline) {  //Cuando clickemos una de las lineas, cambiaremos los estados. Por un lado pondremos la linea seleccionada en azul y el resto en gris, y en el titulo del marcador, pondremos la distancia que tienes por esa nueva ruta seleccionada y el tiempo que llevará
        Log.d("Logs", "click polyline");
        int cont=1;
        for(PolylineData polylineData: mPolylineData){ //Recorremos todas las lineas q tenemos
            Log.d("Logs", "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){ //Si es la linea seleccionada
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue)); //La pintamos en azul
                polylineData.getPolyline().setZIndex(1);

                LatLng endLoc= new LatLng(polylineData.getLeg().endLocation.lat, polylineData.getLeg().endLocation.lng);

                Marker marker=map.addMarker(new MarkerOptions().position(endLoc).title(getString(R.string.maps_trayecto)+" "+cont).snippet(getString(R.string.maps_duracion)+": "+polylineData.getLeg().duration+", "+getString(R.string.maps_kilometros)+": "+polylineData.getLeg().distance));//Cambiamos el titulo del marcador

                marker.showInfoWindow(); //Lo visualizamos
            }
            else{ //Si no es la linea seleccionada
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.grey)); //Lo ponemos de color gris
                polylineData.getPolyline().setZIndex(0);
            }
            cont++;
        }
    }


    @Override
    public void onDetach() {  //Este metodo detecta cuando pulsamos la tecla atras en el mvl. Es importante cancelar el temporizador, ya que si al darle atras no se cancela, cuando este acabe peta la aplicacion
        Log.d("Logs","DETACH"); //Añadimos un log para comprobar
        cancelarTodo();
        super.onDetach();
    }

    public void cancelarTodo(){
        map=null;
        mGeoApiContext=null;
        posUsuario=null;
        mPolylineData=null;
        selectMarker=null;
        proveedordelocalizacion.removeLocationUpdates(actualizador);
        proveedordelocalizacion=null;
        this.actualizador=null;
        peticion=null;
    }
//ME FALTA AÑADIR EL CANCELAR CUANDO TOCA PARA ABRIR EL MENU... Q NO SE COMO JEJE

    //https://gist.github.com/mitchtabian/33d78c511fdb82694296ecf3ab3a05ce#file-addpolylinestomap-java

}