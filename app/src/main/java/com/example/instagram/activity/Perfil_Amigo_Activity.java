package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.adapter.AdapterGrid;
import com.example.instagram.helper.FireBase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfil_Amigo_Activity extends AppCompatActivity {
    TextView name_user, btn_Profile, posts, followers, following;
    CircleImageView imageView;
    GridView gridViewimgs;
    AdapterGrid adapterGrid;
    ValueEventListener valueEventListener;
    DatabaseReference db, userRef, seguidoresRef, postagensRef;

    Usuario usuarioAmigo, usuarioAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // config da toolbar
        configToolbar();

        // inicializa componentes da tela
        inicializaComponentes();

        // recupera dados vindo da pesquisaFragment
        Bundle bundleUser = getIntent().getExtras();
        usuarioAmigo = (Usuario) bundleUser.get("usuarioSelected");

        // define foto e nome do usuario
        definirImgPerfil(usuarioAmigo);

        // recuperar dados do usuario atual
        recuperarDadosUser();

        // carrega as fotos de postagens do usuario selecionado
        carregarFotosPostagem();

        inicializarImageLoader();
    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef = db.child("users").child(usuarioAmigo.getId());
        valueEventListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // DADOS DO PERFIL
                Usuario user = snapshot.getValue(Usuario.class);

                //posts.setText( String.valueOf( user.getPosts() ));
                followers.setText(String.valueOf(user.getSeguidores()));
                following.setText(String.valueOf(user.getSeguindo()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void inicializarImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void carregarFotosPostagem() {

        postagensRef = db
                .child("postagens")
                .child(usuarioAmigo.getId());

        postagensRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // lista para tds links de fotos
                List<String> urlsFotos = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Postagem postagem = snap.getValue(Postagem.class);

                    // add urls a lista de UrlsFotos
                    urlsFotos.add(postagem.getUrlfoto());
                    // exibe numero de postagens
                    posts.setText(String.valueOf(urlsFotos.size()));

                    // config adapterGrid
                    adapterGrid = new AdapterGrid(
                            getApplicationContext(),
                            R.layout.grid_postagem,
                            urlsFotos);
                    gridViewimgs.setAdapter(adapterGrid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarDadosUser() {
        userRef = db.child("users").child(FireBase.UsuarioFirebase.IdUser());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioAtual = snapshot.getValue(Usuario.class);
                verificarSeguidor();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void verificarSeguidor() {
        seguidoresRef = db.child("seguindores")
                .child(FireBase.UsuarioFirebase.IdUser())
                .child(usuarioAmigo.getId());

        seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // ja esta seguindo
                    ativarButtom(false);
                } else {
                    // nao esta seguindo
                    ativarButtom(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickBotton() {
        btn_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarSeguidor(usuarioAtual, usuarioAmigo);
            }
        });
    }

    // salvar seguidor
    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo) {
        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nome", uAmigo.getNome());
        dadosAmigo.put("foto", uAmigo.getFoto());

        DatabaseReference seguidorRef = seguidoresRef;
        seguidorRef.setValue(dadosAmigo);

        // Alterar botao acao para seguindo
        btn_Profile.setText("Seguindo");
        btn_Profile.setOnClickListener(null);

        // Atualizar para ambos
        // USUARIO ATUAL
        final int seguindo = uLogado.getSeguindo() + 1;
        final int seguidores = uAmigo.getSeguidores() + 1;

        userRef = db.child("users").child(FireBase.UsuarioFirebase.IdUser());
        HashMap<String, Object> dadosUserAtual = new HashMap<>();
        dadosUserAtual.put("seguindo", seguindo);
        userRef.updateChildren(dadosUserAtual);

        // USUARIO AMIGO
        //    referencia amigo no db
        DatabaseReference amigoRef = db.child("users").child(uAmigo.getId());

        HashMap<String, Object> dadosUserAmigo = new HashMap<>();
        dadosUserAmigo.put("seguidores", seguidores);
        amigoRef.updateChildren(dadosUserAmigo);
    }

    private void ativarButtom(boolean seguindo) {
        if (seguindo) {
            btn_Profile.setText("Seguir");

            // click btn perfil amigo
            clickBotton();
        } else {
            btn_Profile.setText("Seguindo");
        }
    }


    // toolbar config
    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Instagram");
        toolbar.setElevation(3);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void inicializaComponentes() {
        imageView = findViewById(R.id.imageViewPerfil);
        name_user = findViewById(R.id.textViewNomePerfil);
        btn_Profile = findViewById(R.id.btn_AcaoPerfil);
        followers = findViewById(R.id.seguidores);
        following = findViewById(R.id.seguindo);
        posts = findViewById(R.id.posts);
        gridViewimgs = findViewById(R.id.gridViewFotos);

        btn_Profile.setText("Carregando...");
        posts.setText(" ");
        followers.setText(" ");
        following.setText(" ");

        db = FireBase.databaseRef();
    }

    //definir img user
    private void definirImgPerfil(Usuario usuario) {

        // define o nome
        definirNome(usuario.getNome());

        String urlfoto = usuario.getFoto();
        if (urlfoto != null) {
            Glide.with(this)
                    .load(urlfoto)
                    .centerCrop()
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.padrao);
        }
    }

    // definir nome de usuario
    private void definirNome(String name) {

        // separa o nome do usuario para aparecer so o primeiro nome
        String[] nomeSeparado = name.split(" ");
        if (nomeSeparado.length > 1) {
            if (nomeSeparado[0].trim().length() <= 9
                    && nomeSeparado[1].trim().length() > 9) {

                String nome = nomeSeparado[0];
                name_user.setText(nome);

            } else {
                String nome = String.
                        format("%s" + "\n" + "%s", nomeSeparado[0], nomeSeparado[1]);
                name_user.setText(nome);
            }
        } else {
            name_user.setText(name);
        }
    }

}