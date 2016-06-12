package jp.hotdrop.gmapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UNIXエポックと画面表示のための日付形式文字列の変換クラス
 */
public class DateUtil {

    /**
     * Unixエポック（long型）を日付形式に変換して取得します。
     * @param argEpoch
     * @return
     */
    public static Date longToDate(long argEpoch) {
        return new Date(argEpoch);
    }

    /**
     * 日付形式のデータをUnixエポックに変換して取得します。
     * @param argDate
     * @return
     */
    public static long dateToLong(Date argDate) {
        return argDate.getTime();
    }

    /**
     * 日付型データを yyyy/MM/dd 形式の文字列表現で取得します。
     * @param argDate
     * @return
     */
    public static String dateToString(Date argDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(argDate);
    }
}
