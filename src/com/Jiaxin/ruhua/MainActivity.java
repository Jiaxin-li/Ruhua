package com.Jiaxin.ruhua;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
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
	private ImageButton cameraButton;
	private ImageView imageView;
	private Bitmap origionBitmap = null;
	private Bitmap modifiedBitmap = null;

	private TextView textProgress;
	private final int radiusBarID = R.id.RadiusBar; 
	private final int intensityBarID = R.id.IntensityBar;
	private int radius =1;
	private int intensity =1;
	private final int PICK_IMAGE_REQUEST = 1;
	private final static String TAG ="RUH_main";
	private BitmapFactory.Options btoptions ,options;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 Log.d(TAG, "on create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		radiusBar = (SeekBar)findViewById(radiusBarID);
		intensityBar = (SeekBar)findViewById(intensityBarID);
		imageView = (ImageView) findViewById(R.id.imageView);
		

		textProgress = (TextView)findViewById(R.id.textView1);
		addButton =  (ImageButton)findViewById(R.id.addButton);
		saveButton =  (ImageButton)findViewById(R.id.saveButton);
		cameraButton =  (ImageButton)findViewById(R.id.cameraButton);
		addButton.setOnClickListener(new OnClickListener() {			 
			@Override
			public void onClick(View arg0) {
					Toast.makeText(getApplicationContext(),"addButton is clicked!", Toast.LENGTH_SHORT).show(); 
					 Log.d(TAG, "addButton is clicked!");
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
			   Log.d(TAG, "saveButton is clicked!");
				}			 
			});
		cameraButton.setOnClickListener(new OnClickListener() {			 
			@Override
			public void onClick(View arg0) {

			   Toast.makeText(getApplicationContext(),"cameraButton is clicked!", Toast.LENGTH_SHORT).show();
			   Log.d(TAG, "cameraButton is clicked!");
			   modifiedBitmap = oilPaint(origionBitmap, radius, intensity);
			   imageView.setImageBitmap(modifiedBitmap);
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
			radius = progress+1;
		}
		else if (barId==intensityBarID){
			intensity = progress+1;
		}
		textProgress.setText("Radius: "+ radius+" Intensity: "+intensity);	
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
	    	Log.d(TAG, "if block");
	        Uri uri = data.getData();
	 
	        try {
	        	
	          
	        	
	        	InputStream is = getContentResolver().openInputStream(uri);
	        	Log.d(TAG, uri.toString());
	        	btoptions = new BitmapFactory.Options();
	        	// to be debuged auto fit screen , now will triger null in factory
	        	/*
	        	btoptions.inJustDecodeBounds = true;
	        	BitmapFactory.decodeStream(is, null, btoptions);
	    		displayWidth = imageView.getWidth(); 
	    		displayHeight = imageView.getHeight();
	        	int temp = calculateInSampleSize(btoptions, displayWidth, displayHeight);
	        	*/
	        	int temp=8;
	        	Log.d(TAG, "temp:" + temp);
	        	options = new BitmapFactory.Options();
	        	options.inSampleSize = temp;
	        	options.inJustDecodeBounds = false;
	        	origionBitmap = BitmapFactory.decodeStream(is, null, options);
	            is.close();
	            
	            imageView.setImageBitmap(origionBitmap);
	            /*
	            imageView.getLayoutParams().width = displayWidth;
	            imageView.getLayoutParams().height = displayHeight;
	            imageView.setImageBitmap(bitmap);
	            imageView.setVisibility(View.VISIBLE);
	            */
	        } catch (IOException e) {
	        	 Log.d(TAG, "image exception");
	            e.printStackTrace();
	        }
	        
	    }
	   
	    
	}
	
	public static int calculateInSampleSize(
    BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    Log.d(TAG, "Raw height: "+ height + " Raw width: "+ width);
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        Log.d(TAG, "halfHeight: "+ halfHeight +  " halfWidth: "+ halfWidth +" inSampleSize: "+ inSampleSize +  " reqHeight: "+ reqHeight );
        while ((halfHeight / inSampleSize) > reqHeight || (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
            
        }
    }
    Log.d(TAG, "inSampleSize: "+inSampleSize);
    return inSampleSize;
}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	 public Bitmap oilPaint(Bitmap bitmap, int radius, int intensityLevels){
		  int x, y;
		  Bitmap newbmp = bitmap.copy(bitmap.getConfig(), true);
		  /*Step1
		   *For each pixel, all pixels within the radius will have to be examined. 
		   *Pixels within the radius of the current pixel will be referred to as sub-pixels. 
		   *For each sub-pixel, calculate the intensity, and determine which intensity bin that intensity number falls into. 
		   *Maintain a counter for each intensity bin, which will count the number of sub-pixels which fall into each intensity bin.
		   * Also maintain the total red, green, and blue values for each bin, later; these may be used to determine the final value of the pixel.
		   * */
		   	
		    int averageR[] = new int[intensityLevels];
			int averageG[]=new int[intensityLevels];
			int averageB[]=new int[intensityLevels];
			int intensityCount[]=new int[intensityLevels];
			
		  for (x = 0; x < bitmap.getWidth(); x++) {
			int left = Math.max(0,x-radius);
			int right = Math.min(x+radius,bitmap.getWidth()-1);
		      for (y = 0; y < bitmap.getHeight(); y++) {
		    	 
		     
		    	int top = Math.max(0,y-radius);
				int bottom = Math.min(y+radius,bitmap.getHeight()-1);					
				Arrays.fill(averageR,0);
				Arrays.fill(averageG,0);
				Arrays.fill(averageB,0);
				Arrays.fill(intensityCount,0);
				int maxIndex=-1;
				
				for(int j=top;j<=bottom;++j)
				{
				for(int i=left;i<=right;++i)
					{
					
					if(!isInRange(x,y,i, j , radius)) continue;
					
					//int rgb = img.getRGB(i,j);
					//int red = (rgb >> 16)&0xFF;
					//int green = (rgb >>8)&0xFF;
					//int blue = (rgb )&0xFF;
					
					int pixel = bitmap.getPixel(i,j);
					int red = Color.red(pixel);
					int blue = Color.blue(pixel);
					int green = Color.green(pixel);
					
					
					int intensityIndex = (int)((((red+green+blue)/3.0)/256.0)*intensityLevels);
					
					intensityCount[intensityIndex]++;
					averageR[intensityIndex] += red;
					averageG[intensityIndex] += green;
					averageB[intensityIndex] += blue;
					
					if( maxIndex==-1 ||intensityCount[maxIndex]< intensityCount[intensityIndex]
						)
						{
						maxIndex = intensityIndex;
						}
					}
				}
				
				int curMax = intensityCount[maxIndex];
				int r = averageR[maxIndex] / curMax;
				int g = averageG[maxIndex] / curMax;
				int b = averageB[maxIndex] / curMax;
				
				//int rgb=((r << 16) | ((g << 8) | b));
				//img.setRGB(x,y,rgb);
				
				
				newbmp.setPixel(x, y, Color.rgb(r, g, b));
		      }
		    }
		  //newbmp.setPixel(100, 100, Color.rgb(255, 0, 0));
		  Log.d(TAG, "finish");
		  Toast.makeText(getApplicationContext(),"finish!", Toast.LENGTH_SHORT).show();
		  	return newbmp;	 
	  }
	 private boolean isInRange(int cx,int cy,int i,int j, int radius)
		{
		double d;
		//d= java.awt.geom.Line2D.ptLineDist(cx, cy, cx-radius, cy-radius, i, j);
		d=Math.sqrt((cx-i)*(cx-i)+(cy-j)*(cy-j));
		return d<radius;
		} 
	
}
