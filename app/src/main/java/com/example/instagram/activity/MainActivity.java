package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.instagram.R;

import com.example.instagram.helper.FireBase;
import com.example.instagram.navigation.FeedFragment;
import com.example.instagram.navigation.PerfilFragment;
import com.example.instagram.navigation.PesquisaFragment;
import com.example.instagram.navigation.PostagemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

//import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    FrameLayout frameLayout;

    Fragment fragmento;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onStart() {
        super.onStart();
        if (!FireBase.UsuarioFirebase.UsuarioLogado()) {
            startActivity(new Intent(this, LoguinActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.viewPager);

        //View view = getWindow().findViewById(R.id.toolba);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Instagram");
        toolbar.setElevation(3);
        setSupportActionBar(toolbar);

        fragmento = new FeedFragment();
        openFragment(fragmento);
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    // open  fragments
    private void openFragment(Fragment fragment){
        // config fragments
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    // click navigation
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                try{
                    fragmento = new FeedFragment();
                    openFragment(fragmento);
                    break;

                }catch (Exception e){
                    e.printStackTrace();
                }

            case R.id.ItemPesquisar:

                fragmento = new PesquisaFragment();
                openFragment(fragmento);
                break;

            case R.id.ItemPostagem:

                fragmento = new PostagemFragment();
                openFragment(fragmento);
                break;

            case R.id.ItemPerfil:

                fragmento = new PerfilFragment();
                openFragment(fragmento);
                break;
        }

        return true;
    }


    // methodo criar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // methodo click ou acao do menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sair:
                if (FireBase.UsuarioFirebase.UsuarioLogado()) {
                    try {
                        FireBase.authenticate().signOut();
                        if (!FireBase.UsuarioFirebase.UsuarioLogado()) {
                            startActivity(new Intent(this, LoguinActivity.class));
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}