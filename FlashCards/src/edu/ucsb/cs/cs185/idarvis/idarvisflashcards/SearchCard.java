package edu.ucsb.cs.cs185.idarvis.idarvisflashcards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ucsb.cs.cs185.idarvis.idarvisflashcards.SubjectDialog.DialogListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

public class SearchCard extends ActionBarActivity implements DialogListener {

	private static ArrayList<Pair> cards;
	public static ArrayList<Pair> subjects;
	public static boolean edit;
	public static ItemAdapter adapter;

	public static class ItemAdapter extends ArrayAdapter<Pair> implements
			Filterable {

		private ArrayList<Pair> objects;
		private ArrayList<Pair> mOriginalValues;
		private ArrayFilter mFilter;

		public ItemAdapter(Context context, int textViewResourceId,
				ArrayList<Pair> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.single_row, null);
			}
			Pair i = objects.get(position);

			if (i != null) {
				TextView name = (TextView) v.findViewById(R.id.Row1);
				TextView subject = (TextView) v.findViewById(R.id.Row2);

				if (name != null) {
					name.setText(i.getCard());
				}
				if (subject != null) {
					subject.setText(i.getSubject());
				}
			}
			return v;
		}

		@Override
		public int getCount() {
			return objects.size();
		}

		@Override
		public Pair getItem(int position) {
			return objects.get(position);
		}

		public Filter getFilter() {
			if (mFilter == null) {
				mFilter = new ArrayFilter();
			}
			return mFilter;
		}

		@SuppressLint("DefaultLocale")
		private class ArrayFilter extends Filter {
			private Object lock = new Object();

			@SuppressLint("DefaultLocale")
			@Override
			protected FilterResults performFiltering(CharSequence prefix) {
				FilterResults results = new FilterResults();
				Log.d("Start Filtering", "START");
				if (mOriginalValues == null) {
					synchronized (lock) {
						mOriginalValues = new ArrayList<Pair>(objects);
					}
				}

				if (prefix == null || prefix.length() == 0) {
					Log.d("Filtering", "prefix == null");
					synchronized (lock) {
						ArrayList<Pair> list = new ArrayList<Pair>(
								mOriginalValues);
						results.values = list;
						results.count = list.size();
					}
				} else {
					Log.d("Filtering", "prefix");
					final String prefixString = prefix.toString().toLowerCase();
					Log.d("Filtering", "prefix == " + prefixString);
					ArrayList<Pair> values = mOriginalValues;
					int count = values.size();
					Log.d("Filtering", "count == " + count);
					ArrayList<Pair> newValues = new ArrayList<Pair>();

					for (int i = 0; i < count; i++) {
						Log.d("Filtering", "Getting Pair");
						Pair item = values.get(i);
						if (item == null) {
							Log.d("Filtering", "Pair is null!");
						}
						Log.d("Filtering", "Pair is "
								+ item.toString().toLowerCase());
						if (item != null
								&& item.toString()
										.toLowerCase()
										.contains(
												prefix.toString().toLowerCase())) {
							newValues.add(item);
						}

					}
					Log.d("Filtering", "Values count == " + results.count);
					results.values = newValues;
					results.count = newValues.size();
				}

				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				if (results.values != null) {
					objects = (ArrayList<Pair>) results.values;
				} else {
					objects = new ArrayList<Pair>();
				}
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		}

	}

	public static class Pair {
		public String card;
		public String subject;

		public Pair(String name, String sub) {
			this.card = name;
			this.subject = sub;
		}

		public String getCard() {
			return this.card;
		}

		public String getSubject() {
			return this.subject;
		}

		public void setCard(String name) {
			this.card = name;
		}

		public void setSubject(String sub) {
			this.subject = sub;
		}

		public String toString() {
			return this.card;
		}

	}
	
	public void defaultchange() {
		final Activity act = this;
		adapter = new ItemAdapter(getBaseContext(),
				android.R.layout.simple_list_item_1, cards);
		AutoCompleteTextView Cards = (AutoCompleteTextView) findViewById(R.id.SearchName);
		ListView list = (ListView) findViewById(R.id.cardlist);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final Pair item = (Pair) parent.getItemAtPosition(position);
				if (edit) {
					// Load card and info associated with card to edit it
					// We need to look up the item in the master list and
					// get the subject of the item as well to pass to the
					// edit card activity in order to access the file and
					// perhaps allow for editing of the filename and
					// subject that it already is.

					// FILL ME IN

					Intent intent = new Intent(act, EditCard.class);
					intent.putExtra("key", item.getCard() + File.separator
							+ item.getSubject());
					startActivityForResult(intent, 1);
				} else {
					Intent intent = new Intent(act, Display.class);
					intent.putExtra("key", item.getCard() + File.separator
							+ item.getSubject());
					startActivity(intent);
				}
				Log.d("ListClick", item.toString());
			}
		});

		Log.d("Debug", "AutoCompleteTextView");
		Cards.setAdapter(adapter);
		Cards.setDropDownHeight(getResources().getDimensionPixelSize(
				R.dimen.titlespace) * 3);
		Cards.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final Pair item = (Pair) parent.getItemAtPosition(position);
				if (edit) {
					// Load card and info associated with card to edit it
					// We need to look up the item in the master list and
					// get the subject of the item as well to pass to the
					// edit card activity in order to access the file and
					// perhaps allow for editing of the filename and
					// subject that it already is.

					// FILL ME IN

					Intent intent = new Intent(act, EditCard.class);
					intent.putExtra("key", item.getCard() + File.separator
							+ item.getSubject());
					startActivityForResult(intent, 1);
				} else {
					Intent intent = new Intent(act, Display.class);
					intent.putExtra("key", item.getCard() + File.separator
							+ item.getSubject());
					startActivity(intent);
				}
				Log.d("ListClick", item.toString());
			}
		});
		Log.d("Debug", "Setting Adapter");
		Cards.setThreshold(1);
		Log.d("Debug", "Threshold");

	}

	public void change(String subject) {
		subjects = new ArrayList<Pair>();

		final Activity act = this;
		for (Pair p : cards) {
			if (p.getSubject().equals(subject))
				subjects.add(p);
		}
		adapter = new ItemAdapter(getBaseContext(),
				android.R.layout.simple_list_item_1, subjects);
		AutoCompleteTextView Cards = (AutoCompleteTextView) findViewById(R.id.SearchName);
		ListView list = (ListView) findViewById(R.id.cardlist);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final Pair item = (Pair) parent.getItemAtPosition(position);
				if (edit) {
					// Load card and info associated with card to edit it
					// We need to look up the item in the master list and
					// get the subject of the item as well to pass to the
					// edit card activity in order to access the file and
					// perhaps allow for editing of the filename and
					// subject that it already is.

					// FILL ME IN

					Intent intent = new Intent(act, EditCard.class);
					intent.putExtra("key", item.getCard() + File.separator
							+ item.getSubject());
					startActivityForResult(intent, 1);
				} else {
					Intent intent = new Intent(act, Display.class);
					intent.putExtra("key", item.getCard() + File.separator
							+ item.getSubject());
					startActivity(intent);
				}
				Log.d("ListClick", item.toString());
			}
		});

		Log.d("Debug", "AutoCompleteTextView");
		Cards.setAdapter(adapter);
		Cards.setDropDownHeight(getResources().getDimensionPixelSize(
				R.dimen.titlespace) * 3);
		Cards.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final Pair item = (Pair) parent.getItemAtPosition(position);
				if (edit) {
					// Load card and info associated with card to edit it
					// We need to look up the item in the master list and
					// get the subject of the item as well to pass to the
					// edit card activity in order to access the file and
					// perhaps allow for editing of the filename and
					// subject that it already is.

					// FILL ME IN

					Intent intent = new Intent(act, EditCard.class);
					intent.putExtra("key", item.getCard() + File.separator
							+ item.getSubject());
					startActivityForResult(intent, 1);
				} else {
					Intent intent = new Intent(act, Display.class);
					intent.putExtra("key", item.getCard() + File.separator
							+ item.getSubject());
					startActivity(intent);
				}
				Log.d("ListClick", item.toString());
			}
		});
		Log.d("Debug", "Setting Adapter");
		Cards.setThreshold(1);
		Log.d("Debug", "Threshold");

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// MAKE NEW MENU FOR THIS PAGE
		getMenuInflater().inflate(R.menu.subject, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.sub) {
			HashSet<String> subjects = new HashSet<String>();
			for (Pair p : cards) {
				subjects.add(p.getSubject());
				Log.d("Set", "" + subjects.size());
			}
			DialogFragment subj = new SubjectDialog();
			FragmentManager fm = getSupportFragmentManager();
			Bundle args = new Bundle();
			args.putStringArray("Array",
					subjects.toArray(new String[subjects.size()]));
			subj.setArguments(args);
			subj.show(fm, "");
		}else if(id == R.id.back){
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public static class PlaceholderFragment extends Fragment {
		private View rootView;

		public PlaceholderFragment() {
		}

		public View getView() {
			return rootView;
		}

		public void addCards() {
			cards = new ArrayList<Pair>();
			File file = new File(Environment.getExternalStorageDirectory()
					.toString()
					+ File.separator
					+ getResources().getString(R.string.namesfolder)
					+ File.separator
					+ getResources().getString(R.string.namelist));
			try {
				FileInputStream fin = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(fin));
				String line;
				while ((line = reader.readLine()) != null) {
					final Pattern pattern = Pattern.compile("<(.+)>(.+)<(.+)>");
					Matcher matcher = pattern.matcher(line);
					if (!matcher.find()) {
						continue;
					}
					Log.d("MatcherString", matcher.group(0));
					Log.d("MatcherString", matcher.group(1));
					Log.d("MatcherString", matcher.group(2));
					if (matcher.group(1).compareTo("Name") != 0) {
						continue;
					} else if (matcher.group(1).compareTo("Name") == 0) {
						String name = matcher.group(2).toString();
						Log.d("Name", name);
						line = reader.readLine();
						matcher = pattern.matcher(line);
						if (!matcher.find()) {

							continue;
						}
						if (matcher.group(1).compareTo("Subject") != 0) {
							continue;
						}
						String sub = matcher.group(2).toString();
						Log.d("Name", sub);
						Pair pair = new Pair(name, sub);
						cards.add(pair);
						String finale = name + "\t\t" + sub;
						Log.d("Name", finale);
					}
				}
				reader.close();
				fin.close();
			} catch (IOException e) {
				Log.d("File",
						Environment.getExternalStorageDirectory().toString()
								+ File.separator
								+ getResources()
										.getString(R.string.namesfolder)
								+ File.separator
								+ getResources().getString(R.string.namelist));
			}

		}

		public ItemAdapter addList() {
			final ListView list = (ListView) rootView
					.findViewById(R.id.cardlist);
			adapter = new ItemAdapter(getActivity().getBaseContext(),
					android.R.layout.simple_list_item_1, cards);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					final Pair item = (Pair) parent.getItemAtPosition(position);
					if (edit) {
						// Load card and info associated with card to edit it
						// We need to look up the item in the master list and
						// get the subject of the item as well to pass to the
						// edit card activity in order to access the file and
						// perhaps allow for editing of the filename and
						// subject that it already is.

						// FILL ME IN

						Intent intent = new Intent(getActivity(),
								EditCard.class);
						intent.putExtra("key", item.getCard() + File.separator
								+ item.getSubject());
						startActivityForResult(intent, 1);
					} else {
						Intent intent = new Intent(getActivity(), Display.class);
						intent.putExtra("key", item.getCard() + File.separator
								+ item.getSubject());
						startActivity(intent);
					}
					Log.d("ListClick", item.toString());
				}
			});

			AutoCompleteTextView Cards = (AutoCompleteTextView) rootView
					.findViewById(R.id.SearchName);
			Log.d("Debug", "AutoCompleteTextView");
			Cards.setAdapter(adapter);
			Cards.setDropDownHeight(getResources().getDimensionPixelSize(
					R.dimen.titlespace) * 3);
			Cards.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					final Pair item = (Pair) parent.getItemAtPosition(position);
					if (edit) {
						// Load card and info associated with card to edit it
						// We need to look up the item in the master list and
						// get the subject of the item as well to pass to the
						// edit card activity in order to access the file and
						// perhaps allow for editing of the filename and
						// subject that it already is.

						// FILL ME IN

						Intent intent = new Intent(getActivity(),
								EditCard.class);
						intent.putExtra("key", item.getCard() + File.separator
								+ item.getSubject());
						startActivityForResult(intent, 1);
					} else {
						Intent intent = new Intent(getActivity(), Display.class);
						intent.putExtra("key", item.getCard() + File.separator
								+ item.getSubject());
						startActivity(intent);
					}
					Log.d("ListClick", item.toString());
				}
			});
			Log.d("Debug", "Setting Adapter");
			Cards.setThreshold(1);
			Log.d("Debug", "Threshold");

			return adapter;

		}

		public void onActivityResult(int requestCode, int resultCode,
				Intent extras) {
			Log.d("REstart", "Restart");
			if (requestCode == 1) {

				Intent intent = getActivity().getIntent();
				if (edit) {
					intent.putExtra("key", 1);
				} else {
					intent.putExtra("key", 0);
				}

				getActivity().finish();
				startActivity(intent);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.search, container, false);
			Intent intent = getActivity().getIntent();
			if (intent.getIntExtra("key", 0) == 1) {
				edit = true;
			} else {
				edit = false;
			}
			// AutoComplete //
			Log.d("Debug", "Start");
			addCards();
			// Map<String, String> treeMap = new TreeMap<String, String>(cards);
			Collections.sort(cards, new Comparator<Pair>() {
				@Override
				public int compare(Pair one, Pair two) {
					return one.getCard().compareTo(two.getCard());
				}
			});
			adapter = addList();
			Log.d("Debug", Integer.toString(cards.size()));
			Log.d("Debug", "Strings");
			// ItemAdapter adapter1 = new
			// ItemAdapter(getActivity().getBaseContext(),android.R.layout.simple_list_item_1,
			// cards);
			// Log.d("Debug", "Adapter");

			// ListView //

			return rootView;
		}
	}
}
