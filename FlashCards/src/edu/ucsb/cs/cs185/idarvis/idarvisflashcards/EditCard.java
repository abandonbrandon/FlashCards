package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

public class EditCard extends ActionBarActivity implements DeleteDialog.DialogListener, AddSectionDialog.DialogListener{

	public static String Folder, storage, mainlist, lastCard, lastSub,
			templist;
	public static ArrayList<Save> sections;
	public static boolean delete = false;
	public static int num = 0;

	public static class Save {
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

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		Folder = getResources().getString(R.string.namesfolder);
		mainlist = getResources().getString(R.string.namelist);
		storage = Environment.getExternalStorageDirectory().toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// MAKE NEW MENU FOR THIS PAGE
		getMenuInflater().inflate(R.menu.edit, menu);
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
			for (int x = 0; x < num; x++) {
				Log.d("Number of fields", "" + num);
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
					writer.write(lt + secType + (x + 1) + gt + title + lte
							+ secType + (x + 1) + gt + "\n");
					writer.write(lt + information + (x + 1) + gt + info + lte
							+ information + (x + 1) + gt + "\n");
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
	
	public void Delete(){
		EditText name = (EditText) findViewById(R.id.cardname);
		EditText sub = (EditText) findViewById(R.id.subjectname);
		String cardname = name.getText().toString();
		String subname = sub.getText().toString();
		File file = new File(storage + File.separator + Folder + File.separator + lastSub + File.separator + lastCard);
		if(file.exists()){
			Log.d("Error", "File Exists. Path: " + file.getPath());
			replaceFile(lastCard, lastSub, cardname, subname);
		}else{
			DialogFragment error = new NoCardDialog();
			FragmentManager fm = getSupportFragmentManager();
			error.show(fm, "");
		}
		this.finish();
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
				return mediaFile;
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
			fout = new FileOutputStream(file, false);
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

	public void replaceFile(String lastC, String lastS, String cardn,
			String subj) {
		// Do stuff here to replace the old file in the lastS folder and also
		// replace the lines in the CardNames file with the master list of items
		// and their corresponding subjects. Copy this to the edit card file
		// as well.

		File file = new File(storage + File.separator + Folder + File.separator
				+ lastS + File.separator + lastC);
		if (!file.exists()) {
			Log.d("No file", "Could not find file/directory " + file.getPath());
			return;
		}
		if (file.delete()) {
			Log.d("Delete file", "Deleted " + lastC + " from " + file.getPath());
			File[] contents = new File(storage + File.separator + Folder
					+ File.separator + lastS).listFiles();
			if (contents.length == 0) {
				File directory = new File(storage + File.separator + Folder
						+ File.separator + lastS);
				if (directory.delete()) {
					Log.d("Remove directory", "Removing directory " + lastS
							+ " from " + directory.getPath());
					removeFromMain(lastC, lastS);
					return;
				}
				Log.d("Failure",
						"Could not remove file/directory "
								+ directory.getPath());
			}
			removeFromMain(lastC, lastS);
			return;
		}
		Log.d("Failure", "Could not remove file/directory " + file.getPath());

		// FILL ME IN
	}

	public void removeFromMain(String lastC, String lastS) {
		File mainfile = new File(storage + File.separator + Folder
				+ File.separator + mainlist);
		File tempfile = new File(storage + File.separator + Folder
				+ File.separator + templist);

		try {
			Log.d("Read/Writers", "did not fail?");
			FileInputStream fin = new FileInputStream(mainfile);
			Log.d("Read/Writers", "did not fail?");
			FileOutputStream fout = new FileOutputStream(tempfile);
			Log.d("Read/Writers", "did not fail?");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fin));
			Log.d("Read/Writers", "did not fail?");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					fout));
			Log.d("Read/Writers", "did not fail?");
			String line;
			final Pattern pattern = Pattern.compile("<(.+)>(.+)<(.+)>");
			Matcher matcher;
			while ((line = reader.readLine()) != null) {
				matcher = pattern.matcher(line);
				if (!matcher.find()) {
					writer.write(line);
					writer.write("\n");
					Log.d("Writing others", line);
					continue;
				}
				if (matcher.group(1).compareTo("Name") != 0) {
					writer.write(matcher.group(0));
					Log.d("Writing", matcher.group(0));
					writer.write("\n");
					continue;
				} else {
					if (matcher.group(2).compareTo(lastC) == 0) {
						Log.d("Skip", "Skipping deleted file");
						Log.d("Line0", line);
						line = reader.readLine();
						Log.d("Line1", line);
						line = reader.readLine();
						Log.d("Line2", line);
						line = reader.readLine();
						if (line == null) {
							break;
						}
						Log.d("Line3", line);
						continue;
					} else {
						writer.write(matcher.group(0));
						Log.d("Writing", matcher.group(0));
						writer.write("\n");
						continue;
					}
				}
			}

