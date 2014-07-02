package edu.cs307.railbarons;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class ActivityBoard extends Activity {
	
	private ImageButton[] sidebarButtons;
	private RelativeLayout menuLayout;
	private RelativeLayout transLayout;
	private AlphaAnimation transIn, transOut, menuIn, menuOut;
	private MediaPlayer mediaPlayer;
	
	public InfoCenter infoCenter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_board);
        launchMenu();
        infoCenter = new InfoCenter();
        setupMediaPlayer();
        initSidebar();
        this.menuLayout = (RelativeLayout) findViewById(R.id.menu_layout);
        this.menuLayout.setVisibility(View.GONE);
        this.transLayout = (RelativeLayout) findViewById(R.id.trans_layout);
        this.transLayout.setVisibility(View.GONE);
        transIn = new AlphaAnimation(0.0F, 0.5F);
        transIn.setDuration(200); // Make animation instant
        transIn.setFillAfter(true); // Tell it to persist after the animation ends
        transOut = new AlphaAnimation(0.5F, 0.0F);
        transOut.setDuration(200); // Make animation instant
        transOut.setFillAfter(false); // Tell it to persist after the animation ends
        menuIn = new AlphaAnimation(0.0F, 1.0F);
        menuIn.setDuration(200); // Make animation instant
        menuIn.setFillAfter(true); // Tell it to persist after the animation ends
        menuOut = new AlphaAnimation(1.0F, 0.0F);
        menuOut.setDuration(200); // Make animation instant
        menuOut.setFillAfter(false); // Tell it to persist after the animation ends
        this.transLayout.startAnimation(transOut);
    }
    
    public void launchMenu()
    {
    	Intent myIntent = new Intent(ActivityBoard.this, Activity_Menu.class);
    	ActivityBoard.this.startActivity(myIntent);
    }
    
    public void initSidebar()
    {
    	//Setting up the buttons from the xml document
    	this.sidebarButtons = new ImageButton[10];
    	this.sidebarButtons[0] = (ImageButton) findViewById(R.id.trainmanager_button);
        this.sidebarButtons[1] = (ImageButton) findViewById(R.id.tracks_button);
        this.sidebarButtons[2] = (ImageButton) findViewById(R.id.market_button);
        this.sidebarButtons[3] = (ImageButton) findViewById(R.id.menu_button);
        this.sidebarButtons[4] = (ImageButton) findViewById(R.id.trainmarket_button);
        this.sidebarButtons[5] = (ImageButton) findViewById(R.id.carmarket_button);
        this.sidebarButtons[6] = (ImageButton) findViewById(R.id.back_button);
        this.sidebarButtons[7] = (ImageButton) findViewById(R.id.confirm_button);
        this.sidebarButtons[8] = (ImageButton) findViewById(R.id.undo_button);
        this.sidebarButtons[9] = (ImageButton) findViewById(R.id.cancel_button);
        
        //Make only the base sidebar buttons visible
    	for(int i = 0; i < 4; i++)
    	{
    		sidebarButtons[i].setVisibility(View.VISIBLE);
    	}
    	for(int i = 4; i < 7; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    	for(int i = 7; i < 10; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    }
    
    public void baseSidebar(View v)
    {
    	//Hides the market buttons and makes the base buttons visible
    	for(int i = 0; i < 4; i++)
    	{
    		sidebarButtons[i].setVisibility(View.VISIBLE);
    	}
    	for(int i = 4; i < 7; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    	for(int i = 7; i < 10; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    }
    
    public void baseSidebar()
    {
    	//Hides the market buttons and makes the base buttons visible
    	for(int i = 0; i < 4; i++)
    	{
    		sidebarButtons[i].setVisibility(View.VISIBLE);
    	}
    	for(int i = 4; i < 7; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    	for(int i = 7; i < 10; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    }
    
    public void marketSidebar(View v)
    {
    	//Hides the base buttons and makes the market buttons visible
    	for(int i = 0; i < 4; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    	for(int i = 4; i < 7; i++)
    	{
    		sidebarButtons[i].setVisibility(View.VISIBLE);
    	}
    	for(int i = 7; i < 10; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    }
    
    public void selectDestinationView()
    {
    	for(int i = 0; i < 4; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    	for(int i = 4; i < 7; i++)
    	{
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    	for(int i = 7; i < 10; i++)
    	{
    		sidebarButtons[i].setVisibility(View.VISIBLE);
    	}
    }
    
    public void trainManager(View v)
    {
    	Intent myIntent = new Intent(ActivityBoard.this, Activity_TrainManager.class);
    	ActivityBoard.this.startActivity(myIntent); 
    }
    
    public void tracks(View v) {
    	Renderer.setEventStatePurchaseRail();
    	for(int i=0; i<10; i++) {
    		sidebarButtons[i].setVisibility(View.GONE);
    	}
    	sidebarButtons[7].setVisibility(View.VISIBLE);  // Confirm
    	sidebarButtons[9].setVisibility(View.VISIBLE);  // Cancel
    }
    
    public void menu(View v)
    {
        this.transLayout.startAnimation(transIn);
        this.menuLayout.startAnimation(menuIn);
        this.menuLayout.setVisibility(View.VISIBLE);
        this.transLayout.setVisibility(View.VISIBLE);
    }
    
    public void backButton(View v)
    {
        this.transLayout.startAnimation(transOut);
        this.menuLayout.startAnimation(menuOut);
        this.menuLayout.setVisibility(View.GONE);
        this.transLayout.setVisibility(View.GONE);
    }
    
    public void trainMarket(View v)
    {
    	Intent myIntent = new Intent(ActivityBoard.this, Activity_TrainMarket.class);
    	ActivityBoard.this.startActivity(myIntent);
    }
    
    public void carMarket(View v)
    {
    	Intent myIntent = new Intent(ActivityBoard.this, Activity_CarMarket.class);
    	ActivityBoard.this.startActivity(myIntent);
    }
    
    public void settings(View v)
    {
    	Intent myIntent = new Intent(ActivityBoard.this, Activity_Settings.class);
    	ActivityBoard.this.startActivity(myIntent);
    }
    
    public void newGame(View v)
    {
		//this function is responsible for a generating and displaying a pop-up dialog box
    	//that displays a message making sure that the user wants to make a new game
		 AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		 helpBuilder.setTitle("Are you sure...");
		 helpBuilder.setMessage("Do you really want to make a new game (your current game will be overwritten)?");
		 helpBuilder.setPositiveButton("Yes",
				   new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int which) {    	
				    	Database.clearDatabase();
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
    
    public void statistics(View v)
    {
    	Intent myIntent = new Intent(ActivityBoard.this, Activity_Statistics.class);
    	ActivityBoard.this.startActivity(myIntent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_board, menu);
        return true;
    }
    
    public void setupMediaPlayer()
    {
    	mediaPlayer = MediaPlayer.create(ActivityBoard.this, R.raw.background_music);
    	mediaPlayer.setLooping(true);
    	if(InfoCenter.getAudioStatus())
    	{
        	mediaPlayer.start();
    	}
    }
    
    public void confirmPath(View v){
    	switch(Renderer.getEventState()) {
    	case EVENTSTATESELECTDESTINATION:
        	Renderer.submitEventStateSelectDestination();
        	baseSidebar(v);
        	break;
    	case EVENTSTATEBUILDRAIL:
    		if(Renderer.submitEventStateBuildRail()) {
    			baseSidebar(v);
    		}
    		break;
		default:
			break;
    	}
    	
    }
    
    public void undoSelection(View v){
    	Renderer.undoEventStateSelectDestination();
    }
    
    public void cancelSelection(View v){
    	switch(Renderer.getEventState()) {
    	case EVENTSTATESELECTDESTINATION:
    		Renderer.cancelEventStateSelectDestination();
    		break;
    	case EVENTSTATEBUILDRAIL:
    		Renderer.cancelEventStateBuildRail();
    		break;
    	default:
    		break;
    	}
    	baseSidebar(v);
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
    	
    	switch(Renderer.getEventState()) {
    	case EVENTSTATEVIEWCITY:
    		baseSidebar();
    		break;
    	case EVENTSTATEBUILDRAIL:
    		
    	case EVENTSTATESELECTDESTINATION:
    		selectDestinationView();
    		break;
    	}
    }
}