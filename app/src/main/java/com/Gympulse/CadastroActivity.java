package com.Gympulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.Gympulse.data.dataAlimentos.AcessoAoBancoDeDadosMacro;
import com.Gympulse.databinding.ActivityCadastroBinding;
import com.google.firebase.auth.FirebaseAuth;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        binding.buttonLogin.setOnClickListener(v -> validaDados());
    }

    private void validaDados(){
        String email = binding.criarEmail.getText().toString().trim();
        String senha = binding.criarSenha.getText().toString().trim();

        if(!email.isEmpty()){
            if (!senha.isEmpty()){
                binding.progressBar.setVisibility(View.VISIBLE);
                criarContaFirebase(email,senha);
            } else {
                Toast.makeText(this,"Informe sua senha.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Informe seu E-mail.", Toast.LENGTH_SHORT).show();
        }
    }

    private void criarContaFirebase(String email, String senha){
        mAuth.createUserWithEmailAndPassword(
                email,senha
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                finish();
                startActivity(new Intent(this, AcessoAoBancoDeDadosMacro.class));
            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this,"Ops!Ocorreu um erro!", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
