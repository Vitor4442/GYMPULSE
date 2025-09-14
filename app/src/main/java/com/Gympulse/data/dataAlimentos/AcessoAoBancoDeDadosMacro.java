package com.Gympulse.data.dataAlimentos;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.Gympulse.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class AcessoAoBancoDeDadosMacro extends AppCompatActivity {


    private TextInputEditText etFoodName, etFoodGrams;
    private MaterialButton btnCalculate;
    private FirebaseFirestore db;

    // TextViews para exibir os macros
    private TextView tvProtein, tvCarbs, tvFat, tvCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_macro);

        // Layout
        etFoodName = findViewById(R.id.etFoodName);
        etFoodGrams = findViewById(R.id.etFoodGrams);
        btnCalculate = findViewById(R.id.btnCalculate);

        tvProtein = findViewById(R.id.tvProtein);
        tvCarbs = findViewById(R.id.tvCarbs);
        tvFat = findViewById(R.id.tvFat);
        tvCalories = findViewById(R.id.tvCalories);

        // Inicializa instância secundária do Firebase dentro do onCreate
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:702281146014:android:0568916815d423bf88e58f")
                .setApiKey("AIzaSyDaiLC2RNmUm9RgqM38Mq0yJeGzGYR4igU")
                .setProjectId("gympulse-api")
                .build();

        FirebaseApp alimentosApp = null;
        try {
            alimentosApp = FirebaseApp.getInstance("alimentosApp"); // tenta pegar se já existe
        } catch (IllegalStateException e) {
            alimentosApp = FirebaseApp.initializeApp(getApplicationContext(), options, "alimentosApp");
        }

        FirebaseFirestore dbAlimentos = FirebaseFirestore.getInstance(alimentosApp);

        // Botão de busca
        btnCalculate.setOnClickListener(v -> {
            String alimento = etFoodName.getText().toString().trim();
            String strGramas = etFoodGrams.getText().toString().trim();

            if (alimento.isEmpty() || strGramas.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double gramas;
            try {
                gramas = Double.parseDouble(strGramas);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Digite um valor válido em gramas", Toast.LENGTH_SHORT).show();
                return;
            }

            buscarAlimento(dbAlimentos, alimento, gramas);
        });
    }

    // Passa dbAlimentos como parâmetro
    private void buscarAlimento(FirebaseFirestore dbAlimentos, String nomeAlimento, double gramas) {
        dbAlimentos.collection("alimentos")
                .document(nomeAlimento)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        // Proteínas
                        double proteinas = parseNumberField(documentSnapshot.get("Proteínas (g)"));

                        // Carboidratos
                        double carboidratos = parseNumberField(documentSnapshot.get("Carboidratos (g)"));

                        // Gorduras
                        double gorduras = parseNumberField(documentSnapshot.get("Gorduras (g)"));

                        // Calorias
                        double calorias = parseNumberField(documentSnapshot.get("Calorias (kcal)"));

                        // Calcula os macros de acordo com a quantidade digitada
                        double fator = gramas / 100.0;
                        tvProtein.setText("Proteínas: " + String.format("%.2f g", proteinas * fator));
                        tvCarbs.setText("Carboidratos: " + String.format("%.2f g", carboidratos * fator));
                        tvFat.setText("Gorduras: " + String.format("%.2f g", gorduras * fator));
                        tvCalories.setText("Calorias: " + String.format("%.2f kcal", calorias * fator));

                        Log.d("Alimento", "Alimento encontrado: " + nomeAlimento);

                    } else {
                        Toast.makeText(this, "Alimento não encontrado", Toast.LENGTH_SHORT).show();
                        tvProtein.setText("-");
                        tvCarbs.setText("-");
                        tvFat.setText("-");
                        tvCalories.setText("-");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao acessar o Firestore", Toast.LENGTH_SHORT).show();
                    Log.w("FIRESTORE", "Erro ao acessar documento", e);
                });
    }

    // Método auxiliar para converter qualquer tipo de campo em double
    private double parseNumberField(Object field) {
        if (field == null) return 0;

        if (field instanceof Number) {
            return ((Number) field).doubleValue();
        } else if (field instanceof String) {
            try {
                return Double.parseDouble((String) field);
            } catch (NumberFormatException e) {
                Log.w("FIRESTORE", "Erro ao converter campo String para número: " + field);
                return 0;
            }
        } else {
            Log.w("FIRESTORE", "Campo não é número nem String: " + field);
            return 0;
        }
    }
}
