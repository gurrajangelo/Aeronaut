package com.example.aeronaut;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    double mCrediti = 100.00;
    double mPuntata = 0.00;
    double mMoltiplicatore = 1.00;
    double mStop = 1.00;
    boolean mAttivo = false;
    boolean mRitirato = false;

    TextView mTxtCrediti, mTxtMoltiplicatore;
    EditText mEditPuntata;
    Button mBtnStart;
    Random mRandom = new Random();

    CountDownTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mTxtCrediti = findViewById(R.id.credits_txt);
        mTxtMoltiplicatore = findViewById(R.id.multiplier_txt);
        mEditPuntata = findViewById(R.id.bet_input);
        mBtnStart = findViewById(R.id.start_button);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAttivo) {
                    String testo = mEditPuntata.getText().toString();

                    if (testo.isEmpty()) {
                        mPuntata = 0;
                    } else {
                        mPuntata = Integer.parseInt(testo);
                    }

                    if (mPuntata > mCrediti) {
                        Toast.makeText(MainActivity.this, "Crediti insufficienti", Toast.LENGTH_SHORT).show();
                    } else {
                        mCrediti = mCrediti - mPuntata;
                        mTxtCrediti.setText("Crediti: " + mCrediti);

                        mMoltiplicatore = 1.00;
                        mTxtMoltiplicatore.setText("1.00x");
                        mStop = 0.99 / Math.max(0.01, mRandom.nextDouble());

                        mAttivo = true;
                        mRitirato = false;
                        mEditPuntata.setEnabled(false);
                        mBtnStart.setText("RITIRA");

                        mTimer = new CountDownTimer(30000, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (mMoltiplicatore >= mStop) {
                                    mTimer.cancel();
                                    mAttivo = false;
                                    mEditPuntata.setEnabled(true);
                                    mBtnStart.setText("START");
                                    mTxtMoltiplicatore.setText("CRASH!");
                                } else {
                                    mMoltiplicatore = mMoltiplicatore + 0.02;
                                    double arrotondato = Math.round(mMoltiplicatore * 100.0) / 100.0;
                                    mTxtMoltiplicatore.setText(arrotondato + "x");
                                }
                            }

                            @Override
                            public void onFinish() {
                                mAttivo = false;
                                mEditPuntata.setEnabled(true);
                                mBtnStart.setText("START");
                            }
                        };
                        mTimer.start();
                    }
                } else {
                    if (!mRitirato) {
                        mRitirato = true;
                        int vincita = (int) Math.round(mPuntata * mMoltiplicatore);
                        mCrediti = mCrediti + vincita;
                        mTxtCrediti.setText("Crediti: " + mCrediti);

                        mBtnStart.setText("INCASSATO!");
                    }
                }
            }
        });
    }
}