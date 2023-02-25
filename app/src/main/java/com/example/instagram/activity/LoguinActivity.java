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
import com.example.instagram.R;
import com.example.instagram.helper.FireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoguinActivity extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Intent cadastroActivity;
    private Intent mainActivity;
    private ProgressBar progressBar;
    private Button buttonLogar;

    private String email, senha;
    //private Usuario user;

    private String errorExc = "";

    @Override
    protected void onStart() {
        super.onStart();
        if (FireBase.UsuarioFirebase.UsuarioLogado()) {
            if (mainActivity != null) {
                startActivity(mainActivity);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguin);

        // inicializar componentes
        inicializarComponentes();

    }

    // inicializar componentes
    private void inicializarComponentes() {

        //ref das caixa de dados da IU
        buttonLogar = findViewById(R.id.buttonLogar);
        editEmail = findViewById(R.id.editTextEmail);
        editSenha = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressBarLog);
        progressBar.setVisibility(View.GONE);

        // click do batao
        logInApp();

        editEmail.requestFocus();

        // config Intents e activitys
        cadastroActivity = new Intent(this, CadastroActivity.class);
        mainActivity = new Intent(this, MainActivity.class);

    }

    // closed keyBoard
    private void closedKeyBoard() {
        View view = getWindow().getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // click do botao de logar
    public void logInApp() {

        buttonLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editEmail.getText().toString();
                senha = editSenha.getText().toString();

                closedKeyBoard();
                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        logUserInApp(email, senha);
                    } else {
                        Toast.makeText(LoguinActivity.this,
                                "Preencha a Senha!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoguinActivity.this,
                            "Preencha o E-mail!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // click do txt cadastro
    public void cadastreSeActivity(View view) {
        if (cadastroActivity != null)
            startActivity(cadastroActivity);
    }

    // logar no App
    public void logUserInApp(String email, String senha) {

        progressBar.setVisibility(View.VISIBLE);

        FireBase.authenticate().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);

                            if (mainActivity != null) {
                                startActivity(mainActivity);
                                finish();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoguinActivity.this,
                                    "Falha ao fazer o loguin", Toast.LENGTH_SHORT).show();

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}