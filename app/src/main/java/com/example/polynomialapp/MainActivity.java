package com.example.polynomialapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PolynomialDbHelper dbHelper;
    private SQLiteDatabase db;
    private LinearLayout coefficientsContainer;
    private EditText degreeInput;
    private Button generateFieldsButton;
    private Button saveButton;
    private List<EditText> coefficientInputs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        degreeInput = findViewById(R.id.degreeInput);
        generateFieldsButton = findViewById(R.id.generateFieldsButton);
        coefficientsContainer = findViewById(R.id.coefficientsContainer);
        saveButton = findViewById(R.id.saveButton);

        coefficientInputs = new ArrayList<>();
        dbHelper = new PolynomialDbHelper(this);
        db = dbHelper.getWritableDatabase();

        generateFieldsButton.setOnClickListener(v -> generateCoefficientFields());
        saveButton.setOnClickListener(v -> savePolynomial());

        saveButton.setVisibility(View.GONE); // Hide save button initially
    }

    private void generateCoefficientFields() {
        coefficientsContainer.removeAllViews();
        coefficientInputs.clear(); // Clear previous inputs

        int degree;
        try {
            degree = Integer.parseInt(degreeInput.getText().toString().trim());
            if (degree < 1 || degree > 99) {
                Toast.makeText(MainActivity.this, "Degree must be between 1-99", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Invalid degree", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i <= degree; i++) {
            EditText coefficientField = new EditText(MainActivity.this);
            coefficientField.setHint("Coefficient " + i);
            coefficientField.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            coefficientsContainer.addView(coefficientField);
            coefficientInputs.add(coefficientField); // Store field reference
        }

        saveButton.setVisibility(View.VISIBLE); // Show save button after generating fields
    }

    private void savePolynomial() {
        String degreeText = degreeInput.getText().toString().trim();
        if (degreeText.isEmpty() || coefficientInputs.isEmpty()) {
            Toast.makeText(this, "Please generate and fill all coefficient fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int degree = Integer.parseInt(degreeText);
        List<Double> coefficients = new ArrayList<>();

        for (EditText input : coefficientInputs) {
            String coeffText = input.getText().toString().trim();
            if (coeffText.isEmpty()) {
                Toast.makeText(this, "All coefficients must be filled.", Toast.LENGTH_SHORT).show();
                return;
            }
            coefficients.add(Double.parseDouble(coeffText));
        }

        savePolynomialToDatabase(degree, coefficients);
        showPolynomialDialog(degree, coefficients); // Show polynomial in AlertDialog
    }

    private void savePolynomialToDatabase(int degree, List<Double> coefficients) {
        ContentValues values = new ContentValues();
        values.put("degree", degree);
        values.put("coefficients", coefficients.toString());

        long result = db.insert("polynomials", null, values);
        if (result == -1) {
            Toast.makeText(this, "Error saving polynomial!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Polynomial saved successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPolynomialDialog(int degree, List<Double> coefficients) {
        String formattedPolynomial = formatPolynomial(degree, coefficients);

        new AlertDialog.Builder(this)
                .setTitle("Polynomial Saved")
                .setMessage("f(x) = " + formattedPolynomial)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Clear input fields after clicking OK
                    degreeInput.setText("");
                    coefficientsContainer.removeAllViews();
                    coefficientInputs.clear();
                    saveButton.setVisibility(View.GONE); // Hide save button after saving
                })
                .show();
    }

    private String formatPolynomial(int degree, List<Double> coefficients) {
        StringBuilder polynomial = new StringBuilder();
        boolean isFirstTerm = true;

        for (int i = 0; i <= degree; i++) {
            double coeff = coefficients.get(i);
            if (coeff != 0) {
                if (!isFirstTerm) {
                    polynomial.append(" ");
                    polynomial.append(coeff > 0 ? "+ " : "- ");
                    coeff = Math.abs(coeff);
                } else {
                    isFirstTerm = false;
                }

                String formattedCoeff = (coeff % 1 == 0) ? String.valueOf((int) coeff) : String.valueOf(coeff);

                if (i == 0) {
                    polynomial.append(formattedCoeff);
                } else if (i == 1) {
                    polynomial.append(formattedCoeff).append("x");
                } else {
                    polynomial.append(formattedCoeff).append("x").append(toSuperscript(i));
                }
            }
        }
        return polynomial.toString();
    }

    private String toSuperscript(int number) {
        String normalDigits = "0123456789";
        String superscriptDigits = "⁰¹²³⁴⁵⁶⁷⁸⁹";

        StringBuilder superscript = new StringBuilder();
        String numStr = String.valueOf(number);

        for (char digit : numStr.toCharArray()) {
            int index = normalDigits.indexOf(digit);
            if (index != -1) {
                superscript.append(superscriptDigits.charAt(index));
            }
        }
        return superscript.toString();
    }
}

