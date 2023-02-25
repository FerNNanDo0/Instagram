package com.example.instagram.navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.activity.EditProfileActivity;
import com.example.instagram.helper.FireBase;
import com.example.instagram.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private View view;
    private TextView name_user, btn_editProfile, posts, followers, following;
    private CircleImageView img_profile;
    private ProgressBar progressBar;
    private Intent intentEditPerfil;
    ValueEventListener valueEventListener;
    DatabaseReference db, userRef;

    Usuario usuario;

    public PerfilFragment() {
        // Required empty public constructor
    }


    /***    public static PerfilFragment newInstance(String param1, String param2) {
     PerfilFragment fragment = new PerfilFragment();
     Bundle args = new Bundle();
     args.putString(ARG_PARAM1, param1);
     args.putString(ARG_PARAM2, param2);
     fragment.setArguments(args);
     return fragment;
     }***/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);

        inicializaComponentes();

        // definir nome de usuario
        definirNome();

        //click do btnEditPerfile
        clickBtnEditPerfile();
        //definir imagem de perfil
        definirImgPerfil();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        // buscar dados do uuario logado
        userRef = db.child("users").child( FireBase.UsuarioFirebase.IdUser() );
        valueEventListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario user = snapshot.getValue(Usuario.class);
                posts.setText( String.valueOf( user.getPosts() ));
                followers.setText( String.valueOf( user.getSeguidores() ));
                following.setText( String.valueOf( user.getSeguindo() ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void inicializaComponentes() {
        followers = view.findViewById(R.id.seguidores);
        following = view.findViewById(R.id.seguindo);
        posts = view.findViewById(R.id.posts);
        btn_editProfile = view.findViewById(R.id.btn_AcaoPerfil);
        name_user = view.findViewById(R.id.textViewNomePerfil);
        img_profile = view.findViewById(R.id.imageViewPerfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);

        posts.setText(" ");
        followers.setText(" ");
        following.setText(" ");

        db = FireBase.databaseRef();
    }

        // definir nome de usuario
    private void definirNome(){

        // separa o nome do usuario para aparecer so o primeiro nome
        FirebaseUser userAtual = FireBase.UsuarioFirebase.UserAtual();
        String[] nomeSeparado = userAtual.getDisplayName().split(" ");
        if (nomeSeparado.length > 1) {
            if (nomeSeparado[0].trim().length() <= 9
                    && nomeSeparado[1].trim().length() > 9) {

                String nome = nomeSeparado[0];
                name_user.setText(nome);

            }else{
                String nome = String.
                        format("%s" + "\n" + "%s", nomeSeparado[0], nomeSeparado[1]);
                name_user.setText(nome);
            }
        } else {
            name_user.setText(userAtual.getDisplayName());
        }
    }


    public void clickBtnEditPerfile() {
        btn_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentEditPerfil = new Intent(requireActivity(), EditProfileActivity.class);
                startActivity(intentEditPerfil);
            }
        });
    }


    //definir img
    private void definirImgPerfil() {
        Uri urlfoto = FireBase.UsuarioFirebase.UserAtual().getPhotoUrl();
        if (urlfoto != null) {
            Glide.with(this)
                    .load(urlfoto)
                    .centerCrop()
                    .into(img_profile);
        } else {
            img_profile.setImageResource(R.drawable.padrao);
        }
    }
}