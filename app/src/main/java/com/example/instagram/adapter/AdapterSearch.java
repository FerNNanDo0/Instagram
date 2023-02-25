package com.example.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.helper.FireBase;
import com.example.instagram.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    private List<Usuario> usuarioList;
    private Context context;

    public AdapterSearch(List<Usuario> List, Context cont) {
        usuarioList = List;
        context = cont;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_search, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = usuarioList.get(position);
        String idUserAtual = FireBase.UsuarioFirebase.IdUser();

        if( usuario.getId().equals(idUserAtual) ){
            holder.layoutAdapter.setVisibility(View.INVISIBLE);
        }
        else{
            holder.nome.setText(usuario.getNome());
            if (usuario.getFoto() != null) {
                Glide.with(context).load(Uri.parse(usuario.getFoto())).into(holder.foto);
            } else {
                holder.foto.setImageResource(R.drawable.padrao);
            }
        }



    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView foto;
        private TextView nome;
        LinearLayout layoutAdapter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewSearch);
            nome = itemView.findViewById(R.id.nomeSearch);
            layoutAdapter = itemView.findViewById(R.id.layoutAdapter);
        }
    }

}
