package com.example.instagram.model;

import com.example.instagram.helper.FireBase;
import com.google.firebase.database.DatabaseReference;

public class Postagem {
    String idUser;
    String idpostagem;
    String descricao;
    String urlfoto;

    public Postagem(){
        DatabaseReference databaseRef = FireBase.databaseRef();
        DatabaseReference postagemRef = databaseRef.child("postagens");
        // gerar id da postagem
        String idPostagem = postagemRef.push().getKey();
        setIdpostagem( idPostagem );
    }

    public boolean salvar(){
        DatabaseReference databaseRef = FireBase.databaseRef();
        DatabaseReference postagensRef = databaseRef.child("postagens")
                .child( getIdUser() )
                .child( getIdpostagem() );
        postagensRef.setValue(this);

        return true;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdpostagem() {
        return idpostagem;
    }

    public void setIdpostagem(String idpostagem) {
        this.idpostagem = idpostagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlfoto() {
        return urlfoto;
    }

    public void setUrlfoto(String urlfoto) {
        this.urlfoto = urlfoto;
    }
}
