package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ArrayList<ImageButtonClass> mBoard;
    String currentPlayer = "Red";
    Boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoard = new ArrayList<>();

        for(int i = 0;i <42;i++)
        {
            //in the beginning everything is white.
            int itemId = getResources().getIdentifier("imageView" + i, "id", getPackageName());

            mBoard.add(new ImageButtonClass(this,i,itemId));
        }
    }



    /*This method is called when user clicks on a column of the first row, if a disc was added,
    it will return 1. if the column was full, it will return 0, so we can give the player another try.
    */
    public void makeMove(View view) {
        if (!isBusy) {
            String column = (String) view.getTag(); // from 0-6, according where the user clicks.
            int tagColumn = Integer.parseInt(column); //same as previous line, but int.
            Toast.makeText(this, "Column is " + column, Toast.LENGTH_SHORT).show();
            int placeHolder; // this is needed for the loop, to check every placeHolder from bottom to above.

            //This is the case when the row is filled to the top - no more space on top.
            if (mBoard.get(tagColumn).color.equals("Black") || mBoard.get(tagColumn).color.equals("Red")) {
                Toast.makeText(this, "Column is filled to to top...  column #" + tagColumn, Toast.LENGTH_SHORT).show();
            }

            //check for every row from below, at i*7 + tagColumn, if its Empty or not.
            for (int i = 5; i >= 0; i--) {
                placeHolder = i * 7 + tagColumn;

            /*
             this is the top line special case.
             take the last column for example, it will start from 41.
             41 >> 41-7=34 >> 34 >> 34-7 = 27 >> 27-7 = 20 >> 20 >> 20-7 = 13 >> 13 >> 13-7 = 6 >> 6 >> 6-7(!!!) make it 6 (was placeholder)

            0  1  2  3  4  5  6
            7  8  9  10 11 12 13
            14 15 16 17 18 19 20
            21 22 23 24 25 26 27
            28 29 30 31 32 33 34
            35 36 37 38 39 40 41
             */
                if (placeHolder < 7) {
                    placeHolder = tagColumn;
                }

                if (mBoard.get(placeHolder).color.equals("White")) //found most bottom White placeholder
                {
                    fillBoardAt(tagColumn,placeHolder); //to be added later.
                    Toast.makeText(this, "filled at: " + placeHolder, Toast.LENGTH_SHORT).show();
                    changeTurn();
                    break;
                }
            }
            updateBoard(false);
        }
    }

    private void fillBoardAt(final int top, final int target) {
        isBusy = true; //critical area lock, so no new move can be made.

        /*
        this method works recursively,
         */

        //this is the last case, when top is actually the bottom most
        //we need to save the info on the array.
        //this is also has to be a timer, else it would color without the 300ms wait after previous color.
        if(target <= top) {

            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            switch (currentPlayer) {
                                case "Black":
                                    mBoard.get(top).color = "Black";
                                    mBoard.get(top).imageButton.setImageResource(R.drawable.black);

                                    break;
                                case "Red":
                                    mBoard.get(top).color = "Red";
                                    mBoard.get(top).imageButton.setImageResource(R.drawable.red);

                                    break;
                            }
                            checkBoardState(); //check if the board has 4 in a row
                        }
                    });
                }
            }, 40); //was 300
        }

        else
        {
            //here we just color the top placeholder with currentplayer color for a moment.
            // then return it back to white.
            Timer buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            switch (currentPlayer) {
                                case "Black":
                                    mBoard.get(top).imageButton.setImageResource(R.drawable.black);
                                    break;
                                case "Red":
                                    mBoard.get(top).imageButton.setImageResource(R.drawable.red);
                                    break;
                            }
                        }
                    });
                }
            }, 40); //was 300

            //now we will color it back to white.
            //recursivly call the same method but with the next row.

            Timer buttonTimer2 = new Timer();
            buttonTimer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mBoard.get(top).imageButton.setImageResource(R.drawable.white);
                            fillBoardAt(top+7,target); //recursive call, the +7 is actually next line.
                        }
                    });
                }
            }, 80); //was 600
        }
    }

    //this function will check every end of a turn if the board has 4 in a row
    private void checkBoardState() {
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //horizontal check loop:
                        int row = 0;
                        for (int i=0; i<=3; i++) //check each row for columns 0-3,1-4,2-5,3-6
                        {
                            if (lookRightStartingAt(i + (row*7)))
                                playerWon(mBoard.get(i + (row*7)).color);
                            if (i>=3 && row<5)
                            {
                                i=-1; //-1 because at the end of the loop i is incremented by 1 so we get 0 again
                                row++;
                            }
                        }

                        //vertical check loop:
                        int column = 0;
                        for (int i=0; i<=2; i++) //check each column for rows
                        {
                            if (lookDownStartingAt(i*7 + (column)))
                                playerWon(mBoard.get(i*7 + (column)).color);
                            if (i>=2 && column<6)
                            {
                                i=-1; //-1 because at the end of the loop i is incremented by 1 so we get 0 again
                                column++;
                            }
                        }

                        //diagonal check loop:
                        row = 0;
                        for (int i=0; i<=3; i++) //check each row for columns 0-3,1-4,2-5,3-6
                        {
                            if (lookDiagonallyRightStartingAt(i + (row*7)))
                                playerWon(mBoard.get(i + (row*7)).color);
                            if (i>=3 && row<2)
                            {
                                i=-1; //-1 because at the end of the loop i is incremented by 1 so we get 0 again
                                row++;
                            }
                        }

                        row = 0;
                        for (int i=3; i<=6; i++) //check each row for columns 0-3,1-4,2-5,3-6
                        {
                            if (lookDiagonallyLeftStartingAt(i + (row*7)))
                                playerWon(mBoard.get(i + (row*7)).color);
                            if (i>=6 && row<2)
                            {
                                i=2; //2 because at the end of the loop i is incremented by 1 so we get 3 again
                                row++;
                            }
                        }

                        isBusy = false; //critical area release, so a new move can be made.
                    }
                });
            }
        }, 80); //was 300

    }

    private void playerWon(String color) {
        updateBoard(true);
        Toast.makeText(this, "" + color + " player won!", Toast.LENGTH_SHORT).show();
    }

    private boolean lookRightStartingAt(int start) {
        String startColor = mBoard.get(start).color;
        if (startColor.equals("White")) //no need to check if its white
        {
            return false;
        }
        else //check if we have four same color circles to the right
        {
            if (startColor.equals(mBoard.get(start+1).color) &&
               startColor.equals(mBoard.get(start+2).color) &&
               startColor.equals(mBoard.get(start+3).color))
                  return true;
                  else
                  return false;
        }
    }

    private boolean lookDownStartingAt(int start) {
        String startColor = mBoard.get(start).color;
        if (startColor.equals("White")) //doesn't need to check if its white
        {
            return false;
        }
        else //check if we have four same color circles downside
        {
            if (startColor.equals(mBoard.get(start+7).color) &&
                    startColor.equals(mBoard.get(start+14).color) &&
                    startColor.equals(mBoard.get(start+21).color))
                return true;
            else
                return false;
        }
    }

    private boolean lookDiagonallyRightStartingAt(int start) {
        String startColor = mBoard.get(start).color;
        if (startColor.equals("White")) //doesn't need to check if its white
        {
            return false;
        }
        else //check if we have four same color circles diagonally
        {
            if (startColor.equals(mBoard.get(start+8).color) &&
                    startColor.equals(mBoard.get(start+16).color) &&
                    startColor.equals(mBoard.get(start+24).color))
                return true;
            else
                return false;
        }
    }

    private boolean lookDiagonallyLeftStartingAt(int start) {
        String startColor = mBoard.get(start).color;
        if (startColor.equals("White")) //doesn't need to check if its white
        {
            return false;
        }
        else //check if we have four same color circles diagonally
        {
            if (startColor.equals(mBoard.get(start+6).color) &&
                    startColor.equals(mBoard.get(start+12).color) &&
                    startColor.equals(mBoard.get(start+18).color))
                return true;
            else
                return false;
        }
    }
    private void changeTurn() {
        if (currentPlayer.matches("Black"))
        {
            currentPlayer = "Red";
        }
        else
        {
            currentPlayer = "Black";
        }
    }

    void updateBoard(boolean clear)
    {
        if (clear) { //if clearing the whole board is needed
            for(ImageButtonClass button : mBoard) {
                button.imageButton.setImageResource(R.drawable.white);
                button.color = "White";
            }
        }
        else //update each circle to its matching color
        for(ImageButtonClass button : mBoard)
        {
            if(button.color.matches("White"))
            {
                button.imageButton.setImageResource(R.drawable.white);
            }
            if(button.color.matches("Red"))
            {
                button.imageButton.setImageResource(R.drawable.red);
            }
            if(button.color.matches("Black"))
            {
                button.imageButton.setImageResource(R.drawable.black);
            }
        }

    }
}
