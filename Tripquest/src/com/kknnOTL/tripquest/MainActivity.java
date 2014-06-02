package com.kknnOTL.tripquest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {
	
    private static String TAG="DEBUGGGG";
	
	private DrawerLayout drawerLayout;
    private ListView functionList;
    private ActionBarDrawerToggle drawerToggle;

    private String drawerTitle, title;
    private int currentCheckedPosition;
    private String[] functionTitle;
	static boolean isdrawerOpen;
	
	private WifiManager wifiMgr;
	Handler UIHandler = new Handler();
	HandlerThread socketThread;
	Handler socketHandler;
    
	Socket serverSocket = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    
    JSONObject send = new JSONObject();
    JSONObject receive = new JSONObject();
    
    private static String identity = null;
	
	static final String TITLE = "com.kknnOTL.Tripquest.title";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        title = drawerTitle = (String) getTitle();
        functionTitle = getResources().getStringArray(R.array.main_function);
        
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        functionList = (ListView) findViewById(R.id.function_list);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        functionList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, functionTitle));
        functionList.setOnItemClickListener(new FunctionItemClickListener());
        
        drawerToggle = new ActionBarDrawerToggle(
                this,                  
                drawerLayout,         
                R.drawable.ic_drawer,  
                R.string.drawer_open,  
                R.string.drawer_close  
                ) {
            public void onDrawerClosed(View view) {
            	super.onDrawerClosed(view);
                getActionBar().setTitle(title);
                invalidateOptionsMenu(); 
            }

            public void onDrawerOpened(View view) {
            	super.onDrawerOpened(view);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); 
                Log.d("DEBUG", "E__E");
            }
        };
        
        drawerLayout.setDrawerListener(drawerToggle);
        
        if (savedInstanceState == null) {
            currentCheckedPosition = 0;
        }
        else {
        	currentCheckedPosition = savedInstanceState.getInt("currentCheckedPosition");       	
        }
        selectItem(currentCheckedPosition);
    	setTitle(title);
        drawerLayout.closeDrawer(functionList);
        
        checkGooglePlayServices();
        
        socketThread = new HandlerThread("socket");
        socketThread.start();
        socketHandler = new Handler(socketThread.getLooper());
        
        setWifiState();
        //connectTargetAP();
        createSocket();
        
        sendJson("init");
        
    }
   
    private void sendJson(String Condition) {
    	/*
    	if (Condition == "init") {
    		try {
    			send.put("username", "NekOrz");
    			//send.put("aaa", "AAA");
    		} catch (JSONException e) {
    			Log.d(TAG, "set json" + e.toString());
    		}
    	}
    	else {
    		
    	}*/
    	
    	try {
			out.write(send.toString());
			socketHandler.post(receiveJson);
		} catch (IOException e) {
			Log.d(TAG, "send json" + e.toString());
		}
    	
    	if (Condition == "init") {
    		try {
				identity = receive.getString("iden");
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
    
    public void setWifiState() {
    	
    	wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	
    	if (wifiMgr.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
    		if (wifiMgr.setWifiEnabled(true)) {
    			Log.d(TAG, getString(R.string.setWifiStateSucceeded));
    		} else {
    			Log.d(TAG, getString(R.string.setWifiStateFailed));
    		}
    	} else {
    		Log.d(TAG, getString(R.string.setWifiStateSucceeded));
    	}
    	
    }
    
    public void connectTargetAP() {
    	
    	wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	
    	final Timer timer = new Timer();
    	TimerTask task = new TimerTask() {
       		public void run() {
		    	boolean succ = false;
		    	
		    	WifiConfiguration wificonf = new WifiConfiguration();
		    	wificonf.SSID = "\"" + getString(R.string.targetAPSSID) + "\"";
		    	wificonf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
				wifiMgr.addNetwork(wificonf);
				
		    	List<WifiConfiguration> list = wifiMgr.getConfiguredNetworks();
		    	if (list != null) {
		    		timer.cancel();
			    	for( WifiConfiguration i : list ) {
			    		//Log.d(TAG, i.SSID);
			    	    if(i.SSID != null && i.SSID.equals("\"" + getString(R.string.targetAPSSID) + "\"")) {
			    	    	wifiMgr.disconnect();
			    	    	wifiMgr.enableNetwork(i.networkId, true);
			    	    	wifiMgr.reconnect();
			    	    	succ = true;
			    	    	break;
			    	    }
			    	}
		    	}
		    	if (!succ) {
		    		Log.d(TAG, getString(R.string.connectTartgetAPFailed));
		    	}
       		}
    	};
    	timer.schedule(task, 3000, 3000);
    }

	public void createSocket() {
		
		wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		final Timer timer = new Timer();
		
		TimerTask task = new TimerTask() {
			public void run() {
				
				if (wifiMgr.getConnectionInfo().getSSID().equals("\"" + getString(R.string.targetAPSSID) + "\"") || 
					wifiMgr.getConnectionInfo().getSSID().equals(getString(R.string.targetAPSSID))) {
					timer.cancel();
					UIHandler.post(new UIRunnable(getString(R.string.connectionCreated)));
		    		Log.d(TAG, getString(R.string.connectionCreated));
		    		socketHandler.postDelayed(socketCreation, 1000);
		    	} else {
		    		UIHandler.post(new UIRunnable(getString(R.string.connectionNotCreated)));
		    		Log.d(TAG, getString(R.string.connectionNotCreated));
		    		connectTargetAP();
		    	}
			}
		};
		timer.schedule(task, 3000, 3000);
	
	}
	
	class UIRunnable implements Runnable {
    	private String message;
    	UIRunnable(String msg) {
    		message = msg;
    	}
    	public void run() {
    		if (message == "ERROR") {
    			
    		} else if (message == "TEST") {
    		
    		}
    		else {
    			Log.d(TAG, message);
    		}
    	}
    }
	
	Runnable socketCreation = new Runnable() {
    	public void run() {
    		try {
    			serverSocket = new Socket(getString(R.string.targetIP), Integer.parseInt(getString(R.string.targetPort)));
    			in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    			out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
    			UIHandler.post(new UIRunnable(getString(R.string.socketCreationSucceeded)));
    			Log.d(TAG, getString(R.string.socketCreationSucceeded));
    		}
    		catch (Exception e) {
    			UIHandler.post(new UIRunnable(getString(R.string.socketCreationFailed)));
    			Log.d(TAG, getString(R.string.socketCreationFailed));
    			socketHandler.postDelayed(socketCreation, 1000);
    		}
    	}
    };
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	savedInstanceState.putInt("currentCheckedPosition", currentCheckedPosition);
    	super.onSaveInstanceState(savedInstanceState);
    }
    
    private class FunctionItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = drawerLayout.isDrawerOpen(functionList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch(item.getItemId()) {
        case R.id.action_websearch:
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void selectItem(int position) {
        Fragment fragment = null;
        String title = null;

	    switch(position) {
	        case 0:
	        	fragment = new WallFragment();
	        	Log.d(TAG, "position 0");
	        	break;
	        case 1:
	        	fragment = new MapsFragment();
	        	Log.d(TAG, "position 1");
	        	break;
	        case 2:
	        	fragment = new MissionFragment();
	        	Log.d(TAG, "position 2");
	        	break;
	        case 3:
	        	fragment = new CameraFragment();
	        	Log.d(TAG, "position 3");
	        case 4:
	        	fragment = new SettingsFragment();
	        	Log.d(TAG, "position 4");
	        	break;
	    }
	        
        title = functionTitle[position];
	        
	    functionList.setItemChecked(position, true);
	    currentCheckedPosition = position;
    	
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
        			   .replace(R.id.content_frame, fragment)
        			   .commit();
        Bundle args = new Bundle();
        args.putString("id", identity);
        fragment.setArguments(args);
        setTitle(title);
        drawerLayout.closeDrawer(functionList);
    }
    
    private void checkGooglePlayServices(){
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (result) {
            case ConnectionResult.SUCCESS:
                Log.d(TAG, "SUCCESS");
                break;

            case ConnectionResult.SERVICE_INVALID:
                Log.d(TAG, "SERVICE_INVALID");
                GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_INVALID, this, 0).show();
                break;

            case ConnectionResult.SERVICE_MISSING:
                Log.d(TAG, "SERVICE_MISSING");
                GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_MISSING, this, 0).show();
                break;

            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.d(TAG, "SERVICE_VERSION_UPDATE_REQUIRED");
                GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, this, 0).show();
                break;

            case ConnectionResult.SERVICE_DISABLED:
                Log.d(TAG, "SERVICE_DISABLED");
                GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_DISABLED, this, 0).show();
                break;
        }
    }
    
}
