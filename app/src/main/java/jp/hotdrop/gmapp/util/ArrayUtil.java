package jp.hotdrop.gmapp.util;

import java.util.List;

import jp.hotdrop.gmapp.model.GoodsCategory;

public class ArrayUtil {

    public static String[] toArrayStr(List<GoodsCategory> categoryList) {

        String[] strList = new String[categoryList.size()];

        int idx = 0;
        for(GoodsCategory category : categoryList) {
            strList[idx] = category.getName();
            idx++;
        }
        return strList;
    }

}
