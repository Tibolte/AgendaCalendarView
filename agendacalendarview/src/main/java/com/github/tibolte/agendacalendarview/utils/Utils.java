package com.github.tibolte.agendacalendarview.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class Utils {

    /**
     * Ask the device for any Gmail account, in order to retrieve some events from it.
     *
     * @param context The Context of the application.
     * @return The first Gmail account found on the device.
     */
    public static Account getAccount(Context context) {
        if (context == null) {
            return null;
        }
        // Get all registered account, by gmail accounts
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        if (accounts.length == 0) {
            return null;
        }
        return accounts[0];
    }

}
