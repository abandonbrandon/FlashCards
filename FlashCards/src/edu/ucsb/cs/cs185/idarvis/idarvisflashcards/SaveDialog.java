package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SaveDialog extends DialogFragment implements View.OnClickListener {

	Button yes, no;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.save_dialog, null);
		TextView text = (TextView) v.findViewById(R.id.settext);
		EditText name = (EditText) getActivity().findViewById(R.id.cardname);
		String cardname = name.getText().toString();
		text.setText(getActivity().getResources().getString(R.string.save1)
				+ cardname.trim()
				+ getActivity().getResources().getString(R.string.save2));
		yes = (Button) v.findViewById(R.id.yes);
		yes.setOnClickListener(this);
		builder.setView(v);
		return builder.create();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.yes) {
			this.dismiss();
			//Intent intent = getActivity().getIntent();
			getActivity().finish();
			//startActivity(intent);
		}
	}

}
