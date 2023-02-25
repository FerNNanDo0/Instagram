package com.example.instagram.activity;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.helper.FireBase;
import com.example.instagram.helper.Permissions;
import com.example.instagram.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView editImageView;
    TextView textAlterarFoto;
    TextInputEditText editNomePerfil, editEmailPerfil;
//    private Button btnSaveProfile;
    Usuario dados_usuario_logado;

    StorageReference storageRef;
    Uri urlImagePerfil;

    ProgressBar progressBar;
    ActivityResultLauncher<Intent> galeria_StartActivityForResult;
    StorageReference imgRef;

    String[] listPermission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Permissions.validatePermissions(listPermission, this, 1);

        dados_usuario_logado = FireBase.UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = FireBase.storageRef();

        setToolBar();

        // inicializar objetos
        inicializarComponentes();

        // recuperar dados do usuario
        FirebaseUser userAtual = FireBase.UsuarioFirebase.UserAtual();
        editNomePerfil.setText( userAtual.getDisplayName() );
        editEmailPerfil.setText( userAtual.getEmail() );

        //definir img
        definirImgPerfil();

    }

    private void setToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Perfil");
        toolbar.setElevation(3);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_cancel_24);
    }

    //definir img
    private void definirImgPerfil(){
        progressBar.setVisibility(View.VISIBLE);
        Uri urlfoto = FireBase.UsuarioFirebase.UserAtual().getPhotoUrl();
        if(urlfoto != null){
            Glide.with(this).load(urlfoto).into(editImageView);
            progressBar.setVisibility(View.GONE);
        }else{
            editImageView.setImageResource(R.drawable.padrao);
            progressBar.setVisibility(View.GONE);
        }
    }


    // click btn salvar nome perfil
    public void clickBtnSalvarAlteracao(View view){

        Uri fotoAtual = FireBase.UsuarioFirebase.UserAtual().getPhotoUrl();
        String nomeNew = editNomePerfil.getText().toString();

        // atualizar nome no perfil
        FireBase.UsuarioFirebase.AtulizarDadosUser(nomeNew, fotoAtual);

        // atualizar no banco de dados
        dados_usuario_logado.setNome( nomeNew );
        dados_usuario_logado.atualizarDados();

        Toast.makeText(this, "Dados alterados com sucesso", Toast.LENGTH_LONG).show();
    }

    private void inicializarComponentes(){
        galeria_getStartActivityForResult();

        editImageView = findViewById(R.id.editar_imgPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editNomePerfil = findViewById(R.id.editar_nomeUser);
        editEmailPerfil = findViewById(R.id.editar_emailUser);
//        btnSaveProfile = findViewById(R.id.button_SalvarPerfil);
        progressBar = findViewById(R.id.progressBarImgPerfil);
        progressBar.setVisibility(View.GONE);
        editEmailPerfil.setFocusable(false);

        // click btn
        textAlterarFoto.setOnClickListener(view -> {

            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Intent i = new Intent(Intent.ACTION_PICK, uri);
            if (i != null){
                galeria_StartActivityForResult.launch(i);
            }

        });
    }

    private void galeria_getStartActivityForResult() {
        galeria_StartActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        imagemDaGaleria(data);
                    }
                });
    }

    // pegar imgs da galeria
    private void imagemDaGaleria(Intent data){
        try {
            assert data != null;
            Bitmap imgBitmap;

            // recupera img selecionada
            Uri imgSelected = data.getData();
            imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgSelected);

            // configura img na tela se houver uma img
            if (imgBitmap != null){
                progressBar.setVisibility(View.GONE);
                editImageView.setImageBitmap(imgBitmap);

                //reuperar dados da img para o firebase
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imgBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] dadosImg = baos.toByteArray();

                //salvar img no firebase
                salvarImgFirebase(dadosImg);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void salvarImgFirebase(byte[] dadosImage){
        //salvar img no firebase storage
        imgRef = storageRef
                .child("fotos")
                .child("perfil")
                .child(FireBase.UsuarioFirebase.IdUser()+".jpeg");

        UploadTask _uploadTask = imgRef.putBytes(dadosImage);
        _uploadTask
          .addOnFailureListener(e -> {

          }).addOnSuccessListener(taskSnapshot -> {

              if (taskSnapshot.getTask().isComplete()){

                  Toast.makeText(this, "Sucesso ao salvar imagem",Toast.LENGTH_LONG).show();

                  // recuperar url da img
                  repurarUrlImgPerfilUser();
              }

        });

    }

    // recuperar url da img
    private void repurarUrlImgPerfilUser(){
        imgRef = storageRef
                .child("fotos")
                .child("perfil")
                .child(FireBase.UsuarioFirebase.IdUser()+".jpeg");

        imgRef.getDownloadUrl()
                .addOnCompleteListener(task -> {

                    // recupera o link da img
                    urlImagePerfil = task.getResult();
                    atualizarFotodoUsuario(urlImagePerfil);
                });
    }

    // atualizar no perfil
    private void atualizarFotodoUsuario(Uri url){

        // atualizar img de perfil
        String nomeAtual = FireBase.UsuarioFirebase.UserAtual().getDisplayName();
        FireBase.UsuarioFirebase.AtulizarDadosUser( nomeAtual, url );


        // atualizar no banco de dados
        dados_usuario_logado.setFoto( url.toString() );
        dados_usuario_logado.atualizarDados();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}