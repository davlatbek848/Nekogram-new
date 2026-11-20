package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;
import org.telegram.messenger.ApplicationLoader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PremiumManager {
    private static final String PREFS_NAME = "neko_ai_premium_prefs";
    private static final String KEY_IS_PRO = "is_pro_user";
    private static final String KEY_USAGE_DATE = "daily_usage_date";
    private static final String KEY_USAGE_COUNT = "daily_usage_count";
    private static final int FREE_DAILY_LIMIT = 2;

    private static SharedPreferences getPrefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isProUser() {
        // Google Play BillingClient ulanganda haqiqiy obuna holati shu yerdan tekshiriladi
        return getPrefs().getBoolean(KEY_IS_PRO, false);
    }

    public static void setProUser(boolean isPro) {
        getPrefs().edit().putBoolean(KEY_IS_PRO, isPro).apply();
    }

    public static boolean canUseAiFeature() {
        if (isProUser()) {
            return true;
        }
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        SharedPreferences prefs = getPrefs();
        String savedDate = prefs.getString(KEY_USAGE_DATE, "");

        if (!today.equals(savedDate)) {
            prefs.edit()
                .putString(KEY_USAGE_DATE, today)
                .putInt(KEY_USAGE_COUNT, 0)
                .apply();
            return true;
        }
        return prefs.getInt(KEY_USAGE_COUNT, 0) < FREE_DAILY_LIMIT;
    }

    public static void recordAiUsage() {
        if (isProUser()) return;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        SharedPreferences prefs = getPrefs();
        String savedDate = prefs.getString(KEY_USAGE_DATE, "");
        int current = today.equals(savedDate) ? prefs.getInt(KEY_USAGE_COUNT, 0) : 0;

        prefs.edit()
            .putString(KEY_USAGE_DATE, today)
            .putInt(KEY_USAGE_COUNT, current + 1)
            .apply();
    }

    public static int getRemainingFreeUses() {
        if (isProUser()) return 999;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        SharedPreferences prefs = getPrefs();
        if (!today.equals(prefs.getString(KEY_USAGE_DATE, ""))) {
            return FREE_DAILY_LIMIT;
        }
        return Math.max(0, FREE_DAILY_LIMIT - prefs.getInt(KEY_USAGE_COUNT, 0));
    }
}
