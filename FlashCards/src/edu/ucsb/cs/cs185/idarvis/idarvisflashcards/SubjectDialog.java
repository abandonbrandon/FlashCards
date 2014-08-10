package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SubjectDialog extends DialogFragment implements OnItemClickListener, View.OnClickListener{
	
	Button defaultbtn;
	
	public interface DialogListener {
		public void change(String subject);
		public void defaultchange();
	}
	
	DialogListener mListener;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		mListener = (DialogListener)activity;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		String items[] = getArguments().getStringArray("Array");
		ArrayList<String> things = new ArrayList<String>(Arrays.asList(items));
		View v = inflater.inflate(R.layout.subject_dialog, null);
		Collections.sort(things, String.CASE_INSENSITIVE_ORDER);
		ListView list = (ListView) v.findViewById(R.id.subjectlist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity()
				.getBaseContext(), android.R.layout.simple_list_item_1,
				things);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		defaultbtn = (Button) v.findViewById(R.id.defaultsub);
		defaultbtn.setOnClickListener(this);
		builder.setView(v);
		return builder.create();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		String item = (String) parent.getItemAtPosition(position);
		Log.d("Item", item);
		mListener.change(item);
		this.dismiss();
		
	}

	@Override
	public void onClick(View v) {
		mListener.defaultchange();
		this.dismiss();
	}

	

}
