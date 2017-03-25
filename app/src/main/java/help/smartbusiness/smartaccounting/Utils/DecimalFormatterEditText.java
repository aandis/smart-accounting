package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by gamerboy on 3/13/17.
 */

public class DecimalFormatterEditText extends MaterialEditText {

    public static final String TAG = DecimalFormatterEditText.class.getSimpleName();

    public DecimalFormatterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // This works because setText is only called when we set the value programmatically.
        // If a user enters the value is the EditText setText is not called.
        String formatted = Utils.convertLongToDecimal(text);
        if (formatted != null) {
            super.setText(formatted, type);
            return;
        }
        super.setText(text, type);
    }

    public long rawValue() {
        double value = Utils.parseDouble(getText().toString());
        if (value != -1) {
            return Math.round(value * 100);
        }
        return -1L;
    }

    class DecimalDigitsInputFilter implements InputFilter {

        private final int decimalDigits;

        DecimalDigitsInputFilter(int decimalDigits) {
            this.decimalDigits = decimalDigits;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int dotPos = -1;
            int len = dest.length();
            for (int i = 0; i < len; i++) {
                char c = dest.charAt(i);
                if (c == '.') {
                    dotPos = i;
                    break;
                }
            }
            if (dotPos >= 0) {
                // protects against many dots
                if (source.equals(".") || source.equals(",")) {
                    return "";
                }
                // if the text is entered before the dot
                if (dend <= dotPos) {
                    return null;
                }
                if (len - dotPos > decimalDigits) {
                    return "";
                }
            } else if (source.equals(".")) {
                // If the first dot is being entered, make
                // sure it has at most 2 digits after it.
                if (len - dend > decimalDigits) {
                    return "";
                }
            }
            return null;
        }
    }
}
