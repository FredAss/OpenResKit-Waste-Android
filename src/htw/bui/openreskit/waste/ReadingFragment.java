package htw.bui.openreskit.waste;

import htw.bui.openreskit.domain.waste.FillLevelReading;
import htw.bui.openreskit.odata.RepositoryChangedListener;
import htw.bui.openreskit.odata.WasteRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.inject.Inject;

public class ReadingFragment extends RoboFragment 
{

	@Inject
    private WasteRepository mRepository;
	
	@InjectView
	(R.id.readingListView) ListView mListView;
	
	private Parcelable mListState;
    private List<FillLevelReading> fillLevelReadings;    
    private Activity mContext;
    private OnReadingSelectedListener mListener;
    private long mSeriesId;
    
    private RepositoryChangedListener mEventListener = new RepositoryChangedListener() 
    {
		public void handleRepositoryChange(EventObject e) {
			updateReadings(mSeriesId);
		}
	};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
    	// Inflate the layout for this fragment
    	return inflater.inflate(R.layout.reading_list, container, false);
	}
	
	@Override
 	public void onActivityCreated(Bundle savedInstanceState) 
    {
        super.onActivityCreated(savedInstanceState);
        
        mContext = getActivity();
        mRepository.addEventListener(mEventListener);
        
        if (savedInstanceState != null) 
        {
        	mListState = savedInstanceState.getParcelable("listState");
        }
        updateReadings(mSeriesId);
        
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setOnItemClickListener(mListItemClickListener);
    }

	public void updateReadings(long seriesId) 
	{
		mSeriesId = seriesId;
		if(seriesId != 0) 
		{
			mContext = getActivity();
	    	fillLevelReadings = new ArrayList<FillLevelReading>();
	    	fillLevelReadings = mRepository.getFillReadingsForSeries(seriesId);
	    	Collections.sort(fillLevelReadings);
			ReadingAdapter adapter = new ReadingAdapter(mContext, R.layout.reading_list_row, fillLevelReadings);
	    	mListView.setAdapter(adapter);
	    }
	}

	@Override
	public void onResume() {
	    super.onResume();
	    if(mListState!=null){
	        mListView.onRestoreInstanceState(mListState);
	    } 
	    mListState = null;
	}

    @Override
    public void onSaveInstanceState (Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        Parcelable state = mListView.onSaveInstanceState();
        outState.putParcelable("listState", state);
    }
    
    private OnItemClickListener mListItemClickListener = new OnItemClickListener() 
    {
		public void onItemClick(AdapterView<?> arg0, View view, int position, long id) 
		{
			mListener.onReadingSelected(id);
		}
	};
    
	public void removeRepositoryListener() 
	{
		mRepository.removeEventListener(mEventListener);
	}
	
    //Container Activity must implement this interface
    public interface OnReadingSelectedListener 
    {
        public void onReadingSelected(long id);
    }
    
    //Throw if interface not implemented
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnReadingSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnReadingSelectedListener");
        }
    }

	public void hilightReading(long readingId) 
	{
		int counter = 0;
		for (FillLevelReading r : fillLevelReadings) 
		{
			if(r.getId() == readingId) 
			{
				mListView.setItemChecked(counter, true);
				mListView.smoothScrollToPosition(counter);
			}
			counter++;
		}
	}
}
