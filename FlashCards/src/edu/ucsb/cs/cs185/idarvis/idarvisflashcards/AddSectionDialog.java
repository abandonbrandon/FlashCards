package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class AddSectionDialog extends DialogFragment implements View.OnClickListener{

	Button no, camera, text, date;
	
	public interface DialogListener {
		public void add(int type);
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
		View v = inflater.inflate(R.layout.addsection_dialog, null);
		camera = (Button) v.findViewById(R.id.camera);
		text = (Button) v.findViewById(R.id.text);
		/*date = (Button) v.findViewById(R.id.date);*/
		no = (Button) v.findViewById(R.id.Negative);
		/*date.setOnClickListener(this);*/
		camera.setOnClickListener(this);
		text.setOnClickListener(this);
		no.setOnClickListener(this);
		builder.setView(v);
		return builder.create();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.Negative){
			this.dismiss();
		}else if(v.getId() == R.id.camera){
			mListener.add(1);
			this.dismiss();
		}else if(v.getId() == R.id.text){
			mListener.add(3);
			this.dismiss();
		}/*else if(v.getId() == R.id.date){
			mListener.add(2);
			this.dismiss();
		}*/
		
	}

	
}

