package htw.bui.openreskit.waste;

import htw.bui.openreskit.domain.waste.FillLevelReading;
import htw.bui.openreskit.odata.WasteRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;

public class EnterValueFragment extends RoboFragment {

	@Inject
	private WasteRepository mWasteRepository;
	
	@InjectView
	(R.id.containerName) TextView mContainerNameView;
	
	@InjectView
	(R.id.containerDescription) TextView mContainerDescriptionView;
	
	@InjectView
	(R.id.valueSpinner) Spinner mValueSpinner;
		
	@InjectView
	(R.id.containerOldValue) TextView mContainerOldValue;
	
	@InjectView
	(R.id.saveButton) Button mSaveButton;
	
	@InjectView
	(R.id.graph) BarGraph mBarGraph;
	
	private OnValueEnteredListener mListener;
	private static FillLevel[] mfillLevels = new FillLevel[] 
			{   
				new FillLevel( 0, "leer"), 	
				new FillLevel( 0.25, "viertelvoll"), 
				new FillLevel( 0.5, "halbvoll"), 
				new FillLevel( 0.75, "dreiviertelvoll"),
				new FillLevel( 1, "voll")
			};
	
	private OnClickListener mButtonListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			String responsibleSubjectId = mPrefs.getString("default_responsibleSubject", "none");
			if (responsibleSubjectId != "none") 
			{
				FillLevel fl = (FillLevel) mValueSpinner.getSelectedItem();
				mListener.onValueEntered(mReadingId, fl.fillLevel);
			}
			else
			{
				Toast.makeText(mContext, "Bitte wählen Sie zuerst in den Einstellungen einen Mitarbeiter aus.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private long mReadingId;
	private SharedPreferences mPrefs;
	private Context mContext;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		mContext = getActivity().getApplicationContext();
		mPrefs= PreferenceManager.getDefaultSharedPreferences(mContext);
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.enter_value_form, container, false);
	}
	
	public void getReadingDetails(long readingId) 
	{
		FillLevelReading fr = mWasteRepository.getFillLevelReadingById(readingId);
		mReadingId = readingId;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

		mContainerNameView.setText(fr.getReadingContainer().getName());
		mContainerDescriptionView.setText("Abzulesen bis " + formatter.format(fr.getDueDate().getBegin()) + System.getProperty ("line.separator") + "Größe: " +String.valueOf(fr.getReadingContainer().getSize()) + " m3");
		
		ArrayAdapter<FillLevel> spinnerArrayAdapter = new ArrayAdapter<FillLevel>(getActivity(), R.layout.spinner_item, mfillLevels);
		mValueSpinner.setAdapter(spinnerArrayAdapter);
				
		int count = 0;
		for (FillLevel f : mfillLevels) 
		{
			if (f.fillLevel == fr.getFillLevel()) 
			{
				mValueSpinner.setSelection(count);
			}
			count++;
		}

		mSaveButton.setOnClickListener(mButtonListener);
		
		ArrayList<Bar> points = new ArrayList<Bar>();
		int barCount = 1; 
		List<FillLevelReading> history = mWasteRepository.getHistoryForContainer(fr.getReadingContainer().getId());
		if (history != null) 
		{
			for (FillLevelReading r : history) 
			{
				if (barCount == 5) 
				{
					break;
				}
				Bar d = new Bar();
				d.setColor(Color.parseColor("#0099CC"));
				d.setName(formatter.format(r.getEntryDate().getBegin()));
				if (r.getFillLevel() == 0) 
				{
					d.setStringValue("0");
				} 
				else if (r.getFillLevel() == 0.25) 
				{
					
					d.setStringValue("1/4");
				}
				else if (r.getFillLevel() == 0.5) 
				{
					d.setStringValue("1/2");
				}
				else if (r.getFillLevel() == 0.75) 
				{
					d.setStringValue("3/4");
				}
				else if (r.getFillLevel() == 1) 
				{
					d.setStringValue("1");
				}
				d.setShowStringValue(true);
				d.setValue((float)r.getFillLevel());
				
				if (barCount == 4) 
				{
					d.setColor(Color.parseColor("#669900"));
				}
				points.add(d);
				barCount++;
			}
			mBarGraph.setBars(points);
			mBarGraph.setUnit("none");
			mBarGraph.update();
		}
	}
	
	//Container Activity must implement this interface
    public interface OnValueEnteredListener 
    {
        public void onValueEntered(long readingId, double fillLevel);
    }
    
    //Throw if interface not implemented
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnValueEnteredListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnValueEnteredListener");
        }
    }
}
