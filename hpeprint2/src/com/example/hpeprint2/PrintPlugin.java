package com.example.hpeprint2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UnknownFormatConversionException;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

public class PrintPlugin extends CordovaPlugin {

//private static final String	TAG	= PrintPlugin.class.getSimpleName();
	
    public static File captureScreen(Bitmap.CompressFormat format, View  v1)
    throws IOException, UnknownFormatConversionException
    {
    	File tempFile = null;
    	    	
    	// save to temporary file
    	File dir = new File( Environment.getExternalStorageDirectory(), "temp" );
		if( dir.exists() || dir.mkdirs() )
    	{
			FileOutputStream fos = null;
			try
			{
				String strExt = null;
				switch( format )
				{
					case PNG:
						strExt = ".png";
						break;
						
					case JPEG:
						strExt = ".jpg";
						break;
						
					default:
						throw new UnknownFormatConversionException( "unknown format: " + format );
				}
				
				View view = v1;
				view.setDrawingCacheEnabled(true);
				
				
				Matrix matrix = new Matrix();
				//matrix.postScale(2.0f, 2.0f);
				matrix.postScale(Math.max(1, 2 - (1 * 0.5f)), Math.max(1, 2 - (1 * 0.5f)));
				Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 1, 1,view.getWidth()/2, view.getHeight()/2, matrix, true);
				
				v1.setDrawingCacheEnabled(false);
//				File imageFile = File.createTempFile( "bitmap", strExt, dir );
				File imageFile = new File(dir, "bitmap-cropped"+strExt);
				try {
					fos = new FileOutputStream(imageFile);
				    bitmap.compress(format, 100, fos);
				    

				} catch (FileNotFoundException e) {
				    e.printStackTrace();
				}

				tempFile = imageFile;
			}
			finally
			{
				try
				{
					fos.flush();
				    fos.close();
				}
				catch( Exception e ) {}
			}
    	}		
    	
    	return tempFile;
    }


	@Override
	public boolean execute(String action,  JSONArray arguments,  final CallbackContext callbackContext) throws JSONException {

		if (action.equals("print")) {
				final Activity ctx = this.cordova.getActivity();
			
				try {
					
					
						Uri uri = Uri.fromFile(captureScreen(Bitmap.CompressFormat.PNG, webView.getRootView()));
						Intent intent = new Intent("org.androidprinting.intent.action.PRINT");
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.setDataAndType(uri,"image/*");
						ctx.startActivity(intent);  
						callbackContext.success("OK");
						return true;
						
				} catch (UnknownFormatConversionException e) {
					e.printStackTrace();
					callbackContext.error("");
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					callbackContext.error("");
					return false;
				}
		
		}

		callbackContext.error("");
		return false;
	}

}
