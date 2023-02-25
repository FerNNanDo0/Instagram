package com.example.instagram.navigation;

import android.Manifest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.instagram.R;
import com.example.instagram.activity.FiltroActivity;
import com.example.instagram.helper.Permissions;

import java.io.ByteArrayOutputStream;

public class PostagemFragment extends Fragment {

    String[] listPermission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    View view;

    ActivityResultLauncher<Intent> galeria_StartActivityForResult, camera_StartActivityForResult;

    Button abrirCam, abrirGal;

    public PostagemFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Permissions.validatePermissions(listPermission, requireActivity(), 1);
        view = inflater.inflate(R.layout.fragment_postagem, container, false);
        abrirCam = view.findViewById(R.id.abrirCamera);
        abrirGal = view.findViewById(R.id.abrirGaleria);

        // click dos buttons
        abrirCamera(abrirCam);
        abrirGaleria(abrirGal);

        // Iniciar StartActivityForResult
        camera_getStartActivityForResult();
        galeria_getStartActivityForResult();

        return view;
    }


    // ActivityResul da GALERIA
    private void galeria_getStartActivityForResult() {
        galeria_StartActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        imagemDaGaleria(data);
                    }
                });
    }
    private void imagemDaGaleria(Intent data){
        try {
            assert data != null;
            Bitmap imgBitmap;

            // recupera img selecionada da galeria
            Uri imgSelected = data.getData();
            imgBitmap = MediaStore.Images.Media
                    .getBitmap(requireActivity().getContentResolver(), imgSelected);

            // configura img na tela se houver uma img
            if (imgBitmap != null) {

                //reuperar dados da img para o firebase
                byte[] dadosImg = recuperarDadosIMG(imgBitmap);

                Intent i = new Intent(requireActivity(), FiltroActivity.class);
                i.putExtra("dadosImg", dadosImg);
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ActivityResul da CAMERA
    private void camera_getStartActivityForResult() {
        camera_StartActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        imagemDaCamera(data);
                    }
                });
    }
    private void imagemDaCamera(Intent data){
        try {
            assert data != null;
            Bitmap imgBitmap;

            // recupera img da camera
            imgBitmap = (Bitmap) data.getExtras().get("data");

            // configura img na tela se houver uma img
            if (imgBitmap != null) {

                //reuperar dados da img para o firebase
                byte[] dadosImg = recuperarDadosIMG(imgBitmap);

                Intent i = new Intent(requireActivity(), FiltroActivity.class);
                i.putExtra("dadosImg", dadosImg);
                startActivity(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // click abrirCam
    private void abrirCamera(Button btn) {
        btn.setOnClickListener(View -> {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camera_StartActivityForResult.launch(i);
        });
    }

    // click abrirGal
    private void abrirGaleria(Button btn) {
        btn.setOnClickListener(View -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galeria_StartActivityForResult.launch(i);
        });
    }


    // result para image selecionada


    //reuperar dados da img para o firebase
    private byte[] recuperarDadosIMG(Bitmap imgBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return baos.toByteArray();
    }


}