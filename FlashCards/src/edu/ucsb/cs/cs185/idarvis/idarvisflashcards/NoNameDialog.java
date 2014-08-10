package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class NoNameDialog extends DialogFragment implements View.OnClickListener{
	Button okay;
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.noname_dialog, null);
		okay = (Button) v.findViewById(R.id.okay);
		okay.setOnClickListener(this);
		builder.setView(v);
		return builder.create();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.okay){
			this.dismiss();
		}
	}

}
