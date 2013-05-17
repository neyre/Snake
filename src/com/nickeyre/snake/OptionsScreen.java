/*
Snake - an Android Game
Copyright 2012 Nick Eyre <nick@nickeyre.com>

Snake is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Snake is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Snake.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.nickeyre.snake;

import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class OptionsScreen extends Activity {

  SharedPreferences settings;
  SharedPreferences.Editor editor;
  Spinner themeSpinner,controlsSpinner,viewSpinner,speedSpinner;

  @Override
  public void onCreate(Bundle savedInstanceState) {

    settings  = getSharedPreferences("settings", 0);
    editor    = settings.edit();
    int theme = settings.getInt("theme",0);
    int controls = settings.getInt("controls",0);
    int view  = settings.getInt("view",0);
    int speed = settings.getInt("speed",0);

    // Set Dark Theme
    if(theme == 1) setTheme(android.R.style.Theme_Holo);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.options);

    // Grab Settings Spinners
    themeSpinner = (Spinner) findViewById(R.id.spinnerTheme);
    controlsSpinner = (Spinner) findViewById(R.id.spinnerControls);
    viewSpinner  = (Spinner) findViewById(R.id.spinnerView);
    speedSpinner = (Spinner) findViewById(R.id.spinnerSpeed);

    // Set Spinner Current Values
    themeSpinner.setSelection(theme);
    controlsSpinner.setSelection(controls);
    viewSpinner.setSelection(view);
    speedSpinner.setSelection(speed);
  }

  // Back Button in View
  public void back(View view){
    onBackPressed();
  }

  // Go Back to Title Screen
  @Override
  public void onBackPressed(){

    // Get New Values
    int theme = themeSpinner.getSelectedItemPosition();
    int controls = controlsSpinner.getSelectedItemPosition();
    int view = viewSpinner.getSelectedItemPosition();
    int speed = speedSpinner.getSelectedItemPosition();

    // Save in Settings
    editor.putInt("theme", theme);
    editor.putInt("controls", controls);
    editor.putInt("view", view);
    editor.putInt("speed", speed);
    editor.commit();

    // Call for Backup
    BackupManager backupManager = new BackupManager(this);
    backupManager.dataChanged();

    // Go Home & Close Options Screen
    Intent intent = new Intent(this, TitleScreen.class);
    startActivity(intent);
    this.finish();
  }
}
