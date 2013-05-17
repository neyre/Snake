package com.nickeyre.snake;

import android.annotation.TargetApi;
import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.Build;

/**
 * Created by neyre on 5/16/13.
 * Backup app settings using Android Backup Service
 */

@TargetApi(Build.VERSION_CODES.FROYO)
public class SnakeBackup extends BackupAgentHelper {
    // The name of the SharedPreferences file
    static final String PREFS = "settings";

    // A key to uniquely identify the set of backup data
    static final String PREFS_BACKUP_KEY = "settings";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, PREFS);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}