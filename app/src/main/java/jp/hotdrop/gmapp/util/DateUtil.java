package jp.hotdrop.gmapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UNIXエポックと画面表示のための日付形式文字列の変換クラス
 */
public class DateUtil {

    /**
     * yyyy/MM/dd 形式の文字列日付を、UNIXエポック表現で取得します。
     *
     * @param argTextDate yyyy/MM/dd形式の文字列
     * @return UNIXエポック
     */
    public static Long stringToLong(String argTextDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = sdf.parse(argTextDate);
            return date.getTime();
        } catch(ParseException pe) {
            throw new IllegalStateException("yyyy/MM/ddの形式に変換できない文字列が渡されました。textDate=" + argTextDate, pe);
        }
    }

    /**
     * UNIXエポックを、yyyy/MM/dd 形式の文字列表現で取得します。
     * @param argEpochDate UNIXエポック
     * @return 引数が1以上であればyyyy/MM/ddの文字列表現、0以下であれば空
     */
    public static String longToString(long argEpochDate) {
        if(argEpochDate <= 0L) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(argEpochDate);
        return sdf.format(date);
    }
}
