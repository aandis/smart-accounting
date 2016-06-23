package help.smartbusiness.smartaccounting.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gamerboy on 5/6/16.
 */
public class DateParser {

    private static final String SQLITE_FORMAT = "yyyy-MM-dd";
    private static final String NORMAL_FORMAT = "dd-MM-yyyy";

    public static String padSqliteDate(String sqliteDatStr) {
        SimpleDateFormat format = new SimpleDateFormat(SQLITE_FORMAT);
        String padded;
        try {
            padded = format.format(format.parse(sqliteDatStr));
            return padded;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fromSqliteDate(String sqliteDateStr) {
        SimpleDateFormat format = new SimpleDateFormat(SQLITE_FORMAT);
        Date sqlDate;
        try {
            sqlDate = format.parse(sqliteDateStr);
        } catch (ParseException ex) {
            return "";
        }
        format.applyPattern(NORMAL_FORMAT);
        return format.format(sqlDate);
    }

    public static String toSqliteDate(String normalDateStr) {
        SimpleDateFormat format = new SimpleDateFormat(NORMAL_FORMAT);
        Date normalDate;
        try {
            normalDate = format.parse(normalDateStr);
        } catch (ParseException ex) {
            return "";
        }
        format.applyPattern(SQLITE_FORMAT);
        return format.format(normalDate);
    }

}
