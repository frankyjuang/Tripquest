package com.kknnOTL.tripquest;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MissionFragment extends Fragment {
	
	private ListView listView;
	private ArrayAdapter<Mission> missionAdapter;
	private Activity context;
	private Mission[] data;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			 				 Bundle savedInstanceState) {
        View missionListLayout = inflater.inflate(R.layout.mission_list, container, false);
        listView = (ListView) missionListLayout.findViewById(R.id.mission_list);
        return missionListLayout;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		createList();
	}
	
	
	public void createList() {
		String[] title = context.getResources().getStringArray(R.array.mission_title);
		String[] content = context.getResources().getStringArray(R.array.mission_content);
		data = new Mission[title.length];
		for(int i = 0; i < title.length; i++) {
			data[i] = new Mission();
			data[i].title = title[i];
			data[i].content = content[i];
		}
		
        missionAdapter = (ArrayAdapter<Mission>) new MissionBlockAdapter(context, data);
		
        listView.setAdapter(missionAdapter);
        listView.setOnItemClickListener(new MissionItemClickListener());
	}
	
	private class MissionItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Intent intent = new Intent(context, MissionDetailActivity.class);
            //intent.putExtra(MissionDetailActivity.TITLE, data[position].title);
            //intent.putExtra(MissionDetailActivity.CONTENT, data[position].content);
            //startActivity(intent);
        }
    }
	
}

