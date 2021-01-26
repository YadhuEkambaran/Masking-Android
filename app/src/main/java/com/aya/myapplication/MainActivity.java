package com.aya.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PATTERN = "##/##";

    private static final String DIGITS_FOR_TEXT = "0123456789 abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS_FOR_NUMBER = "0123456789 ";

    private int prevLength = 0;

    private EditText editText;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edt_text);
        editText.setKeyListener(DigitsKeyListener.getInstance(DIGITS_FOR_NUMBER));
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(PATTERN.length()), inputFilter});
        addWatcher();
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int after, int count) {
            formatter(charSequence, start);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void addWatcher() {
        editText.addTextChangedListener(textWatcher);
    }

    private void removeTextWatcher() {
        editText.removeTextChangedListener(textWatcher);
    }

    private final InputFilter inputFilter = (charSequence, start, end, dest, dStart, dEnd) -> {

        if (charSequence.toString().matches("[ ,.]")) {
            return "";
        }

        return null;
    };

    private void formatter(CharSequence sequence, int start) {
        char[] patternChars = PATTERN.toCharArray();

        SpannableStringBuilder charBuilder = new SpannableStringBuilder(sequence);
        String formattingText = charBuilder.toString().replaceAll("[^0-9]", "");
        if (formattingText.length() <= 0) {
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int patternPosition = 0;
        for (int i = 0; i < formattingText.length(); i++) {
            char textChar = formattingText.charAt(i);
            if (isInt(textChar)) {
                char patternChar = patternChars[patternPosition++];
                while (patternChar != '#') {
                    builder.append(patternChar);
                    patternChar = patternChars[patternPosition++];
                }
                builder.append(textChar);
            }
        }

        int position = builder.length();
        if (prevLength > builder.length()) {
            position = getPosition(start);
        }

        prevLength = builder.length();
        removeTextWatcher();
        editText.setText(builder);
        editText.setSelection(position);

        addWatcher();


        Log.d(TAG, "--- LENGTH ---" + builder.length());
    }

    private int getPosition(int position) {
        char[] patternChars = PATTERN.toCharArray();
        if (position == 0 || position == 1) {
            return position;
        }

        if (patternChars[position - 1] == '#') {
            return position;
        }
        return getPosition(--position);
    }

    private boolean isInt(char number) {
        try {
            Integer.parseInt(number + "");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}