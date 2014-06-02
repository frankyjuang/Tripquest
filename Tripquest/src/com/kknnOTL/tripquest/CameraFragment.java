package com.kknnOTL.tripquest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CameraFragment extends Fragment {
	
    private static String TAG="DEBUGGGG";	

	private Activity context;
	
	private static String identity = null;
	
	HandlerThread socketThread;
	Handler socketHandler;
	
	Socket serverSocket = null;
    BufferedReader in = null;
    BufferedWriter out = null;

    JSONObject send = null;
    JSONObject receive = null;
    JSONArray jsonArr = null;
    
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    
    private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	private String mCurrentPhotoPath;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View cameraListLayout = inflater.inflate(R.layout.camera, container, false);
        Bundle bundle = getArguments();
        identity = bundle.getString("id");
        Log.d(TAG, "id: " + identity);
        
        return cameraListLayout;
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		
		socketThread = new HandlerThread("socket");
        socketThread.start();
        socketHandler = new Handler(socketThread.getLooper());

        dispatchTakePictureIntent(1);
        
		createSocket();
		//sendJson("getCamera");

	}
	
	private void dispatchTakePictureIntent(int actionCode) {
		
		File f = null;
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		try {
			f = setUpPhotoFile();
			mCurrentPhotoPath = f.getAbsolutePath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mCurrentPhotoPath = null;
		}
	}
	
	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	public void createSocket() {
		
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				timer.cancel();
	    		Log.d(TAG, getString(R.string.connectionCreated));
	    		try {
	    			serverSocket = new Socket(getString(R.string.targetIP), Integer.parseInt(getString(R.string.targetPort)));
	    			in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
	    			out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
	    			Log.d(TAG, getString(R.string.socketCreationSucceeded));
	    		} catch (IOException e) {
	    			Log.d(TAG, e.toString());
	    		}
			}
		};
		timer.schedule(task, 3000, 3000);
	
	}
	/*
    private void sendJson(String Condition) {
    	
    	if (Condition == "getCamera") {
    		try {
    			send.put("iden", identity);
    			send.put("action", "get_camera");
    			send.put("args", "");
    		} catch (JSONException e) {
    			Log.d(TAG, "set json" + e.toString());
    		}
    	}
    	else {
    		
    	}
    	
    	try {
			out.write(send.toString());
			socketHandler.post(receiveJson);
		} catch (IOException e) {
			Log.d(TAG, "send json" + e.toString());
		}
    	
    	if (Condition == "getCamera") {
    		try {
    			jsonArr = receive.getJSONArray("");
			} catch (JSONException e) {
				Log.d(TAG, "parse json" + e.toString());
			}
    	}
    	
    }
    
    Runnable receiveJson = new Runnable() {
    	String line;
    	public void run() {
    		try {
    			line = in.readLine();
    			if (line.equals("")) {
        			socketHandler.postDelayed(receiveJson, 500);
    			}
    			else {
    				receive = new JSONObject(line);
    			}
    		}
    		catch (Exception e) {
    			Log.d(TAG, "receive json" + e.toString());
    		}
    	}
    };
    */
	
	
	
}

