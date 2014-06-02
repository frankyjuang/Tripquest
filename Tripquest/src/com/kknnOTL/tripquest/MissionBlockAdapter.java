package com.kknnOTL.tripquest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MissionBlockAdapter extends ArrayAdapter<Mission> {
	
	private Activity context;
	private Mission[] data;
	
	public MissionBlockAdapter(Activity context, Mission[] data) {
		super(context, R.layout.wall_row_block, data);
		this.context = context;
		this.data = new Mission[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);

	}
			 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	//Log.d("DEUBG", position + "E__E");
    	
    	LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.wall_row_block, parent, false);
        ImageView photo = (ImageView) row.findViewById(R.id.wall_photo);
        TextView title = (TextView) row.findViewById(R.id.wall_title);
        TextView content = (TextView) row.findViewById(R.id.wall_content);

        photo.setImageResource(R.drawable.photo);
        
        //Log.d("DEUBG", String.valueOf(position));
        //Log.d("DEBUG", data[position].title + "TTT");
        //Log.d("DEBUG", data[position].content + "TTT");
       
        title.setText(data[position].title);
        content.setText(data[position].content);
        
        return row;
    }

}
