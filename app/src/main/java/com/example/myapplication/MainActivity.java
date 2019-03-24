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




    public int makeMove(View view) {
        String column = (String) view.getTag();
        int tagColumn = Integer.parseInt(column);
        Toast.makeText(this, "Column is " + column, Toast.LENGTH_SHORT).show();
        int placeHolder;

        //This is the case when the row is filled to the top.
        if(mBoard.get(tagColumn).color.equals("Black") || mBoard.get(tagColumn).color.equals("Red"))
        {
            Toast.makeText(this, "Not Empty at top of line "+ tagColumn, Toast.LENGTH_SHORT).show();
            return 0;
        }

        for(int i=5; i >= 0; i--)
        {
            placeHolder = i*7+tagColumn;
            if(placeHolder < 7)
            {
                placeHolder = tagColumn;
            }
            if(mBoard.get(placeHolder).color.equals("White"))
            {
                //fillBoardAt(placeHolder,currentPlayer);
                Toast.makeText(this, "filled at: "+ placeHolder, Toast.LENGTH_SHORT).show();
                mBoard.get(placeHolder).color = "Red";
                break;
            }
        }
        return 1;  // move was successful
    }
}
