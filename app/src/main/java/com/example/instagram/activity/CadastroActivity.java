package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.helper.FireBase;
import com.example.instagram.R;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText editEmail, editSenha, editNome;
    private Intent mainActivity;
    private Button buttonCadastrar;
    private ProgressBar progressBar;
    private String nome, email, senha;
    private Usuario user;

    private String errorExc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //ref das caixa de dados da IU
        inicializarComponentes();
    }

    // inicializar componentes
    private void inicializarComponentes(){

        //ref das caixa de dados da IU
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
        editNome = findViewById(R.id.editTextNome);
        editEmail = findViewById(R.id.editTextEmail);
        editSenha = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBarCad);
        progressBar.setVisibility( View.GONE );

        // click do botao
        btnCadastrar();

        editNome.requestFocus();

        // config Intents e activitys
        mainActivity = new Intent(this, MainActivity.class);
    }

    // closed keyBoard
    private void closedKeyBoard(){
        View view = getWindow().getCurrentFocus();
        if(view != null){
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow( view.getWindowToken(),0);
        }
    }

    //Click do button cadastrar
    public void btnCadastrar(){
        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nome = editNome.getText().toString();
                email = editEmail.getText().toString();
                senha = editSenha.getText().toString();

                closedKeyBoard();
                validarDados();
            }
        });
    }

    private void validarDados(){
        if ( !nome.isEmpty() ){
            if ( !email.isEmpty() ){
                if ( !senha.isEmpty() ){

                    user = new Usuario();
                    user.setNome(nome);
                    user.setEmail(email);
                    user.setSenha(senha);

                    cadastrarFireBase(user);

                }else{
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a Senha!", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this,
                        "Preencha o E-mail!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(CadastroActivity.this,
                    "Preencha o Nome!", Toast.LENGTH_LONG).show();
        }
    }


    // cadastrar usuarios no firebase
    private void cadastrarFireBase(Usuario user){

        progressBar.setVisibility(View.VISIBLE);

        FireBase.authenticate().createUserWithEmailAndPassword( email, senha )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            try{
                                progressBar.setVisibility( View.GONE );

                                // salvar dados do usuario
                                String id = task.getResult().getUser().getUid();
                                user.setId( id );
                                user.salvarUser();

                                //salvar nome no profile do firebase
                                FireBase.UsuarioFirebase.AtulizarDadosUser( nome, null );

                                Toast.makeText(CadastroActivity.this, "Usuario cadastrado com sucesso!", Toast.LENGTH_LONG).show();
                                if (mainActivity != null){
                                    startActivity(mainActivity);
                                    finish();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        else{
                            progressBar.setVisibility( View.GONE );

                            try{
                                throw task.getException();

                            } catch (FirebaseAuthWeakPasswordException passwordException){
                                errorExc = "Digite uma senha mais forte!";

                            } catch (FirebaseAuthInvalidCredentialsException invalidCredentials){
                                errorExc = "Digite um e-mail válido!";

                            } catch (FirebaseAuthUserCollisionException collision){
                                errorExc = "Uma conta com esse E-mail já foi cadastrada no sistema!";

                            } catch (Exception e){
                                errorExc = "Erro ao cadastrar usuario: "+ e.getMessage() ;
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroActivity.this, errorExc, Toast.LENGTH_SHORT).show();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}