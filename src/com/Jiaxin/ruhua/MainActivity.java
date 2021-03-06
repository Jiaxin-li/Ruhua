package com.Jiaxin.ruhua;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
	private ImageButton processButton;
	private ProgressBar progressBar; 
	private ImageView imageView;
	private Bitmap origionBitmap = null;
	private Bitmap modifiedBitmap = null;
	private Uri uri;
	private int compressRate = 4;

	private TextView textProgress;
	private final int radiusBarID = R.id.RadiusBar; 
	private final int intensityBarID = R.id.IntensityBar;
	private int radius =1;
	private int intensity =1;
	private final int PICK_IMAGE_REQUEST = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private final static String TAG ="RUH_main";
	private BitmapFactory.Options options;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 Log.d(TAG, "on create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		radiusBar = (SeekBar)findViewById(radiusBarID);
		intensityBar = (SeekBar)findViewById(intensityBarID);
		imageView = (ImageView) findViewById(R.id.imageView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar.setVisibility(android.view.View.INVISIBLE);
		textProgress = (TextView)findViewById(R.id.textView1);
		addButton =  (ImageButton)findViewById(R.id.addButton);
		saveButton =  (ImageButton)findViewById(R.id.saveButton);
		cameraButton =  (ImageButton)findViewById(R.id.cameraButton);
		processButton =  (ImageButton)findViewById(R.id.processButton);
		addButton.setOnClickListener(new OnClickListener() {			 
			@Override
			public void onClick(View arg0) {
					//Toast.makeText(getApplicationContext(),"addButton is clicked!", Toast.LENGTH_SHORT).show(); 
					 Log.d(TAG, "addButton is clicked!");
					Intent intent = new Intent();
					// Show only images, no videos or anything else
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					// Always show the chooser (if there are multiple options available)
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
				}			 
			});
		
		cameraButton.setOnClickListener(new OnClickListener() {			 
			@Override
			public void onClick(View arg0) {
					//Toast.makeText(getApplicationContext(),"addButton is clicked!", Toast.LENGTH_SHORT).show(); 
					 Log.d(TAG, "cameraButton is clicked!");
					
					// Show only images, no videos or anything else
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					//intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					// Always show the chooser (if there are multiple options available)
					 startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				}			 
			});
		saveButton.setOnClickListener(new OnClickListener() {			 
			@Override
			public void onClick(View arg0) {

			   //Toast.makeText(getApplicationContext(),"saveButton is clicked!", Toast.LENGTH_SHORT).show();
				onTakePicture( );
			   Log.d(TAG, "saveButton is clicked!");
				}			 
			});
		processButton.setOnClickListener(new OnClickListener() {			 
			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "processButton is clicked!");
				if(origionBitmap==null){
					Toast.makeText(getApplicationContext(),"please select an image first!", Toast.LENGTH_SHORT).show();
				}
				else{
					progressBar.setVisibility(android.view.View.VISIBLE);
					 new Imageprocessing().execute(origionBitmap);
				}			   
		
			  
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
	    	
	        uri = data.getData();
	        fetchImage(uri);
	    }
	    
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	    	//Log.d(TAG, data.getDataString());
	        if (resultCode == RESULT_OK ) {
	        	origionBitmap = (Bitmap) data.getExtras().get("data");
	        	imageView.setImageBitmap(origionBitmap);
	            // Image captured and saved to fileUri specified in the Intent
	        	/*	
	            if(data.getData() == null){
	            	Log.d(TAG, "data getdata null");
	            }
	            else{
	            	uri = data.getData();
	            	fetchImage(uri);
	            }
	        */
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        	Log.d(TAG, "cancel");
	        } else {
	            // Image capture failed, advise user
	        	Log.d(TAG,"fail");
	        }
	    }
	   
	    
	}
	
	void fetchImage(Uri uri){
		try {	        	
        	InputStream is = getContentResolver().openInputStream(uri);
        	Log.d(TAG, uri.toString());        	
        	Log.d(TAG, "compressRate:" + compressRate);
        	options = new BitmapFactory.Options();
        	options.inSampleSize = compressRate;
        	options.inJustDecodeBounds = false;
        	origionBitmap = BitmapFactory.decodeStream(is, null, options);
            is.close();
            
            imageView.setImageBitmap(origionBitmap);
            
        } catch (IOException e) {
        	 Log.d(TAG, "image exception");
            e.printStackTrace();
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
	
	public void onTakePicture( ) {		
		
		if (! isExternalStorageReadable() && ! isExternalStorageReadable()) return;
		
		Long currentTime = System.currentTimeMillis() / 1000L; // Long time
		
		// Create pictures directory
		File picturesDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "RUHUA");
		picturesDirectory.mkdirs();
		
		String picturesDirectoryPath = picturesDirectory.getPath();
		String newImagePath = picturesDirectoryPath + File.separator + "RUHUA" + currentTime + ".jpg";
		Log.d(TAG,"newImagePath :"+ newImagePath);
		File file = new File(newImagePath);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		
		
		if (modifiedBitmap == null) {
			Toast.makeText(MainActivity.this, "no image to store", Toast.LENGTH_SHORT).show();
			return;
		}
		
		FileOutputStream fos = null;
		
		try {
	        file.createNewFile();
	       
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		modifiedBitmap .compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		
		try {
			fos = new FileOutputStream(file);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		
		try {
	        fos.write(bytes.toByteArray());
	        fos.close();
	        Toast.makeText(MainActivity.this, "Image stored: " + newImagePath, Toast.LENGTH_SHORT).show();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}


private class Imageprocessing extends AsyncTask<Bitmap, Void, Bitmap> {
	
	
	
	
	
	 private Bitmap oilPaint(Bitmap[] origionBitmaps, int radius, int intensityLevels){
		 Bitmap origionBitmap = origionBitmaps[0]; // not good practice temp solution
		 int x, y;
		  Bitmap newbmp = origionBitmap.copy(origionBitmap.getConfig(), true);
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
			int imageHeight = origionBitmap.getHeight();
			int imageWidth = origionBitmap.getWidth();
			progressBar.setMax(imageHeight*imageWidth);
			Log.d(TAG, "progress max:" + imageHeight*imageWidth);
		  for (x = 0; x < imageWidth ; x++) {
			int left = Math.max(0,x-radius);
			int right = Math.min(x+radius,imageWidth-1);			
		      for (y = 0; y < imageHeight; y++) {		    	
		    	int top = Math.max(0,y-radius);
				int bottom = Math.min(y+radius,imageHeight-1);					
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
					
					int pixel = origionBitmap.getPixel(i,j);
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
				progressBar.incrementProgressBy(1);
		      }
		    }
		  //newbmp.setPixel(100, 100, Color.rgb(255, 0, 0));
		  Log.d(TAG, "finish");
		  //Toast.makeText(getApplicationContext(),"finish!", Toast.LENGTH_SHORT).show();
		  	return newbmp;	 
	  }
	 private boolean isInRange(int cx,int cy,int i,int j, int radius)
		{
		double d;
		//d= java.awt.geom.Line2D.ptLineDist(cx, cy, cx-radius, cy-radius, i, j);
		d=Math.sqrt((cx-i)*(cx-i)+(cy-j)*(cy-j));
		return d<radius;
		}
	@Override
	protected Bitmap doInBackground(Bitmap... origionBitmap) {
		// TODO Auto-generated method stub
		modifiedBitmap = oilPaint(origionBitmap, radius, intensity);
		return modifiedBitmap;
	}
	@Override
	protected void onPostExecute(Bitmap result) {
		imageView.setImageBitmap(result);
		progressBar.setVisibility(android.view.View.INVISIBLE);
    }


	
	}

}