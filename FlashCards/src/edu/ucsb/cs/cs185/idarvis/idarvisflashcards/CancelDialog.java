package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class CancelDialog extends DialogFragment implements View.OnClickListener{

	Button yes, no;
	
	
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.cancel_dialog, null);
		yes = (Button) v.findViewById(R.id.yes);
		no = (Button) v.findViewById(R.id.no);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		builder.setView(v);
		return builder.create();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.yes){
			getActivity().finish();
			this.dismiss();
		}else if(v.getId() == R.id.no){
			this.dismiss();
		}
		
	}

	
}

