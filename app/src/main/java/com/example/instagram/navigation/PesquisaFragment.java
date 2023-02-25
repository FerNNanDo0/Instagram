package com.example.instagram.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.widget.AdapterView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.instagram.R;
import com.example.instagram.activity.Perfil_Amigo_Activity;
import com.example.instagram.adapter.AdapterSearch;
import com.example.instagram.helper.FireBase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("NotifyDataSetChanged")
public class PesquisaFragment extends Fragment {

    private View view;
    private SearchView searchView;

    DatabaseReference usuariosRef;
    private RecyclerView recyclerView;
    List<Usuario> usuarioList = new ArrayList<>();
    private AdapterSearch adapterSearch;
    ProgressBar progressBar;

    private String nome;
    public PesquisaFragment() {
        // Required empty public constructor
    }

    /***public static PesquisaFragment newInstance(String param1, String param2) {
        PesquisaFragment fragment = new PesquisaFragment();
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
        view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        // nome no firebase
        nome = FireBase.UsuarioFirebase.UserAtual().getDisplayName();

        // referencía componentes da tela
        inicializarComponentes();

        // ativa methodos do searchView
        onSearchView();

        // config the adapter
        adapterSearch = new AdapterSearch( usuarioList, requireContext() );

        // config the recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter( adapterSearch );

        // referencia do database
        DatabaseReference db = FireBase.databaseRef();
        usuariosRef = db.child("users");

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                requireContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelected = usuarioList.get(position);
                        Intent i = new Intent(requireContext(), Perfil_Amigo_Activity.class);
                        i.putExtra("usuarioSelected", usuarioSelected);
                        startActivity( i );
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        return view;
    }

    private void inicializarComponentes(){
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        progressBar = view.findViewById(R.id.progressBarSearch);
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void onSearchView(){
        searchView.setQueryHint("Buscar usuarios");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // limpar lista
                usuarioList.clear();
                adapterSearch.notifyDataSetChanged();
                pesquisarUsuariosFirebase(newText);
                return true;
            }
        });
    }

    private void pesquisarUsuariosFirebase(String text){

        if( text.length() >= 2 ){
            Query query = usuariosRef.orderByChild("nome")
                    .startAt(text)
                    .endAt(text + "\uf8ff");

            progressBar.setVisibility(View.VISIBLE);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    usuarioList.clear();

                    for (DataSnapshot ds : snapshot.getChildren()){

                        Usuario usuario = ds.getValue( Usuario.class );
                        if ( !usuario.getId().equals( FireBase.UsuarioFirebase.IdUser() ) ){
                            usuarioList.add( usuario );
                        }


                    }
                    if( !usuarioList.isEmpty() ){
                        progressBar.setVisibility(View.INVISIBLE);
                        adapterSearch.notifyDataSetChanged();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }




    }

}