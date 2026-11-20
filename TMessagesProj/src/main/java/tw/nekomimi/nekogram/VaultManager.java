package tw.nekomimi.nekogram;

import android.content.Context;
import android.content.SharedPreferences;
import org.telegram.messenger.ApplicationLoader;
import java.util.HashSet;
import java.util.Set;

public class VaultManager {
    private static final String PREFS_NAME = "neko_secret_vault";
    private static final String KEY_HIDDEN_DIALOGS = "hidden_dialog_ids";
    private static final String KEY_VAULT_PIN = "vault_pin_code";
    private static boolean isVaultUnlocked = false;

    private static SharedPreferences getPrefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean hasPin() {
        return !getPrefs().getString(KEY_VAULT_PIN, "").isEmpty();
    }

    public static void setPin(String pin) {
        getPrefs().edit().putString(KEY_VAULT_PIN, pin).apply();
    }

    public static boolean checkPin(String inputPin) {
        String saved = getPrefs().getString(KEY_VAULT_PIN, "");
        if (!saved.isEmpty() && saved.equals(inputPin)) {
            isVaultUnlocked = true;
            return true;
        }
        return false;
    }

    public static void unlockVault() {
        isVaultUnlocked = true;
    }

    public static void lockVault() {
        isVaultUnlocked = false;
    }

    public static boolean isUnlocked() {
        return isVaultUnlocked;
    }

    public static void hideChat(long dialogId) {
        Set<String> hidden = new HashSet<>(getPrefs().getStringSet(KEY_HIDDEN_DIALOGS, new HashSet<>()));
        hidden.add(String.valueOf(dialogId));
        getPrefs().edit().putStringSet(KEY_HIDDEN_DIALOGS, hidden).apply();
    }

    public static void unhideChat(long dialogId) {
        Set<String> hidden = new HashSet<>(getPrefs().getStringSet(KEY_HIDDEN_DIALOGS, new HashSet<>()));
        hidden.remove(String.valueOf(dialogId));
        getPrefs().edit().putStringSet(KEY_HIDDEN_DIALOGS, hidden).apply();
    }

    public static boolean isChatHidden(long dialogId) {
        Set<String> hidden = getPrefs().getStringSet(KEY_HIDDEN_DIALOGS, new HashSet<>());
        return hidden.contains(String.valueOf(dialogId));
    }
}
