package com.example.excessbaggagecalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etWeight, etRate;
    private RadioGroup rgClass;
    private RadioButton rbEconomy;
    private TextView tvExcessWeight, tvExcessCharge, tvTotalPayable;
    private Button btnCalculate, btnReset;

    private static final String GITHUB_REPO_URL = "https://github.com/arifrusyaidi/excess-baggage-calculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        rgClass = findViewById(R.id.rgClass);
        rbEconomy = findViewById(R.id.rbEconomy);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateExcessBaggage();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });
    }

    private void calculateExcessBaggage() {
        String weightStr = etWeight.getText() != null ? etWeight.getText().toString().trim() : "";
        String rateStr = etRate.getText() != null ? etRate.getText().toString().trim() : "";

        if (weightStr.isEmpty()) {
            etWeight.setError(getString(R.string.err_empty));
            etWeight.requestFocus();
            return;
        }

        if (rateStr.isEmpty()) {
            etRate.setError(getString(R.string.err_empty));
            etRate.requestFocus();
            return;
        }

        double baggageWeight;
        double ratePerKg;

        try {
            baggageWeight = Double.parseDouble(weightStr);
            ratePerKg = Double.parseDouble(rateStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.err_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        if (baggageWeight < 0 || ratePerKg < 0) {
            Toast.makeText(this, "Values cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        double allowanceX = rbEconomy.isChecked() ? 20.0 : 40.0;
        double excessWeight = Math.max(0.0, baggageWeight - allowanceX);
        double excessCharge = excessWeight * ratePerKg;
        double totalPayable = excessCharge + (0.08 * excessCharge);

        tvExcessWeight.setText(String.format(Locale.getDefault(), "Excess Baggage Weight: %.2f kg", excessWeight));
        tvExcessCharge.setText(String.format(Locale.getDefault(), "Excess Baggage Charge: RM %.2f", excessCharge));
        tvTotalPayable.setText(String.format(Locale.getDefault(), "Total Amount Payable (incl. 8%% SST): RM %.2f", totalPayable));
    }

    private void resetFields() {
        etWeight.setText("");
        etRate.setText("");
        etWeight.setError(null);
        etRate.setError(null);
        rbEconomy.setChecked(true);
        tvExcessWeight.setText("Excess Baggage Weight: 0.00 kg");
        tvExcessCharge.setText("Excess Baggage Charge: RM 0.00");
        tvTotalPayable.setText("Total Amount Payable (incl. 8% SST): RM 0.00");
        etWeight.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            shareAppUrl();
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareAppUrl() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "Check out the SkyExcess Baggage Estimator app on GitHub:\n" + GITHUB_REPO_URL;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Excess Baggage Fee Estimator App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
