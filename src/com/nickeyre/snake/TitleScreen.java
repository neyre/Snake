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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class TitleScreen extends Activity {

  SharedPreferences settings;

  // Create Title Screen View
  @Override
  public void onCreate(Bundle savedInstanceState) {

    // Set Theme According to Settings
    settings = getSharedPreferences("settings", 0);
    if(settings.getInt("theme",0) == 1) setTheme(android.R.style.Theme_Holo);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.title_screen);
  }

  // On "Start" View Button Press, Start Game
  public void startGame(View view){
    Intent intent = new Intent(this, GameScreen.class);
    startActivity(intent);
  }

  // On "Options" View Button Press, Change to Options Screen
  public void options(View view){
    Intent intent = new Intent(this, OptionsScreen.class);
    startActivity(intent);
    this.finish();
  }

}
