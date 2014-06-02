package com.kknnOTL.tripquest;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WallBlockAdapter extends ArrayAdapter<Wall> {
	
	private Activity context;
	private Wall[] data;
	
	public WallBlockAdapter(Activity context, Wall[] data) {
		super(context, R.layout.wall_row_block, data);
		this.context = context;
		this.data = new Wall[data.length];
		System.arraycopy(data, 0, this.data, 0, data.length);

	}
			 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	//Log.d("DEUBG", position + "E__E");
    	
    	LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(R.layout.wall_row_block, parent, false);
        RelativeLayout block = (RelativeLayout) row.findViewById(R.id.wall_block);
        ImageView photo = (ImageView) row.findViewById(R.id.wall_photo);
        TextView title = (TextView) row.findViewById(R.id.wall_title);
        TextView content = (TextView) row.findViewById(R.id.wall_content);

        photo.setImageResource(R.drawable.photo);
        
        //Log.d("DEUBG", String.valueOf(position));
        
        //Log.d("DEBUG", data[position].title + "TTT");
        //Log.d("DEBUG", data[position].content + "TTT");
        
        if (position < 2) {
        	block.setBackgroundColor(Color.RED);
        }
        else {
        	block.setBackgroundColor(Color.WHITE);
        }
        
        title.setText(data[position].title);
        content.setText(data[position].content);
        
        
        return row;
    }

}
