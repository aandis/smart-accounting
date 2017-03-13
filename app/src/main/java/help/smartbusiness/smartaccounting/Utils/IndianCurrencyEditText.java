package help.smartbusiness.smartaccounting.Utils;

import android.content.Context;
import android.util.AttributeSet;

import com.blackcat.currencyedittext.CurrencyEditText;

import java.util.Locale;

import help.smartbusiness.smartaccounting.SmartAccounting;

/**
 * Created by abhishekraj on 3/13/17.
 */

public class IndianCurrencyEditText extends CurrencyEditText {
    public IndianCurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLocale(SmartAccounting.getAppLocale());
    }
}
