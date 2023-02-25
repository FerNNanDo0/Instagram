package com.example.instagram.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FireBase {

    private static FirebaseAuth auth;
    private static StorageReference storage;
    private static DatabaseReference database;

    // return auth
    public static FirebaseAuth authenticate(){
        return auth = FirebaseAuth.getInstance();
    }

    // return database
    public static DatabaseReference databaseRef(){
        return database = FirebaseDatabase.getInstance().getReference();
    }

    // return storage
    public static StorageReference storageRef(){
        return storage = FirebaseStorage.getInstance().getReference();
    }


    // return instance of user

    public static class UsuarioFirebase{

        private static String id;

        public static String IdUser(){
            id = authenticate().getCurrentUser().getUid();
            return id;
        }

        public static FirebaseUser UserAtual(){
            return authenticate().getCurrentUser();
        }

        public static boolean UsuarioLogado(){
            return authenticate().getCurrentUser() != null;
        }

        public static void AtulizarDadosUser( String nome, Uri img){
            try{
                UserProfileChangeRequest profile = new UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(nome)
                        .setPhotoUri(img)
                        .build();
                UserAtual().updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Log.d("tag","erro ao atulizar perfil");
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        public static Usuario getDadosUsuarioLogado(){
            FirebaseUser atualUser = FireBase.UsuarioFirebase.UserAtual();
            Uri uri = atualUser.getPhotoUrl();

            Usuario user = new Usuario();
            user.setEmail( atualUser.getEmail() );
            user.setNome( atualUser.getDisplayName() );
            user.setId( atualUser.getUid() );

            if(uri  == null){
                user.setFoto("");
            }else{
                user.setFoto( uri.toString() );
            }

            return user;
        }

    }

}
