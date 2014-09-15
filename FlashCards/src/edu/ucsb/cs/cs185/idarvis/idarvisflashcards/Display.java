package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Display extends ActionBarActivity {

	public static String Folder, storage, mainlist;
	public static String card = null;
	public static String subject = null;
	public static int num = 0;
	public static LinearLayout back;
	public static RelativeLayout front;
	public boolean frontside = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		getMenuInflater().inflate(R.menu.display, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.flip) {
			if(frontside){
				back.setVisibility(View.VISIBLE);
				front.setVisibility(View.INVISIBLE);
				frontside = false;
			}else{
				front.setVisibility(View.VISIBLE);
				back.setVisibility(View.INVISIBLE);
				frontside = true;
			}
			return true;
		}else if(id == R.id.back){
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {	
		
		public View rootView;
		
		public PlaceholderFragment() { 
		}
		
		public void addold(int type, Context c) {
			Log.d("adding", "addviews");
			LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (inflater == null) {
				Log.d("Inflater", "Failed");
			}
			Log.d("Inflater", "inflater ok");
			num++;
			switch (type) {
			case 1:
			case 2:
			case 3:
				View view = inflater.inflate(R.layout.displaytextsection, null);
				Log.d("Created View", "View");
				if (view == null) {
					Log.d("Fail", "No inflate");
				}
				view.setId(num);
				
				((LinearLayout)rootView.findViewById(R.id.add)).addView(view);
				
				break;
			}
		}

		public void LoadCard(String card, String sub, Context c) {
			File file = new File(storage + File.separator + Folder
					+ File.separator + sub + File.separator + card);
			Log.d("File", file.getPath());
			TextView fronttitle = (TextView)rootView.findViewById(R.id.title);
			fronttitle.setText(card);
			TextView backtitle = (TextView)rootView.findViewById(R.id.backtitle);
			backtitle.setText(card);
			if (file.exists()) {
				try {
					Log.d("File", "EXISTS");
					String line = null;
					FileInputStream fin = new FileInputStream(file);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(fin));
					Pattern pattern = Pattern
							.compile("<([a-zA-Z]+)[0-9]+>(.+)<(.+)[0-9]+>");
					Matcher match;
					Log.d("File", "Found type perhaps");
					while ((line = reader.readLine()) != null) {
						Log.d("File", "File empty?");
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
										TextView name = (TextView) (((LinearLayout) rootView.findViewById(R.id.add).findViewById(num)).findViewById(R.id.sectionname));
										TextView inf = (TextView) (((LinearLayout) rootView.findViewById(R.id.add).findViewById(num)).findViewById(R.id.details));
										if (name == null) {
											Log.d("NULL", "IN THE NAME");
										}
										if (inf == null) {
											Log.d("NULL", "IN THE INFO");
										}
										name.setText(title);
										inf.setText(information);
									}
								}
							} else if (match.group(1).equals("Picture")) {

							} else if (match.group(1).equals("Date")) {

							}
						}
					}

					Log.d("File", "Out of loop");
					reader.close();
				} catch (IOException e) {
					Log.d("File", "READER ERROR");
				}

			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState){
			rootView = inflater.inflate(R.layout.card_view_front, container,
					false);
			num = 0;
			String file = getActivity().getIntent().getStringExtra("key");
			final Pattern pattern = Pattern.compile("(.+)/(.+)");
			Matcher match = pattern.matcher(file);
			if (match.find()) {
				card = match.group(1).toString();
				subject = match.group(2).toString();
			}
			
			LoadCard(card, subject, rootView.getContext());
			front = (RelativeLayout)rootView.findViewById(R.id.front);
			back = (LinearLayout)rootView.findViewById(R.id.back);
			back.setVisibility(View.INVISIBLE);
			return rootView;
		}
	}

}
