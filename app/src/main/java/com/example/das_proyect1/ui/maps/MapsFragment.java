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
    private MutableLiveData<String> mText;
    private GoogleMap map;
    private GeoApiContext mGeoApiContext=null;
    private LatLng posUsuario;
    private ArrayList<PolylineData> mPolylineData= new ArrayList<>();
    private Marker selectMarker=null;

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
                    Log.d("Logs", "CLICK A LA MARCCA");
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    dialogo.setTitle(("Calcular distancia?"));
                    dialogo.setMessage("Quieres calcular el camino: " );
                    //dialogo.setCancelable(false);
                    dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            selectMarker=marker;
                            calculateDirections(marker);
                        }
                    });
                    dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            Log.d("Logs", "no cerrar sesion");
                        }
                    });
                    dialogo.show();
                }

            });
            Double[] cordenadaActual = getCordenadas();
            posUsuario= new LatLng(cordenadaActual[0],cordenadaActual[1]);


            Log.d("Logs", "lo q recibe: " + cordenadaActual[0] + "  " + cordenadaActual[1]);
            LatLng sydney = new LatLng(cordenadaActual[0], cordenadaActual[1]);
            //map.addMarker(new MarkerOptions().position(sydney).title("Marcador en la cordenada actual"));
            //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            //Poner el circulito azul de mi localizacion
            map.setMyLocationEnabled(true);


            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    Log.d("Logs", "Has clicado el punto: " + latLng.latitude + "  " + latLng.longitude);
                    //Igual mejor de crear una alerta de seguro que quieres poner aqui un punto?
                    //Creamos un nuevo punto
                    alertaCrearMarca(latLng);

                }
            });


        }

    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mText = new MutableLiveData<>();
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    private SupportMapFragment f;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
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
            mGeoApiContext= new GeoApiContext.Builder().apiKey("AIzaSyAuBU3ofxCqO-Rw6IlZib7MNx9_3QastJc").build();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapBundle = outState.getBundle("MapViewBundleKey");
        if (mapBundle == null) {
            mapBundle = new Bundle();
            outState.putBundle("MapViewBundleKey", mapBundle);
        }
        f.onSaveInstanceState(mapBundle);
    }

    public void alertaCrearMarca(LatLng latLng) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
        dialogo.setTitle(("Poner marca"));
        dialogo.setMessage("Quieres poner una marca en las cordenadas: " + latLng.latitude + " " + latLng.longitude);
        //dialogo.setCancelable(false);
        dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                crearPunto(latLng);
            }
        });
        dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                Log.d("Logs", "no cerrar sesion");
            }
        });
        dialogo.show();
    }

    public boolean solicitarPermiso() {
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
        return permiso;

    }

    public Double[] getCordenadas() {
        Double[] cordenadas = new Double[2];
        cordenadas[0] = Double.valueOf(43.0504629);
        cordenadas[1] = Double.valueOf(-3.0070026);
        //Permiso
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
        if (permiso) {
            FusedLocationProviderClient proveedordelocalizacion =
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
                                //text.setText("Latitud: (desconocida)"+"  Longitud: (desconocida)");
                            }
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("Logs", "FATAL");
                        }
                    });


            //cambios de posicion)

            LocationRequest peticion = LocationRequest.create();
            peticion.setInterval(1000);
            peticion.setFastestInterval(5000);
            peticion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationCallback actualizador = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult != null) {
                        Log.d("Logs", "DOS - BN");
                        cordenadas[0] = locationResult.getLastLocation().getLatitude();
                        cordenadas[1] = locationResult.getLastLocation().getLongitude();
                        Log.d("Logs", "" + cordenadas[0] + "  " + cordenadas[1]);
                        actualizarPunto(cordenadas);
                        //text.setText("Latitud: " +
                        //       locationResult.getLastLocation().getLatitude()+"  Longitud: " +
                        //       locationResult.getLastLocation().getLongitude());
                    } else {
                        Log.d("Logs", "DOS - BN PERO MAL");
                        //text.setText("Latitud: (desconocida)  Longitud: (desconocida)");
                    }
                }
            };
            proveedordelocalizacion.requestLocationUpdates(peticion, actualizador, null);
        } else {

        }
        Log.d("Logs", "antes del return: " + cordenadas[0] + "  " + cordenadas[1]);
        return cordenadas;

    }

    public void crearPuntoIni(LatLng posicion) {
        map.addMarker(new MarkerOptions().position(posicion).title("Punto inicial"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));
        latitude=posicion.latitude;
        longitude=posicion.longitude;

    }

    public void crearPunto(LatLng posicion) {
        map.addMarker(new MarkerOptions().position(posicion).title("Toca para calcular el trayecto"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 15));
        latitude=posicion.latitude;
        longitude=posicion.longitude;

    }
    private double latitude;
    private double longitude;
    public void actualizarPunto(Double[] cordenadas) {
        CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(cordenadas[0], cordenadas[1]), 15);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.latitude=cordenadas[0];
        this.longitude=cordenadas[1];
        map.setMyLocationEnabled(true);
        //map.moveCamera(actualizar);  //o animateCamara()
    }

    private void calculateDirections(Marker marker){
        Log.d("Logs", "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        posUsuario.latitude,
                        posUsuario.longitude
                )
        );
        Log.d("Logs", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("Logs", "calculateDirections: routes: " + result.routes[0].toString());
                Log.d("Logs", "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d("Logs", "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d("Logs", "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("Logs", "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("Logs", "run: result routes: " + result.routes.length);

                if(mPolylineData.size()>0){
                    mPolylineData= new ArrayList<>();
                }
                int cont=1;
                for(DirectionsRoute route: result.routes){
                    Log.d("Logs", "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.grey));
                    polyline.setClickable(true);
                    mPolylineData.add(new PolylineData(polyline,route.legs[0]));
                    onPolylineClick(polyline);
                    selectMarker.setVisible(false);
                    if(cont==1){
                        polyline.setColor(ContextCompat.getColor(getActivity(), R.color.blue));
                        polyline.setZIndex(1);
                    }
                    cont++;

                }
            }
        });
    }


    @Override
    public void onPolylineClick(Polyline polyline) {
        Log.d("Logs", "click polyline");
        int cont=1;
        for(PolylineData polylineData: mPolylineData){
            Log.d("Logs", "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLoc= new LatLng(polylineData.getLeg().endLocation.lat, polylineData.getLeg().endLocation.lng);

                Marker marker=map.addMarker(new MarkerOptions().position(endLoc).title("Trayecto "+cont).snippet("Duración: "+polylineData.getLeg().duration+", Kilometros: "+polylineData.getLeg().distance));

                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.grey));
                polylineData.getPolyline().setZIndex(0);
            }
            cont++;
        }
    }


    @Override
    public void onDetach() {  //Este metodo detecta cuando pulsamos la tecla atras en el mvl. Es importante cancelar el temporizador, ya que si al darle atras no se cancela, cuando este acabe peta la aplicacion
        map=null;
        mGeoApiContext=null;
        posUsuario=null;
        mPolylineData=null;
        selectMarker=null;
        super.onDetach();
    }

    //https://gist.github.com/mitchtabian/33d78c511fdb82694296ecf3ab3a05ce#file-addpolylinestomap-java

}