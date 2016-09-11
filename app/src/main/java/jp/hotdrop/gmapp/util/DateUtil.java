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
        long epoch = argDate.getTime();
        if(epoch <= 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(argDate);
    }

    /**
     * 引数の日付が現在日付から何日経過しているか日数を取得します。
     * 日付が未設定（UNIXエポックの初期値）かまたは経過日数が１日未満の場合は空文字を取得します。
     * @param argDate
     * @return
     */
    public static String calcWhetherAfterDays(Date argDate) {
        long epoch = argDate.getTime();
        if(epoch <= 0) {
            return "";
        }
        long res = (System.currentTimeMillis() - epoch)/(1000 * 60 * 60 * 24);
        return (res > 0)? String.valueOf(res) + "日経過" : "";
    }
}
