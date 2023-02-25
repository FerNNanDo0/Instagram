package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterFilter;
import com.example.instagram.helper.FireBase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.model.Postagem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    // quando a classe è chamada esse bloco e executado
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    ImageView imgSelected;
    Bitmap imageOriginal, imgBitmap;

    StorageReference imageRef, storageRef;

    String urlImgPostada = "";
    Postagem postagem;
    TextInputEditText editTextInputLayout;
    AdapterFilter adapterFilter;
    RecyclerView recyclerViewFiltros;
    ProgressBar progressBarFilter;
    List<ThumbnailItem> listFiltros;

    @Override
    public boolean onSupportNavigateUp() {
        listFiltros.clear();
        finish();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        setToolBar();

        // iniciar componentes
        listFiltros = new ArrayList<>();
        imgSelected = findViewById(R.id.imageSelected);
        editTextInputLayout = findViewById(R.id.editTextInputLayout);
        recyclerViewFiltros = findViewById(R.id.recyclerViewFiltros);
        progressBarFilter = findViewById(R.id.progressBarFilter);
        progressBarFilter.setVisibility(View.GONE);

        // adapter filters
        adapterFilter = new AdapterFilter(listFiltros, this);

        // iniciar recyclerView
        RecyclerView.LayoutManager layoutManager = new
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewFiltros.setHasFixedSize(true);
        recyclerViewFiltros.setLayoutManager(layoutManager);
        recyclerViewFiltros.setAdapter(adapterFilter);

        // recupera dados da activity anterior
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] dadosImg = bundle.getByteArray("dadosImg");
            imageOriginal = BitmapFactory.decodeByteArray(dadosImg, 0, dadosImg.length);
            imgSelected.setImageBitmap(imageOriginal);

            imgBitmap = imageOriginal.copy(imageOriginal.getConfig(), true);

            filterImages();
            clickRecyclierView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemPublicar) {
            // acao de item publicar
            publicarPostagem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // setTooBar
    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Filtros");
        toolbar.setElevation(3);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_cancel_24);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void filterImages() {

        listFiltros.clear();
        ThumbnailsManager.clearThumbs();

        ThumbnailItem itemFiltroNormal = new ThumbnailItem();

        itemFiltroNormal.image = imgBitmap;
        itemFiltroNormal.filterName = "Normal";

        ThumbnailsManager.addThumb(itemFiltroNormal);

        List<Filter> filters = FilterPack.getFilterPack(getApplicationContext());

        for (Filter filter : filters) {
            ThumbnailItem itemFiltro = new ThumbnailItem();

            itemFiltro.image = imgBitmap;
            itemFiltro.filter = filter;
            itemFiltro.filterName = filter.getName();

            ThumbnailsManager.addThumb(itemFiltro);
        }

        listFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterFilter.notifyDataSetChanged();

    }

    private void clickRecyclierView() {
        recyclerViewFiltros.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewFiltros,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ThumbnailItem thumbnailItem = listFiltros.get(position);

                        // Accessing single filter...
                        imgBitmap = imageOriginal.copy(imageOriginal.getConfig(), true);
                        Filter filter = thumbnailItem.filter;
// apply filter
                        imgSelected.setImageBitmap(filter.processFilter(imgBitmap));

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));
    }

    // publicar postagens
    private void publicarPostagem() {

        // verifica o campo de descrição e retorna se houver texto digitado
        String textDescricao = editTextInputLayout.getText().toString();

        postagem = new Postagem();
        postagem.setIdUser(FireBase.UsuarioFirebase.IdUser());
        postagem.setIdpostagem(postagem.getIdpostagem());
        postagem.setDescricao(textDescricao);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageOriginal.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImg = baos.toByteArray();

        // salvar imgs no storage
        storageRef = FireBase.storageRef();
        imageRef = storageRef
                .child("imagens")
                .child("postagens")
                .child(postagem.getIdpostagem() + "jpeg");

        // menssagem pro usuario
        Toast.makeText(this, "Salvando imagem Aguarde...", Toast.LENGTH_LONG).show();
        progressBarFilter.setVisibility(View.VISIBLE);

        // salvar imgs no storage
        UploadTask _uploadTask = imageRef.putBytes(dadosImg);
        _uploadTask
                .addOnFailureListener(e -> {

                }).addOnSuccessListener(taskSnapshot -> {

                    if (taskSnapshot.getTask().isComplete()) {
                        // recuperar url da img
                        repurarUrlImgPostagemUser();
                    }

                });
    }


    // recupera o link da img
    private void repurarUrlImgPostagemUser() {
        imageRef = storageRef
                .child("imagens")
                .child("postagens")
                .child(postagem.getIdpostagem() + "jpeg");

        imageRef.getDownloadUrl()
                .addOnCompleteListener(task -> {

                    // recupera o link da img
                    urlImgPostada = task.getResult().toString();
                    postagem.setUrlfoto(urlImgPostada);


                    if (postagem.salvar()) {
                        // salvar postagem no database
                        Toast.makeText(this, "Sucesso ao salvar imagem", Toast.LENGTH_LONG).show();
                        finish();

                    }
                }).addOnFailureListener(e ->{
                    Toast.makeText(this, "Erro ao salvar imagem", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
        });
    }
}