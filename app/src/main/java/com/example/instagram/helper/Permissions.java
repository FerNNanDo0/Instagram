package com.example.instagram.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {
    static boolean temPermission;

    public static boolean validatePermissions(String[] permissions, Activity activity, int requestCode){
        if ( Build.VERSION.SDK_INT >= 23 ){

            List<String> listPermission = new ArrayList<>();

            //percorrer permissions passadas verificando uma a uma
            for ( String permiss : permissions ){
                temPermission = ContextCompat.checkSelfPermission(activity, permiss)
                        == PackageManager.PERMISSION_GRANTED;

                if ( !temPermission ){
                    listPermission.add( permiss );
                }
            }

            // caso a lista nao esteja vazia, nao e necessario solitar permissao
            if ( listPermission.isEmpty() ){
                return true;
            }

            String[] neewPerssion = new String[ listPermission.size() ];
            listPermission.toArray( neewPerssion );

            //solitar permissao
            ActivityCompat.requestPermissions( activity, neewPerssion, requestCode );

        }
        return true;
    }
}
