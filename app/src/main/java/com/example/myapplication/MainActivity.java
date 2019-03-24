package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    ArrayList<Disc> mBoard;
    String currentPlayer = "Red";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoard = new ArrayList<>();

        for(int i = 0;i <42;i++)
        {
            //in the beginning everything is white.
            mBoard.add(new Disc("White"));
        }
    }



    /*This method is called when user clicks on a column of the first row, if a disc was added,
    it will return 1. if the column was full, it will return 0, so we can give the player another try.
    */
    public int makeMove(View view) {
        String column = (String) view.getTag(); // from 0-6, according where the user clicks.
        int tagColumn = Integer.parseInt(column); //same as previous line, but int.
        Toast.makeText(this, "Column is " + column, Toast.LENGTH_SHORT).show();
        int placeHolder; // this is needed for the loop, to check every placeHolder from bottom to above.

        //This is the case when the row is filled to the top - no more space on top.
        if(mBoard.get(tagColumn).color.equals("Black") || mBoard.get(tagColumn).color.equals("Red"))
        {
            Toast.makeText(this, "Column is filled to to top...  column #"+ tagColumn, Toast.LENGTH_SHORT).show();
            return 0;
        }

        //check for every row from below, at i*7 + tagColumn, if it Empty or not.
        for(int i=5; i >= 0; i--)
        {
            placeHolder = i*7 + tagColumn;

            /*
             this is the top line special case.
             take the last column for example, it will start from 41.
             41 >> 41-7=34 >> 34 >> 34-7 = 27 >> 27-7 = 20 >> 20 >> 20-7 = 13 >> 13 >> 13-7 = 6 >> 6 >> 6-7(!!!) make it 6 (was placeholder)
             */
            if(placeHolder < 7)
            {
                placeHolder = tagColumn;
            }

            if(mBoard.get(placeHolder).color.equals("White")) //found most bottom White placeholder
            {
                //fillBoardAt(placeHolder,currentPlayer); //to be added later.
                Toast.makeText(this, "filled at: "+ placeHolder, Toast.LENGTH_SHORT).show();
                mBoard.get(placeHolder).color = "Red"; //this is just a test, remove later.
                break;
            }
        }
        return 1;  // move was successful
    }
}
