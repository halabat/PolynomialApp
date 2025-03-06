package com.example.polynomialapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private PolynomialDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText degreeInput = findViewById(R.id.degreeInput);
        EditText coefficientsInput = findViewById(R.id.coefficientsInput);
        Button saveButton = findViewById(R.id.saveButton);
        TextView polynomialOutput = findViewById(R.id.polynomialOutput);

        dbHelper = new PolynomialDbHelper(this);
        db = dbHelper.getWritableDatabase();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String degreeText = degreeInput.getText().toString().trim();
                    String coefficientsText = coefficientsInput.getText().toString().trim();

                    // בדיקה אם השדות ריקים
                    if (degreeText.isEmpty() || coefficientsText.isEmpty()) {
                        showErrorDialog("שגיאה", "נא למלא את כל השדות!");
                        return;
                    }

                    int degree = Integer.parseInt(degreeText);
                    String[] coeffs = coefficientsText.split(",");
                    List<Double> coefficients = new ArrayList<>();

                    // בדיקה אם מספר המקדמים תואם לדרגת הפולינום
                    if (coeffs.length != degree + 1) {
                        showErrorDialog("שגיאה", "מספר המקדמים שהוזנו אינו תואם לדרגת הפולינום!");
                        return;
                    }

                    // המרת המקדמים ממחרוזת למספרים
                    for (String coeff : coeffs) {
                        coefficients.add(Double.parseDouble(coeff.trim()));
                    }

                    savePolynomial(degree, coefficients);
                    String polynomialString = formatPolynomial(degree, coefficients);
                    polynomialOutput.setText("f(x) = " + polynomialString);

                    // ריקון השדות לאחר שמירה
                    degreeInput.setText("");
                    coefficientsInput.setText("");

                } catch (NumberFormatException e) {
                    showErrorDialog("שגיאה", "נא להזין מספרים תקינים בלבד!");
                }
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("אישור", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    private void savePolynomial(int degree, List<Double> coefficients) {
        ContentValues values = new ContentValues();
        values.put("degree", degree);
        values.put("coefficients", coefficients.toString());

        long result = db.insert("polynomials", null, values);
        if (result == -1) {
            Toast.makeText(this, "שגיאה בשמירת הפולינום!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "פולינום נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
        }
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


    private String formatPolynomial(int degree, List<Double> coefficients) {
        StringBuilder polynomial = new StringBuilder();
        boolean isFirstTerm = true;

        for (int i = 0; i <= degree; i++) {
            double coeff = coefficients.get(i);
            if (coeff != 0) {
                if (!isFirstTerm) {
                    polynomial.append(" ");
                    if (coeff > 0) {
                        polynomial.append("+ ");
                    } else {
                        polynomial.append("- ");
                        coeff = Math.abs(coeff);
                    }
                } else {
                    isFirstTerm = false;
                }

                // הצגת מספר שלם ללא `.0`
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

}