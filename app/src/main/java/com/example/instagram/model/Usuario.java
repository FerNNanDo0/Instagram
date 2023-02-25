package com.example.instagram.model;

import com.example.instagram.helper.FireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;
    private int posts, seguidores, seguindo;

    DatabaseReference db ;
    DatabaseReference userRef;

    public Usuario(){}

    // salvar dados no firebase
    public void salvarUser(){

//        id = FireBase.IdUser();

        db = FireBase.databaseRef();
        userRef = db.child("users").child( getId() );
        userRef.setValue( this );
    }

    public void atualizarDados(){
        db = FireBase.databaseRef();
        userRef = db.child("users").child( getId() );

        Map<String, Object> userMap = converterMap();
        userRef.updateChildren( userMap );
    }

    public Map<String, Object> converterMap(){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail() );
        userMap.put("nome", getNome() );
        userMap.put("id", getId() );
        userMap.put("foto", getFoto() );
        userMap.put("posts", getPosts());
        userMap.put("seguindo", getSeguindo() );
        userMap.put("seguidores", getSeguidores() );

        return userMap;
    }


    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
