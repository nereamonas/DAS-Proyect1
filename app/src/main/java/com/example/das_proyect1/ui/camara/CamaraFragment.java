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

import com.bumptech.glide.Glide;
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
    private StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;

    private int CAMERA_REQUEST_CODE=4;
    private int GALLERY_REQUEST_CODE=2;

    private ImageView imagenActual;
    Button btnGaleria,btnCamara,buttonSubirAFirebase;
    String currentPhotoPath;
    Uri contentUri;
    TextView verTodosLosArchivosFirebase;
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
                permisoGuardar();
                permisoCamara();
            }
        });
        btnGaleria=root.findViewById(R.id.buttonGaleria);
        btnGaleria.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en la galeria se abrira la galeria
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE); //Le pasamos un codigo para identificar la respuesta
            }
        });

        imagenActual = root.findViewById(R.id.displayImageView);


        buttonSubirAFirebase=root.findViewById(R.id.buttonSubirAFirebase);
        buttonSubirAFirebase.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en el boton camara. pediremos permisos para abrir la camara y guardar las fotos
            @Override
            public void onClick(View v) {
                nombreFoto=root.findViewById(R.id.editTextNombreFoto);
                if(TextUtils.isEmpty(nombreFoto.getText().toString())) { //Si el nombre es nulo, decir q no se va a subir
                    Toast.makeText(getActivity(), "Tienes que insertar un nombre", Toast.LENGTH_SHORT).show();

                }else{
                    if (contentUri!=null) {
                        String ext = "jpg";
                        if (getFileExt(contentUri) != null) {
                            ext = getFileExt(contentUri);
                        }
                        String imageFileName = nombreFoto.getText() + "." + ext;
                        Log.d("Logs", "Se subira la imagen con el nombre: " + imageFileName);

                        //Subimos la foto al firebase
                        uploadImageToFirebase(imageFileName, contentUri);
                    }else{
                        Toast.makeText(getActivity(), "Tienes que insertar una foto", Toast.LENGTH_SHORT).show();
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
                        .build();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_camaraFragment_to_imagenesFirebaseFragment,null,options);

            }
        });

        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == CAMERA_REQUEST_CODE){  //Si la respuesta viene de la camara y es correcta
            if(resultCode == Activity.RESULT_OK){

                //Bitmap image=(Bitmap) data.getExtras().get("data");  //Ponemos la imaagen directamente en la pantalla
                //selectedImage.setImageBitmap(image);

                //Guardaremos la foto en la galeria del movil
                File f = new File(currentPhotoPath);
                imagenActual.setImageURI(Uri.fromFile(f));
                Log.d("Logs", "URL de la imagen sacada:  " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                contentUri=Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);

                //Subiremos la foto al firebase
                //uploadImageToFirebase(f.getName(),contentUri,"Camara");

            }

        }

        if(requestCode == GALLERY_REQUEST_CODE){ //Si la respuesta viene de la galeria y es correcta.
            if(resultCode == Activity.RESULT_OK){
                //Cargamos la imagen poniendole un nombre
                this.contentUri=data.getData();

                Log.d("Logs","URL de la imagen de la galeria: "+contentUri);
                //Uri contentUri = data.getData();
                //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                //String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                //Log.d("Logs", "Url de la imagen de la galeria:  " +  imageFileName);
                imagenActual.setImageURI(contentUri);

                //Subimos la foto al firebase
                //uploadImageToFirebase(imageFileName,contentUri,"Galeria");
            }
        }
    }

    private String getFileExt(Uri contentUri) {
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

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);

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
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    private void uploadImageToFirebase(String name, Uri contentUri) {

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

                Toast.makeText(getActivity(), "Se ha subido la imagen", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "No se ha podido subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });

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
                Toast.makeText(getActivity(), "No se han concedido los permisos para abrir la camara", Toast.LENGTH_SHORT).show();
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);  //ACTIVITY_RECOGNITION

            Log.d("Logs","PEDIR PERMISO");
            abrirCamara();
            //ya estará aceptado asiq:
        }
        else {
            Log.d("Logs","YA LO TIENE");
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            abrirCamara();
        }
    }


    public void permisoGuardar(){  //Permiso de escritura
        Log.d("Logs","PERMISO CAMARA");
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
                Toast.makeText(getActivity(), "No se ha concedido el permiso de escritura", Toast.LENGTH_SHORT).show();
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);  //ACTIVITY_RECOGNITION

            Log.d("Logs","PEDIR PERMISO");
            //ya estará aceptado asiq:
        }
        else {
            Log.d("Logs","YA LO TIENE");
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
        }
    }

}