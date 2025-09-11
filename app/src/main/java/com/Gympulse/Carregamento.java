package com.Gympulse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatDelegate;

import androidx.appcompat.app.AppCompatActivity;

public class Carregamento extends AppCompatActivity {
    private static final int TIME_OUT = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carregamento); // conecta no seu XML

        // pega a logo
        ImageView logo = findViewById(R.id.logo);

        // animação
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.startAnimation(anim);

        // depois do tempo, vai pra MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Carregamento.this, LoginActivity.class);
            startActivity(intent);
            finish(); // fecha a splash para não voltar nela
        }, TIME_OUT);
    }
}