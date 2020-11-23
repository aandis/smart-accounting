package help.smartbusiness.smartaccounting.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.blackcat.currencyedittext.CurrencyTextFormatter;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Currency;
import java.util.Locale;

import help.smartbusiness.smartaccounting.SmartAccounting;

/**
 * Created by gamerboy on 26/3/17.
 * A {@link MaterialEditText} which supports showing currency
 * values supported by {@link com.blackcat.currencyedittext.CurrencyEditText}.
 *
 * Note: The implementation does not support getting raw values yet.
 */
public class MaterialIndianCurrencyEditText extends MaterialEditText {
    public static final String TAG = MaterialIndianCurrencyEditText.class.getSimpleName();

    public MaterialIndianCurrencyEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null && !text.toString().isEmpty()) {
            Locale appLocale = SmartAccounting.getAppLocale();
            Currency currency = Currency.getInstance(appLocale);
            text = CurrencyTextFormatter.formatText(text.toString(), currency, appLocale);
        }
        super.setText(text, type);
    }
}
