package htw.bui.openreskit.waste;


import htw.bui.openreskit.domain.waste.FillLevelReading;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadingAdapter extends BaseAdapter {
	@SuppressLint("SimpleDateFormat")
	private final SimpleDateFormat mDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
	private final Activity mContext;
	private final List<FillLevelReading> mReadings;
	private final int mRowResID;
	private final LayoutInflater mLayoutInflater;

	public ReadingAdapter(final Activity context, final int rowResID, final List<FillLevelReading> readings) 
	{
		mContext = context;
		mRowResID = rowResID;
		mReadings = readings;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
	}
	
	public int getCount() 
	{
		return mReadings.size();
	}

	public Object getItem(int position) 
	{
		return mReadings.get(position);
	}

	public long getItemId(int position) 
	{
		return mReadings.get(position).getId();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) 
		{
			rowView = mLayoutInflater.inflate(mRowResID, null);
					
			ReadingViewHolder viewHolder = new ReadingViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.readingListText1);
			viewHolder.description = (TextView) rowView.findViewById(R.id.readingListText2);
			viewHolder.level = (ImageView) rowView.findViewById(R.id.levelIcon);
			viewHolder.statusIcon = (ImageView) rowView.findViewById(R.id.StatusIcon);
			rowView.setTag(viewHolder);
		}
		final FillLevelReading singleReading = mReadings.get(position);
		ReadingViewHolder holder = (ReadingViewHolder) rowView.getTag();
		
		holder.name.setText(singleReading.getReadingContainer().getName());
	
		String formattedDueDate = mDateFormatter.format(singleReading.getDueDate().getBegin());
		holder.description.setText(formattedDueDate);
		
		double level =  singleReading.getFillLevel();
		if (level == 0.25) 
		{
			holder.level.setBackgroundResource(R.drawable.level_25);
		}
		
		else if (level == 0.5) 
		{
			holder.level.setBackgroundResource(R.drawable.level_50);
		}
		else if (level ==  0.75) 
		{
			holder.level.setBackgroundResource(R.drawable.level_75);
		}
		
		else if (level == 1) 
		{
			holder.level.setBackgroundResource(R.drawable.level_100);
		}
		else if (level == 0) 
		{
			holder.level.setBackgroundResource(R.drawable.level_0);
		}
		
		if (singleReading.getEntryDate() == null) 
		{
			if (Build.VERSION.SDK_INT >= 16) 
			{
			    holder.statusIcon.setBackground(null);
			} 
			else 
			{
				 holder.statusIcon.setBackgroundDrawable(null);
			}
		}
		else
		{
			holder.statusIcon.setBackgroundResource(R.drawable.navigation_accept);
		}

		
		return rowView;
	}

	 static class ReadingViewHolder 
	{
		public TextView name;
		public TextView description;
		public ImageView level;
		public ImageView statusIcon;
	}
	
}
