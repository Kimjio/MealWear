package com.kimjio.mealwear;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by kjo0927 on 17. 2. 4.
 */

public class ListData {

    public String mSchoolName;

    public String mZipAdres;

    public String mOrgCode;

    public String mSchulKndScCode;

    public String mSchulCrseScCode;

    public static final Comparator<ListData> ALPHA_COMPARATOR = new Comparator<ListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ListData mListDate_1, ListData mListDate_2) {
            return sCollator.compare(mListDate_1.mSchoolName, mListDate_2.mSchoolName);
        }
    };
}