			if (mainfile.delete()) {
				tempfile.renameTo(new File(storage + File.separator + Folder
						+ File.separator + mainlist));
			}
			writer.close();
			reader.close();
			fin.close();
		} catch (IOException e) {
			Log.d("Error", "Could not change the mainlist file");
		}
		// Copy the mainlist file across without the lines that we want to
		// remove
		// and then rename the tempfile to the mainlist's name
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
			String cardname = name.getText().toString();
			String subject = sub.getText().toString();
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
			Log.d("LastC", lastCard);
			Log.d("LastS", lastSub);
			if (!lastCard.equalsIgnoreCase("") && !lastSub.equalsIgnoreCase("")
					&& (!lastCard.equals(cardname) || !lastSub.equals(subject))) {
				Log.d("Removing file", lastCard);
				
			}
			replaceFile(lastCard, lastSub, cardname, subject);
			try {
				MainList(cardname, subject);
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
		} else if (id == R.id.delete) {
			DialogFragment clear = new DeleteDialog();
			FragmentManager fm = getSupportFragmentManager();
			clear.show(fm, "");
		}

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
			view.setId(num);
			ll.addView(view);
			sections.add(new Save(0));
			break;
		}
	}
	
	public void close(){
		this.finish();
	}

	public static class PlaceholderFragment extends Fragment {

		public View rootView;
		public LinearLayout ll;

		public PlaceholderFragment() {
		}

		public void getCard() {
			// Use this to get the card that we are editing. We need to have
			// the subject and filename. We need to go to the subject's folder,
			// retrieve the file, and then load the data to the Edit view.
		}

		public void addold(int type, Context c) {
			
			LayoutInflater inflater = (LayoutInflater) c.getSystemService(
							Context.LAYOUT_INFLATER_SERVICE);
			if (inflater == null) {
				Log.d("Inflater", "Failed");
			}
			num++;
			switch (type) {
			case 1:
			case 2:
			case 3:
				View view = inflater.inflate(R.layout.textsection, null);
				if (view == null) {
					Log.d("Fail", "No inflate");
				}
				view.setId(num);
				ll.addView(view);
				sections.add(new Save(0));
				break;
			}
		}

		public void LoadCard(String card, String sub, Context c) {
			File file = new File(storage + File.separator + Folder
					+ File.separator + sub + File.separator + card);
			Log.d("File", file.getPath());
			((EditText) rootView.findViewById(R.id.cardname)).setText(card);
			((EditText) rootView.findViewById(R.id.subjectname)).setText(sub);
			if (file.exists()) {
				try {
					String line = null;
					FileInputStream fin = new FileInputStream(file);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(fin));
					Pattern pattern = Pattern
							.compile("<([a-zA-Z]+)[0-9]+>(.+)<(.+)[0-9]+>");
					Matcher match;
					while ((line = reader.readLine()) != null) {
						match = pattern.matcher(line);
						if (match.find()) {
							Log.d("File", "Found type perhaps");
							if (match.group(1).equals("Text")) {
								String title = match.group(2).toString();
								Log.d("File", title);
								line = reader.readLine();
								match = pattern.matcher(line);
								if (match.find()) {
									if (match.group(1).equals("Info")) {
										String information = match.group(2)
												.toString();
										Log.d("File", information);
										addold(3, c);
										Log.d("File", "Out of add");
										EditText name = (EditText)(((LinearLayout)ll.findViewById(num)).findViewById(R.id.sectionname));
										EditText inf = (EditText)(((LinearLayout)ll.findViewById(num)).findViewById(R.id.details));
										if(name == null){
											Log.d("NULL", "IN THE NAME");
										}
										if(inf == null){
											Log.d("NULL", "IN THE INFO");
										}
										name.setText(title);
										inf.setText(information);
									}
								}
							}else if (match.group(1).equals("Picture")) {

							}else if (match.group(1).equals("Date")) {

							}
						}
					}
					reader.close();
				} catch (IOException e) {
					
				}

			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.edit, container, false);
			getCard();
			String file = getActivity().getIntent().getStringExtra("key");
			sections = new ArrayList<Save>();
			num = 0;
			Log.d("initial", "" + sections.size());
			final Pattern pattern = Pattern.compile("(.+)/(.+)");
			Matcher match = pattern.matcher(file);
			if (match.find()) {
				lastCard = match.group(1).toString();
				lastSub = match.group(2).toString();
			}
			ll = (LinearLayout) rootView.findViewById(R.id.add);
			
			LoadCard(lastCard, lastSub, inflater.getContext());
			Log.d("Number of fields", "" + sections.size());
			return rootView;
		}
	}
}
