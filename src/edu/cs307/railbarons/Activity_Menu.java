package edu.cs307.railbarons;

import java.util.ArrayList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;

public class Activity_Menu extends Activity {
	private MediaPlayer mediaPlayer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.splash_menu);
        setupMediaPlayer();
    }
    
    public void backButton(View v)
    {
    	finish();
    }
    
    public void newGame(View v){
    	
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//that displays a message making sure that the user wants to make a new game
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("Are you sure...");
		 helpBuilder.setMessage("Do you really want to make a new game (your current game will be overwritten)?");
		 helpBuilder.setPositiveButton("Yes",
				   new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {    	
				    	Database.clearDatabase();
				    	finish();
				    }
				   });
				 
				 helpBuilder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
		 		    public void onClick(DialogInterface dialog, int which) {
		 		    	//no functionality should be placed here
		 		    	//the system should ignore a "No" request,     		    
		 		    }
		 		   });

		 AlertDialog helpDialog = helpBuilder.create();
		 helpDialog.show();
    }
    
    public void setupMediaPlayer()
    {
    	mediaPlayer = MediaPlayer.create(Activity_Menu.this, R.raw.menu_music);
    	mediaPlayer.setLooping(true);
    	if(InfoCenter.getAudioStatus())
    	{
        	mediaPlayer.start();
    	}
    }
    
    public void settings(View v)
    {
    	Intent myIntent = new Intent(Activity_Menu.this, Activity_Settings.class);
    	Activity_Menu.this.startActivity(myIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer != null && InfoCenter.getAudioStatus())
        	mediaPlayer.pause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(mediaPlayer != null && InfoCenter.getAudioStatus())
    	{
    		mediaPlayer.start();
    	}
    }
}
