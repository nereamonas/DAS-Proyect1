package com.example.das_proyect1.ui.camara;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.base.BaseViewModel;
import com.example.das_proyect1.helpClass.ImagenFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CamaraFragment  extends BaseFragment {

    //Fragmento que controla la camara. Podemos sacar una foto, o seleccionar una foto desde la galeria. Se cargará en la pantalla y poniendole un nombre podremos subirla a firebase. En el caso de que la foto se saque con la camara esta tambn se guardara en la galeria del mvl


    private StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;

    private int CAMERA_CODIGO=4;
    private int GALLERY_COGIDO=2;

    private ImageView imagenActual;
    Button btnGaleria,btnCamara,buttonSubirAFirebase;
    String currentPhotoPath;
    Uri contentUri;
    EditText nombreFoto;
    private BaseViewModel camaraViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        camaraViewModel =
                new ViewModelProvider(this).get(BaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_camara, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);


        mStorageRef = FirebaseStorage.getInstance().getReference();  //Abrimos la referencia del firebase para poder subir las fotos
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("imagenes");  //Abrimos la referencia del firebase


        btnCamara=root.findViewById(R.id.buttonCamara);
        btnCamara.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en el boton camara. pediremos permisos para abrir la camara y guardar las fotos
            @Override
            public void onClick(View v) {
                if ((ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
                    abrirCamara();
                }if ((ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                    permisoCamara();
                }if ((ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    permisoGuardar();
                }
            }
        });

        btnGaleria=root.findViewById(R.id.buttonGaleria);
        btnGaleria.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en la galeria se abrira la galeria
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //Creamos un intent de la galeria. para abrir la galeria
                startActivityForResult(gallery, GALLERY_COGIDO); //Le pasamos un codigo para identificar la respuesta
            }
        });

        imagenActual = root.findViewById(R.id.displayImageView); //Referencia a la imagen
        if (savedInstanceState != null) { //Si viene de la rotacion de pantalla q tiene cosas guardadas. el tiempo y la posicion lo ogera de ahi
            String uri = savedInstanceState.getString("uri"); //cogemos la url de la imagen guardada antes d la rot
            Log.d("Logs","save url: "+uri);
            contentUri= Uri.parse(uri);
            imagenActual.setImageURI(contentUri); //Y la colocamos
        }

        buttonSubirAFirebase=root.findViewById(R.id.buttonSubirAFirebase);
        buttonSubirAFirebase.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en el boton camara. pediremos permisos para abrir la camara y guardar las fotos
            @Override
            public void onClick(View v) {
                nombreFoto=root.findViewById(R.id.editTextNombreFoto);  //Referencia al nombre
                if(TextUtils.isEmpty(nombreFoto.getText().toString())) { //Si el nombre es nulo, decir q no se va a subir
                    Toast.makeText(getActivity(), getString(R.string.camara_toast_Tienesqueinsertarunnombre), Toast.LENGTH_SHORT).show(); //Es obligatorio insertar un nombre

                }else{
                    if (contentUri!=null) { //Si contentUri es distinto de null significa que tenemos una imagen seleccionada
                        String ext = "jpg";  //Ponemos por defecto jpg
                        if (getFileExt(contentUri) != null) { //Pero si trae extension cogemos su extension
                            ext = getFileExt(contentUri);
                        }
                        String imageFileName = nombreFoto.getText() + "." + ext; //Creamos el nombre de la imagen con el nombre q ha elegido el usuario + la extension
                        Log.d("Logs", "Se subira la imagen con el nombre: " + imageFileName);
                        //Subimos la foto al firebase
                        subirImagenAFirebase(imageFileName, contentUri); //Subimos la foto a firebase
                    }else{
                        Toast.makeText(getActivity(), getString(R.string.camara_toast_Tienesqueinsertarunafoto), Toast.LENGTH_SHORT).show(); //Mensaje de q es obligatorio seleccionar una foto
                    }
                }
            }
        });

        TextView verTodosLosArchivosFirebase=root.findViewById(R.id.verTodosLosArchivosFirebase);
        verTodosLosArchivosFirebase.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en la galeria se abrira la galeria
            @Override
            public void onClick(View v) {
                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build();  //Navegaremos a la ventana de ImagenesFirebase
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_camaraFragment_to_imagenesFirebaseFragment,null,options);
            }
        });

        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {  //Kudeatuamos las respuestas

        if(requestCode == CAMERA_CODIGO){  //Si la respuesta viene de la camara y es correcta
            if(resultCode == Activity.RESULT_OK){ //Si to do ha ido bien

                //Bitmap image=(Bitmap) data.getExtras().get("data");  //Ponemos la imaagen directamente en la pantalla
                //selectedImage.setImageBitmap(image);

                //Cogemos la imagen sacada
                File f = new File(currentPhotoPath);
                imagenActual.setImageURI(Uri.fromFile(f)); //Ponemos la imagen en pantalla
                Log.d("Logs", "URL de la imagen sacada:  " + Uri.fromFile(f));

                //Guardaremos la foto en la galeria del movil
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri=Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);
            }
        }

        if(requestCode == GALLERY_COGIDO){ //Si la respuesta viene de la galeria y es correcta.
            if(resultCode == Activity.RESULT_OK){ //Si to do ha ido bien
                //Cargamos la imagen
                this.contentUri=data.getData();
                Log.d("Logs","URL de la imagen de la galeria: "+contentUri);
                imagenActual.setImageURI(contentUri);  //La mostramos en pantalla
            }
        }
    }

    private String getFileExt(Uri contentUri) {  //Cogemos la extension de la uri y la devolvemos
        ContentResolver c = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    //Copiado de la pag oficial
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Copiado de la pag oficial
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_CODIGO);
            }
        }
    }


    private void subirImagenAFirebase(String name, Uri contentUri) {  //Subimos la imagen a firebase

        final StorageReference image = mStorageRef.child("imagenes/" + name); //La carpeta donde queremos almacenar la foto
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Logs", "Imagen subida. URI: " + uri.toString());
                        //Glide.with(getActivity().getApplicationContext()).load(uri).into(imagenActual);  //Cargamos la imagen en el imagen view
                       //Lo subimos tambn a la base de datos para poder visualizarlas luego
                        ImagenFirebase img= new ImagenFirebase(nombreFoto.getText().toString(),uri.toString());
                        String id= mDatabaseRef.push().getKey();
                        mDatabaseRef.child(id).setValue(nombreFoto.getText().toString()+"###"+uri.toString());
                    }
                });
                Toast.makeText(getActivity(), getString(R.string.camara_toast_Sehasubidolaimagen), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), getString(R.string.camara_toast_Nosehapodidosubirlaimagen), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Cuando se rota la pantalla tendremos q guardar x informacion. la posicion en la que estamos y el tiempo q falta del contador para accabar
        super.onSaveInstanceState(outState);
        Log.d("Logs","GUARDAR "+String.valueOf(contentUri));
        outState.putString("uri", String.valueOf(contentUri));
    }

    public void permisoCamara(){  //Pedimos permiso para abrir la camara, cuando este concedido abrimos la camara
        Log.d("Logs","PERMISO CAMARA");
        boolean permiso=false;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("Logs","NO ESTA CONCEDIDO");
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO
                Log.d("Logs","DECIR XQ ES NECESARIO");
            }
            else{
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
                Log.d("Logs","NO SE HA CONCEDIDO");
                Toast.makeText(getActivity(), getString(R.string.camara_toast_Nosehanconcedidolospermisosparaabrirlacamara), Toast.LENGTH_SHORT).show();
            }
            //PEDIR EL PERMISO
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);  //ACTIVITY_RECOGNITION
            Log.d("Logs","PEDIR PERMISO");
        }
        else {
            Log.d("Logs","YA LO TIENE");
        }
    }


    public void permisoGuardar(){  //Permiso de escritura
        Log.d("Logs","PERMISO GUARDAR");
        boolean permiso=false;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("Logs","NO ESTA CONCEDIDO");
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO
                Log.d("Logs","DECIR XQ ES NECESARIO");
            }
            else{
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
                Log.d("Logs","NO SE HA CONCEDIDO");
                Toast.makeText(getActivity(), getString(R.string.camara_toast_Nosehaconcedidoelpermisodeescritura), Toast.LENGTH_SHORT).show();
            }
            //PEDIR EL PERMISO
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);  //ACTIVITY_RECOGNITION
            Log.d("Logs","PEDIR PERMISO");
        }
        else {
            Log.d("Logs","YA LO TIENE");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode==1){
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoGuardar();
                }
                return;
        }
        if(requestCode==2){
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirCamara();
                }
        }
    }
}
