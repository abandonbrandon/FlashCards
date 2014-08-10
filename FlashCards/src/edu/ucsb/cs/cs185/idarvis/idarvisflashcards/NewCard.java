package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import edu.ucsb.cs.cs185.idarvis.idarvisflashcards.AddSectionDialog.DialogListener;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

public class NewCard extends ActionBarActivity implements DialogListener {

	public static String Folder, storage, mainlist, lastCard, lastSub,
			templist;
	public ArrayList<Save> sections = new ArrayList<Save>();
	public static boolean delete = false;
	public int num = 0;

	public class Save {
		public int Type;

		public Save(int T) {
			this.Type = T;
		}

		public int getType() {
			return this.Type;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lastCard = "";
		lastSub = "";
		Folder = getResources().getString(R.string.namesfolder);
		mainlist = getResources().getString(R.string.namelist);
		templist = getResources().getString(R.string.tempname);
		storage = Environment.getExternalStorageDirectory().toString();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// MAKE NEW MENU FOR THIS PAGE
		getMenuInflater().inflate(R.menu.newcard, menu);
		return true;
	}

	public String CreateXMLString(String sub, String name, int type) {

		StringWriter writer = new StringWriter();
		LinearLayout ll = (LinearLayout) findViewById(R.id.add);
		String lt = getResources().getString(R.string.lt);
		String lte = getResources().getString(R.string.lte);
		String gt = getResources().getString(R.string.gt);
		String information = getResources().getString(R.string.information);
		writer.write('\n');
		writer.write(getResources().getString(R.string.cardstart));
		writer.write('\n');
		writer.write(getResources().getString(R.string.namestart));
		writer.write(name);
		writer.write(getResources().getString(R.string.nameend));
		writer.write('\n');
		writer.write(getResources().getString(R.string.subjectstart));
		writer.write(sub);
		writer.write(getResources().getString(R.string.subjectend));
		writer.write('\n');
		if (type == 2) {
			for (int x = 0; x < sections.size(); x++) {
				String title = ((EditText) ll.findViewById(x + 1).findViewById(
						R.id.sectionname)).getText().toString();
				String secType = null;
				String imagepath = null;
				String info = null;
				String date = null;
				switch (sections.get(x).getType()) {
				case 0:
					secType = "Text";
					info = ((EditText) ll.findViewById(x + 1).findViewById(
							R.id.details)).getText().toString();
					writer.write(lt + secType + (x + 1) + gt + title + lte + secType + (x+1)
							+ gt + "\n");
					writer.write(lt + information + (x + 1) + gt + info + lte + information + (x+1)
							+ gt + "\n");
					break;
				case 1:
				case 2:
				}

			}
		}
		writer.write(getResources().getString(R.string.cardend));

		return writer.toString();
	}

	private Uri getOutputMediaFileUri(int type, String cardname, String subject) {
		File out = getOutputMediaFile(type, cardname, subject);
		if (out == null) {
			return null;
		}
		return Uri.fromFile(out);
	}

	private File getOutputMediaFile(int type, String filename, String subject) {

		File StorageDir = new File(storage, Folder);
		if (!StorageDir.exists()) {
			if (!StorageDir.mkdirs()) {
				return null;
			}
		}
		if (type == 2) {
			StorageDir = new File(StorageDir.getPath(), subject);
			if (!StorageDir.exists()) {
				if (!StorageDir.mkdirs()) {
					Log.d("Folder", StorageDir.getPath());
					return null;
				}
			}
		}
		if (type == 1) {
			// Create or get mainlist to add the file into
			while (true) {
				Log.d("FileNames", filename);
				String file = StorageDir.getPath() + File.separator + filename;
				File mediaFile = new File(file);
				return mediaFile;
			}
		} else if (type == 2) {
			// Check if file already exists and if it does, return error.
			while (true) {

				String file = StorageDir.getPath() + File.separator + filename;
				Log.d("FileNames", file);
				File mediaFile = new File(file);
				if (mediaFile.exists()) {
					DialogFragment error = new UsedNameDialog();
					FragmentManager fm = getSupportFragmentManager();
					error.show(fm, "");
					return null;
				} else {
					return mediaFile;
				}
			}
		} else {

			return null;
		}
	}

	public boolean MainList(String cardname, String subject) throws IOException {

		Uri mainfile = getOutputMediaFileUri(2, cardname, subject);
		if (mainfile == null) {
			Log.d("NULL", "File doesnt exist type 2");
			return false;
		}

		String output = CreateXMLString(subject, cardname, 2);
		File file = new File(mainfile.getPath());
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(file, true);
			fout.write(output.getBytes(), 0, output.length());
			fout.close();
			Log.d("Writing", "writing xml file type 2");
		} catch (FileNotFoundException e) {
			// do nothing
		}
		mainfile = getOutputMediaFileUri(1, mainlist, subject);
		if (mainfile == null) {
			return false;
		}

		output = CreateXMLString(subject, cardname, 1);
		file = new File(mainfile.getPath());
		try {
			fout = new FileOutputStream(file, true);
			fout.write(output.getBytes(), 0, output.length());
			fout.close();
			Log.d("Writing", "writing xml file");
		} catch (FileNotFoundException e) {
			// do nothing
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// This is for Creating files
		if (id == R.id.save) {
			EditText name = (EditText) findViewById(R.id.cardname);
			EditText sub = (EditText) findViewById(R.id.subjectname);
			String cardname = name.getText().toString().trim();
			String subject = sub.getText().toString().trim();
			if (cardname.length() == 0) {
				DialogFragment error = new NoNameDialog();
				FragmentManager fm = getSupportFragmentManager();
				error.show(fm, "");
				return true;
			} else if (subject.length() == 0) {
				DialogFragment error = new NoSubjectDialog();
				FragmentManager fm = getSupportFragmentManager();
				error.show(fm, "");
				return true;
			}
			try {
				if (!MainList(cardname, subject)) {
					return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lastCard = cardname;
			lastSub = subject;
			DialogFragment save = new SaveDialog();
			FragmentManager fm = getSupportFragmentManager();
			save.show(fm, "");
			return true;
		} else if (id == R.id.cancel) {
			DialogFragment cancel = new CancelDialog();
			FragmentManager fm = getSupportFragmentManager();
			cancel.show(fm, "");
		} /*else if (id == R.id.clear) {
			DialogFragment clear = new ClearDialog();
			FragmentManager fm = getSupportFragmentManager();
			clear.show(fm, "");
		}*/

		return super.onOptionsItemSelected(item);
	}

	public void AddSection(View v) {
		DialogFragment add = new AddSectionDialog();
		FragmentManager fm = getSupportFragmentManager();
		add.show(fm, "");
	}

	public void add(int type) {
		LinearLayout ll = (LinearLayout) findViewById(R.id.add);
		num++;
		switch (type) {
		case 1:
		case 2:
		case 3:
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.textsection, null);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (delete) {
						Log.d("Delete", "Do something later");
					}
					Log.d("Delete", "Do nothing");
				}
			});
			view.setId(num);
			ll.addView(view);
			Log.d("Failure", "Here");
			sections.add(new Save(0));
			break;
		}
	}
	
	public void close(){
		this.finish();
	}

	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.edit, container, false);
			return rootView;
		}
	}
}
