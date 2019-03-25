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

            //check for every row from below, at i*7 + tagColumn, if it Empty or not.
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
            updateBoard();
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
                            isBusy = false; //critical area release, so a new move can be made.
                        }
                    });
                }
            }, 300);
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
            }, 300);

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
            }, 600);
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

    void updateBoard()
    {
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
