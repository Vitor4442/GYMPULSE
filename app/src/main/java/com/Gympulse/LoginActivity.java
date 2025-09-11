package com.Gympulse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.Gympulse.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatDelegate;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        binding.loginCriarConta.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroActivity.class));
        });
        binding.buttonLogin.setOnClickListener(v -> validaDados());
    }
    private void validaDados(){
        String email = binding.criarEmail.getText().toString().trim();
        String senha = binding.criarSenha.getText().toString().trim();

        if(!email.isEmpty()){
            if (!senha.isEmpty()){
                binding.progressBar.setVisibility(View.VISIBLE);


                loginFirebase(email,senha);
            } else {
                Toast.makeText(this,"Informe sua senha.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Informe seu E-mail.", Toast.LENGTH_SHORT).show();
        }
    }
    private void loginFirebase(String email, String senha){
        mAuth.signInWithEmailAndPassword(
                email,senha
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                finish();
                startActivity(new Intent(this, Calculator_Macro.class));
            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this,"Ops!Ocorreu um erro!", Toast.LENGTH_SHORT).show();
            }

        });
    }

}