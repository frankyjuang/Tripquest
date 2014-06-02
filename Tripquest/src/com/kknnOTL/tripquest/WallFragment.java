package com.kknnOTL.tripquest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WallFragment extends Fragment {
	
    private static String TAG="DEBUGGGG";	

	private ListView listView;
	private ArrayAdapter<Wall> wallAdapter;
	private Activity context;
	private Wall[] data;
	
	private static String identity = null;
	
	HandlerThread socketThread;
	Handler socketHandler;
	
	Socket serverSocket = null;
    BufferedReader in = null;
    BufferedWriter out = null;

    JSONObject send = null;
    JSONObject receive = null;
    JSONArray jsonArr = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View wallListLayout = inflater.inflate(R.layout.wall_list, container, false);
        listView = (ListView) wallListLayout.findViewById(R.id.wall_list);
        Bundle bundle = getArguments();
        identity = bundle.getString("id");
        Log.d(TAG, "id: " + identity);
        
        return wallListLayout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		
		socketThread = new HandlerThread("socket");
        socketThread.start();
        socketHandler = new Handler(socketThread.getLooper());
		
		createSocket();
		sendJson("getWall");
		
		createList();
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
	
    private void sendJson(String Condition) {
    	
    	if (Condition == "getWall") {
    		try {
    			send.put("iden", identity);
    			send.put("action", "get_wall");
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
    	
    	if (Condition == "getWall") {
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
	
	public void createList() {
		//String[] title = context.getResources().getStringArray(R.array.wall_title);
		//String[] content = context.getResources().getStringArray(R.array.wall_content);
		data = new Wall[jsonArr.length()];
		for(int i = 0; i < jsonArr.length(); i++) {
			data[i] = new Wall();
			try {
				data[i].title = jsonArr.getJSONObject(i).getString("author");
				
			} catch (JSONException e) {
				Log.d(TAG, "parse parse json" + e.toString());
			}
			//data[i].content = content[i];
		}
		
        wallAdapter = (ArrayAdapter<Wall>) new WallBlockAdapter(context, data);
		
        listView.setAdapter(wallAdapter);
        listView.setOnItemClickListener(new WallItemClickListener());
	}
	
	private class WallItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Intent intent = new Intent(context, WallDetailActivity.class);
            //intent.putExtra(WallDetailActivity.TITLE, data[position].title);
            //intent.putExtra(WallDetailActivity.CONTENT, data[position].content);
            //startActivity(intent);
        }
    }
	
}

