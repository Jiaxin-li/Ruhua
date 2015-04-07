package com.Jiaxin.ruhua;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnSeekBarChangeListener{
	private SeekBar radiusBar;
	private SeekBar intensityBar;
	private ImageButton addButton;
	private ImageButton saveButton;
	private ImageView imageView;
	private Bitmap bitmap = null;
	private int displayWidth;
	private int displayHeight;
	private TextView textProgress;
	private final int radiusBarID = R.id.RadiusBar; 
	private final int intensityBarID = R.id.IntensityBar;
	private int radius =0;
	private int intensity =0;
	private final int PICK_IMAGE_REQUEST = 1;
	private final String TAG ="DEBUG";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		radiusBar = (SeekBar)findViewById(radiusBarID);
		intensityBar = (SeekBar)findViewById(intensityBarID);
		imageView = (ImageView) findViewById(R.id.imageView);
		displayWidth = imageView.getLayoutParams().width;
		displayHeight = imageView.getLayoutParams().height;
		textProgress = (TextView)findViewById(R.id.textView1);
		addButton =  (ImageButton)findViewById(R.id.addButton);
		saveButton =  (ImageButton)findViewById(R.id.saveButton);
		addButton.setOnClickListener(new OnClickListener() {			 
			@Override
			public void onClick(View arg0) {
					Toast.makeText(getApplicationContext(),"addButton is clicked!", Toast.LENGTH_SHORT).show(); 
					Intent intent = new Intent();
					// Show only images, no videos or anything else
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					// Always show the chooser (if there are multiple options available)
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
				}			 
			});
		saveButton.setOnClickListener(new OnClickListener() {			 
			@Override
			public void onClick(View arg0) {

			   Toast.makeText(getApplicationContext(),"saveButton is clicked!", Toast.LENGTH_SHORT).show();

				}			 
			});
		
			radiusBar.setOnSeekBarChangeListener(this); // set seekbar listener.
			intensityBar.setOnSeekBarChangeListener(this); // set seekbar listener.
		}
		
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		int barId =seekBar.getId();
		if(barId==radiusBarID){
			radius = progress;
		}
		else if (barId==intensityBarID){
			intensity = progress;
		}
		textProgress.setText("Radius: "+ radius+" Intensity: "+intensity);		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
	 
	        Uri uri = data.getData();
	 
	        try {
	            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
	            Log.d(TAG, String.valueOf(bitmap));
	            imageView.getLayoutParams().width = displayWidth;
	            imageView.getLayoutParams().height = displayHeight;
	            imageView.setImageBitmap(bitmap);
	        } catch (IOException e) {
	        	
	            e.printStackTrace();
	        }
	        
	    }
	   
	    
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}
