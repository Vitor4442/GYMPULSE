package com.Gympulse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Calculator_Macro extends AppCompatActivity {

    private TextInputEditText etFoodName, etFoodGrams;
    private TextView tvProtein, tvCarbs, tvFat, tvCalories;
    private MaterialButton btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_macro);

        // Referências dos elementos do layout
        etFoodName = findViewById(R.id.etFoodName);
        etFoodGrams = findViewById(R.id.etFoodGrams);
        tvProtein = findViewById(R.id.tvProtein);
        tvCarbs = findViewById(R.id.tvCarbs);
        tvFat = findViewById(R.id.tvFat);
        tvCalories = findViewById(R.id.tvCalories);
        btnCalculate = findViewById(R.id.btnCalculate);

        // Botão para calcular macros
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alimento = etFoodName.getText().toString().trim();
                String gramasStr = etFoodGrams.getText().toString().trim();

                if (!alimento.isEmpty() && !gramasStr.isEmpty()) {
                    double gramas = Double.parseDouble(gramasStr);
                    buscarAlimento(alimento, gramas);
                }
            }
        });
    }

    private void buscarAlimento(String alimento, double quantidadeGramas) {
        String apiKey = "pomGmDoh7cXfn4moNYFmshvRclMNN4bTYb9WmKoQ";
        String urlString = "https://api.nal.usda.gov/fdc/v1/foods/search?query="
                + alimento + "&api_key=" + apiKey;

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("GET");

                int responseCode = conexao.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                    StringBuilder resposta = new StringBuilder();
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        resposta.append(linha);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(resposta.toString());
                    JSONArray foodsArray = jsonObject.getJSONArray("foods");

                    if (foodsArray.length() > 0) {
                        JSONObject firstFood = foodsArray.getJSONObject(0);
                        JSONArray nutrientsArray = firstFood.getJSONArray("foodNutrients");

                        double protein = 0, carbs = 0, fat = 0, calories = 0;

                        for (int i = 0; i < nutrientsArray.length(); i++) {
                            JSONObject nutrient = nutrientsArray.getJSONObject(i);
                            String nome = nutrient.getString("nutrientName").toLowerCase();
                            double valor = nutrient.getDouble("value");
                            String unidade = nutrient.has("unitName") ? nutrient.getString("unitName") : "";

                            switch (nome) {
                                case "protein":
                                    protein = valor;
                                    break;
                                case "carbohydrate, by difference":
                                case "carbohydrate":
                                    carbs = valor;
                                    break;
                                case "total lipid (fat)":
                                case "fat":
                                    fat = valor;
                                    break;
                                case "energy":
                                    // Verifica se unidade é kcal, senão converte de kj para kcal
                                    if (unidade.equalsIgnoreCase("kcal")) {
                                        calories = valor;
                                    } else if (unidade.equalsIgnoreCase("kj")) {
                                        calories = valor / 4.184; // 1 kcal = 4,184 kJ
                                    }
                                    break;
                            }
                        }

                        // Ajustar pelos gramas inseridos pelo usuário
                        double fator = quantidadeGramas / 100.0;
                        protein *= fator;
                        carbs *= fator;
                        fat *= fator;
                        calories *= fator;

                        double finalProtein = protein;
                        double finalCarbs = carbs;
                        double finalFat = fat;
                        double finalCalories = calories;

                        runOnUiThread(() -> {
                            tvProtein.setText(String.format("Proteína: %.2f g", finalProtein));
                            tvCarbs.setText(String.format("Carboidratos: %.2f g", finalCarbs));
                            tvFat.setText(String.format("Gorduras: %.2f g", finalFat));
                            tvCalories.setText(String.format("Calorias: %.2f kcal", finalCalories));
                        });
                    } else {
                        Log.e("USDA", "Nenhum alimento encontrado para: " + alimento);
                    }
                } else {
                    Log.e("USDA", "Erro na requisição: " + responseCode);
                }

                conexao.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
