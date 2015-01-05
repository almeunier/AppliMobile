package com.example.twittersearchactivity;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
		TextView tweetDisplay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tweetDisplay = (TextView) findViewById(R.id.tweet_txt);
	}			
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	public void searchTwitter(View view){
		EditText searchTxt = (EditText)findViewById(R.id.search_edit);
		String searchTerm = searchTxt.getText().toString();
		if(searchTerm.length()>0){
			try{
				String encodedSearch = URLEncoder.encode(searchTerm, "UTF-8");
				String searchURL = "http://search.twitter.com/search.json?q="+encodedSearch;
				new GetTweets().execute(searchURL);
			}
			catch(Exception e){ 
				tweetDisplay.setText("Erreur");
				e.printStackTrace(); 
			}
		}
		else {
		    tweetDisplay.setText("Enter a search!");
		}
	}
	
	private class GetTweets extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... twitter URL) {
			StringBuilder tweetFeedBuilder = new StringBuilder(); 
			for (String searchURL : twitterURL) {
				try {
					HttpClient tweetClient = new DefaultHttpClient();
					HttpGet tweetGet = new HttpGet(searchURL);
					HttpResponse tweetResponse = tweetClient.execute(tweetGet);
					StatusLine searchStatus = tweetResponse.getStatusLine();
						if (searchStatus.getStatusCode() == 200) {
							HttpEntity tweetEntity = tweetResponse.getEntity();
							InputStream tweetContent = tweetEntity.getContent();
							InputStreamReader tweetInput = new InputStreamReader(tweetContent);
							BufferedReader tweetReader = new BufferedReader(tweetInput);
							String lineIn;
							while ((lineIn = tweetReader.readLine()) != null) {
							    tweetFeedBuilder.append(lineIn);
							}
						}
						else{
						    tweetDisplay.setText("Erreur");
						}
				}

				catch(Exception e) { 
				    tweetDisplay.setText("Erreur");
				    e.printStackTrace(); 
				}
				
				return tweetFeedBuilder.toString();
				
			}
		}
		
		protected void onPostExecute(String result) {
			StringBuilder tweetResultBuilder = new StringBuilder();
			try {
				JSONObject resultObject = new JSONObject(result);
				JSONArray tweetArray = resultObject.getJSONArray("results");
				for (int t=0; t<tweetArray.length(); t++) {
					JSONObject tweetObject = tweetArray.getJSONObject(t);
					tweetResultBuilder.append(tweetObject.getString("from_user")+": ");
					tweetResultBuilder.append(tweetObject.get("text")+"\n\n");
				}
			}
			catch (Exception e) {
			    tweetDisplay.setText("Erreur");
			    e.printStackTrace();
			}
			if(tweetResultBuilder.length()>0){
			    tweetDisplay.setText(tweetResultBuilder.toString());
			}
			else{
			    tweetDisplay.setText("Sorry - no tweets found for your search!");
			}
		}
	}
}
