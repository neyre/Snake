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

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import android.widget.TextView;

public class Game extends View {

  private boolean setupComplete = false;
  private int pxSquare, squaresWidth, squaresHeight,sqBorder=0,paddingTop=0,paddingLeft=0;
  private ArrayList<Block> walls;
  public Snake snake;
  private Food food;
  private Random random;
  private TextView scoreView;
  private GameScreen mActivity;
  public boolean gameOver=false;
  private int score,frameRate;
  private boolean darkTheme,classicMode,snakeOriented;

  public Game(Context context,GameScreen activity,TextView scoreView,boolean darkTheme,boolean classicMode,boolean snakeOriented,int speed) {
    super(context);
    mActivity = activity;
    random = new Random();
    this.scoreView = scoreView;
    this.darkTheme = darkTheme;
    this.classicMode = classicMode;
    this.snakeOriented = snakeOriented;
    this.frameRate = 5*(speed+1);
  }

  // If User Scores
  private void score(){
    score++;
    scoreView.setText(Integer.toString(this.score));
  }

  // Draw View
  @SuppressLint("DrawAllocation")
  protected void onDraw(Canvas canvas){
    if(!setupComplete) {
      setup();
      this.invalidate();
      return;
    }

    //Draw Walls
    for(Block block:walls){
      block.draw(canvas);
    }

    //Move & Draw Snake
    snake.draw(canvas);

    //Draw Food
    food.draw(canvas);

    //Invalidate View After Timer, Using New Thread to prevent Blocking UI Thread
    //If Snake is Stopped, Wait and then call game over
    final View parent = this;
    if(!snake.stopped){
      new Thread(new Runnable() {
        public void run() {
          parent.postDelayed(new Runnable() {
            public void run() {
              parent.invalidate();
            }
          },1000/frameRate);
        }
      }).start();
    }else if(gameOver){
      new Thread(new Runnable() {
        public void run() {
          parent.postDelayed(new Runnable() {
            public void run() {
              mActivity.gameOver();
            }
          },500);
        }
      }).start();
    }
  }

  // Setup View
  public void setup(){
    //Reset Score
    score = -1;
    this.score();
    gameOver=false;

    //Calculate Width of View in Inches
    int pxWidth = getWidth();
    int pxHeight = getHeight();
    int dpi = getResources().getDisplayMetrics().densityDpi;
    float inWidth = ((float) pxWidth) / dpi;
    float inHeight = ((float) pxHeight) / dpi;

    //Calculate Number of Squares Based on View Size (Minimum 15 x 15)
    squaresWidth  = (int) (inWidth * 10.0);
    squaresHeight = (int) (inHeight * 10.0);
    if(squaresHeight < 15) squaresHeight = 15;
    if(squaresWidth < 15)  squaresWidth = 15;

    //Calculate Size of Squares
    int pxSquareWidth = pxWidth / squaresWidth;
    int pxSquareHeight = pxHeight / squaresHeight;
    if(pxSquareWidth > pxSquareHeight)
      pxSquare = pxSquareHeight; //Extra Space on Sides
    else
      pxSquare = pxSquareWidth;  //Extra Space on Top

    //Calculate Padding Around & Between Squares
    paddingLeft = (pxWidth - squaresWidth * pxSquare)/2;
    paddingTop = paddingLeft;
    if(classicMode) sqBorder = pxSquare / 20;

    //Build List of Wall Objects
    walls = new ArrayList<Block>();
    for(int j=0;j<squaresWidth;j++){
      walls.add(new Block(j,0,0));  //Top Walls
      walls.add(new Block(j,squaresHeight-1,0));  //Bottom Walls
    }for(int j=1;j<(squaresHeight-1);j++){ //Left Walls
      walls.add(new Block(0,j,0));  //Left Walls
      walls.add(new Block(squaresWidth-1,j,0)); //Right Walls
    }

    //Create Snake
    snake = new Snake();

    //Create Food
    food = new Food(snake,walls);

    setupComplete = true;
  }

  // Snake Object contains a list of blocks, knows if it is moving and
  // which direction it is moving
  public class Snake{

    public ArrayList<Block> blocks;
    private int direction,length;
    public boolean stopped=false;

    // Create Snake with 3 Blocks
    public Snake(){

      // Create Leading Block
      blocks = new ArrayList<Block>();
      blocks.add(new Block(squaresWidth/2,squaresHeight/2,1));
      length=3;

      // Calculate Random Initial Direction and Add 2 Remaining Blocks
      direction = random.nextInt(4);
      switch(direction){
        case 0: //Going Right
          blocks.add(new Block(squaresWidth/2-1,squaresHeight/2,1));
          blocks.add(new Block(squaresWidth/2-2,squaresHeight/2,1));
          break;
        case 1: //Going Down
          blocks.add(new Block(squaresWidth/2,squaresHeight/2-1,1));
          blocks.add(new Block(squaresWidth/2,squaresHeight/2-2,1));
          break;
        case 2: //Going Left
          blocks.add(new Block(squaresWidth/2+1,squaresHeight/2,1));
          blocks.add(new Block(squaresWidth/2+2,squaresHeight/2,1));
          break;
        case 3: //Going Up
          blocks.add(new Block(squaresWidth/2,squaresHeight/2+1,1));
          blocks.add(new Block(squaresWidth/2,squaresHeight/2+2,1));
      }
    }

