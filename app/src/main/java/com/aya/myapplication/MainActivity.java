package com.aya.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String PATTERN = "##/##";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = findViewById(R.id.edt_text);
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789/"));
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PATTERN.length()), inputFilter});
    }

    private final InputFilter inputFilter = (charSequence, start, end, dest, dStart, dEnd) -> {
        if (dStart == PATTERN.length() || (start  == 0 && end == 0)) return null;
        return formatValue(charSequence, dest, dStart);
    };

    private CharSequence formatValue(CharSequence charSequence, Spanned dest, int charPosition) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        while (PATTERN.charAt(charPosition) != '#') {
            builder.append(PATTERN.charAt(charPosition++));
        }

        return builder.append(charSequence);
    }
}