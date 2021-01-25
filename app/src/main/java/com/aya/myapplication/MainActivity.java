package com.aya.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PATTERN = "(###) ### ####";

    private static final String DIGITS_FOR_TEXT = "0123456789 abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS_FOR_NUMBER = "0123456789() ";

    private boolean isBackPressed = false;
    private boolean isForwardFormatting = false;

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
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (isForwardFormatting) {
                formatter(charSequence);
            }

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
        Log.d(TAG, "--- charSequence -- " + charSequence);
        Log.d(TAG, "--- start -- " + start);
        Log.d(TAG, "--- end -- " + end);
        Log.d(TAG, "--- dest -- " + dest);
        Log.d(TAG, "--- dStart -- " + dStart);
        Log.d(TAG, "--- dEnd -- " + dEnd);
        if (charSequence.toString().matches("[ ,.]")) {
            return "";
        }

        if (editText.getInputType() == InputType.TYPE_CLASS_TEXT) {
            if ((end - start) > (dEnd - dStart)) {
                return textFormatter(charSequence, dest.toString(), dEnd);
            } else {
                return null;
            }
        } else {
            if ((start == 0 && end == 0)) {
                if (dStart < editText.getText().length()) {
                    numberBackFormatter(dStart, dEnd);
                }
                return null;
            } else if (((dStart - dEnd) == 0)) {
                if (charSequence.length() == 1) {
                    return numberFormatter(charSequence, dStart);
                }
            }

            return null;
        }
    };

    private CharSequence textFormatter(CharSequence charSequence, String dest, int charPosition) {
        int lastPosition = dest.lastIndexOf(" ");
        if (lastPosition != -1) {
            dest = dest.substring(lastPosition + 1);
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(dest);
        Log.d("-- textFormatter --", "-- DEST -- " + dest);
        Log.d("-- textFormatter --", "-- charSequence -- " + charSequence);
        while (PATTERN.charAt(charPosition) != '#') {
            builder.append(PATTERN.charAt(charPosition));
            charPosition = charPosition + 1;
        }
        builder.append(charSequence.charAt(charSequence.length() - 1));
        return builder.toString().toUpperCase();
    }

    private CharSequence numberFormatter(CharSequence charSequence, int charPosition) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        while (PATTERN.charAt(charPosition) != '#') {
            char letter = PATTERN.charAt(charPosition);
            builder.append(letter);
            ++charPosition;
        }

        if (isBackPressed) {
            isForwardFormatting = true;
            isBackPressed = false;
        }
        return builder.append(charSequence);
    }

    private void numberBackFormatter(int dStart, int dEnd) {
        char[] patternChars = PATTERN.toCharArray();

        SpannableStringBuilder charBuilder = new SpannableStringBuilder(editText.getText().toString());
        charBuilder.replace(dStart, dEnd, "");
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
                if (patternChar != '#') {
                    builder.append(patternChar);
                    ++patternPosition;
                }
                builder.append(textChar);
            }
        }

        removeTextWatcher();
        editText.setText(builder);
        int position = dStart > builder.length() ? dStart - 1 : dStart;
        editText.setSelection(position);
        isBackPressed = true;
        addWatcher();
    }

    private void formatter(CharSequence sequence) {
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
                if (patternChar != '#') {
                    builder.append(patternChar);
                    ++patternPosition;
                }
                builder.append(textChar);
            }
        }

        isForwardFormatting = false;
        removeTextWatcher();
        editText.setText(builder);
        editText.setSelection(builder.length());
        addWatcher();
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