    // Move & Draw Snake
    public void draw(Canvas canvas){
      if(!stopped) move();
      for(Block block:blocks) block.draw(canvas);
    }

    // Turn One Direction Left from Current Orientation (Snake Oriented)
    // If Not Going Left or Right, Go Left (Four Direction)
    public void turnLeft(){
      if(snakeOriented){
        this.direction -= 1;
        if(this.direction < 0) this.direction = 3;
      }else if(this.direction != 0 && this.direction != 2)
        this.direction = 2;
    }

    // Turn One Direction Right from Current Orientation (Snake Oriented)
    // If Not Going Left or Right, Go Right (Four Direction)
    public void turnRight(){
      if(snakeOriented){
        this.direction += 1;
        if(this.direction > 3) this.direction = 0;
      }else if(this.direction != 0 && this.direction != 2)
        this.direction = 0;
    }

    // If Not Going Down or Up, Go Down (Four Direction Only)
    public void turnDown(){
      if(!snakeOriented && this.direction != 1 && this.direction != 3)
        this.direction = 1;
    }

    // If Not Going Down or Up, Go Up (Four Direction Only)
    public void turnUp(){
      if(!snakeOriented && this.direction != 1 && this.direction != 3)
        this.direction = 3;
    }

    // Move Snake 1 Space in Current Direction
    public void move(){

      // Grab Current Front Block
      Block frontBlock = blocks.get(0);

      // Create New Block at Front of Snake
      Block newBlock;
      switch(direction){
        case 0: //Going Right
          newBlock = new Block(frontBlock.x+1,frontBlock.y,1);
          break;
        case 1: //Going Down
          newBlock = new Block(frontBlock.x,frontBlock.y+1,1);
          break;
        case 2: //Going Left
          newBlock = new Block(frontBlock.x-1,frontBlock.y,1);
          break;
        default:  //Going Up
          newBlock = new Block(frontBlock.x,frontBlock.y-1,1);
      }

      // If New Front Block Collides with Walls
      if(this.collides(newBlock) || newBlock.collides(walls)){
        stopped = true;
        for(Block block:blocks){
          block.setType(3);
        }
        newBlock.setType(0);
        gameOver=true;

      // If New Block is Clear
      }else{

        // Add New Block to the Front
        blocks.add(0,newBlock);

        // If Collision with Food
        if(this.collides(food)){
          food.move(this,walls);
          length++;
          score();

        // If No Collision with Food, Remove Last Block
        }else
          blocks.remove(length);
      }
    }

    // Check for Collisions with a Block
    public boolean collides(Block block){
      for(Block oneBlock:this.blocks)
        if(block.collides(oneBlock)) return true;
      return false;
    }

  }

  public class Block {
    public int x=0,y=0;
    ShapeDrawable shape;

    public Block(){}

    public Block(int x,int y,int type){
      this.x = x;
      this.y = y;

      shape = new ShapeDrawable(new RectShape());
      shape.setBounds(paddingLeft+x*pxSquare+sqBorder,paddingTop+y*pxSquare+sqBorder,paddingLeft+(x+1)*pxSquare-sqBorder,paddingTop+(y+1)*pxSquare-sqBorder);

      this.setType(type);
    }

    public void draw(Canvas canvas){
      shape.draw(canvas);
    }

    public boolean collides(Block block){
      return block.x == this.x && block.y == this.y;
    }

    public boolean collides(ArrayList<Block> blocks){
      for(Block block:blocks){
        if(this.collides(block)) return true;
      }
      return false;
    }

    public void setType(int type){
      switch(type){
        case 0: //If Wall, Paint Black
          if(darkTheme) shape.getPaint().setColor(Color.parseColor("#fff3f3f3"));
          else shape.getPaint().setColor(Color.BLACK);
          break;
        case 1: //If Snake, Paint Blue
          shape.getPaint().setColor(Color.parseColor("#ff33b5e5"));
          break;
        case 2: //If Food, Paint Grey
          shape.getPaint().setColor(Color.GRAY);
          break;
        case 3: //If Collision, Paint Red
          shape.getPaint().setColor(Color.RED);
      }
    }

  }

  class Food extends Block {

    public Food(Snake snake, ArrayList<Block> blocks){
      shape = new ShapeDrawable(new RectShape());
      this.setType(2);
      this.move(snake,blocks);
    }

    public void move(Snake snake, ArrayList<Block> blocks){
      while(true){
        this.x = random.nextInt(squaresWidth-3)+1;
        this.y = random.nextInt(squaresHeight-3)+1;
        if(!snake.collides(this) && !this.collides(blocks)) break;
      }
      shape.setBounds(paddingLeft+x*pxSquare+sqBorder,paddingTop+y*pxSquare+sqBorder,paddingLeft+(x+1)*pxSquare-sqBorder,paddingTop+(y+1)*pxSquare-sqBorder);
    }

  }
}
