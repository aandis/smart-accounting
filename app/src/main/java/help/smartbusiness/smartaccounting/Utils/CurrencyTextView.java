package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.blackcat.currencyedittext.CurrencyTextFormatter;

import java.util.Currency;
import java.util.Locale;

import help.smartbusiness.smartaccounting.SmartAccounting;

/**
 * Created by gamerboy on 3/12/17.
 * Custom {@link AppCompatTextView} to display currency stored as long.
 */

public class CurrencyTextView extends AppCompatTextView {

    public static final String TAG = CurrencyTextView.class.getSimpleName();

    public CurrencyTextView(Context context, AttributeSet attributeSet) {
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
