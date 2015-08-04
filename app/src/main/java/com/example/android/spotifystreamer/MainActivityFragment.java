package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.android.spotifystreamer.Utils.*;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ImageAndTextListAdapter mArtistAdapter;
    private String artistName = "";
    private List<Artist> artistListCache = new ArrayList<Artist>();
    private final SpotifyApi spotifyApi = new SpotifyApi();
    private final SpotifyService spotify = spotifyApi.getService();

    public MainActivityFragment() {
    }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Check whether we're recreating a previously destroyed instance
            if (savedInstanceState != null) {
                // Restore value of members from saved state (during screen orientation changes)
                artistName = savedInstanceState.getString("artistName");
                String artistListCacheJson = savedInstanceState.getString("artistListCache");
                Gson gson = new Gson();
                artistListCache = gson.fromJson(artistListCacheJson, new TypeToken<List<Artist>>(){}.getType());
            } else {
                // Probably initialize members with default values for a new instance
            }
            mArtistAdapter = new ImageAndTextListAdapter (
                    getActivity(), // current context
                    (ArrayList<Artist>)artistListCache);
        }
        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            // Save the user's current state
            savedInstanceState.putString("artistName", artistName);
            Gson gson = new Gson();
            String artistListCacheJson = gson.toJson(artistListCache, new TypeToken<List<Artist>>(){}.getType());
            savedInstanceState.putString("artistListCache", artistListCacheJson);
            // Always call the superclass so it can save the view hierarchy state
            super.onSaveInstanceState(savedInstanceState);
        }

        private class updateArtistList extends AsyncTask<String, Void, List<Artist>> {
            private final String LOG_TAG = updateArtistList.class.getSimpleName();
            @Override
            protected List<Artist> doInBackground(String... params) {
                try {
                    artistListCache = spotify.searchArtists(params[0]).artists.items;
                    return artistListCache;
                } catch(final RetrofitError e) {
                    Log.e(LOG_TAG, "Error from spotify api access", e);
                }
                return null;
            }
            @Override
            protected void onPostExecute(List<Artist> artistSearchResult) {
                mArtistAdapter.clear();
                for (Artist artist: artistSearchResult) {
                    mArtistAdapter.add(artist);
                }
                // when the artist is not found
                if (artistSearchResult.size() == 0) {
                    UIUtils.ArtistNotFoundAlert(getActivity().findViewById(R.id.listview_artists));
                }
            }

        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_artist_search, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);
            listView.setAdapter(mArtistAdapter);
            EditText searchArtist = (EditText) rootView.findViewById(R.id.searchArtist);
            searchArtist.setText(artistName);
            searchArtist.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if(s.length() != 0) {
                        if (MiscUtils.isNetworkAvailable(getActivity().getApplicationContext()))
                            new updateArtistList().execute(new String[]{s.toString()});
                        else
                            UIUtils.InternetAccessibilityAlert(getActivity().findViewById(R.id.listview_artists));
                    }
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (MiscUtils.isNetworkAvailable(getActivity().getApplicationContext())){
                        Artist artist = mArtistAdapter.getItem(position);
                        Intent tracksIntent = new Intent(getActivity(), TopTracksActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, artist.id);
                        tracksIntent.putExtra("ArtistName", artist.name);
                        startActivity(tracksIntent);
                    } else
                        UIUtils.InternetAccessibilityAlert(getActivity().findViewById(R.id.listview_artists));
                }
            });
        return rootView;

    }

    public class ImageAndTextListAdapter extends ArrayAdapter<Artist> {
        public ImageAndTextListAdapter(Context context, ArrayList<Artist> artistList) {
            super(context, 0, artistList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Artist artist = getItem(position);
            // A holder will hold the references
            // to your views.
            ViewHolder holder;
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artists, parent, false);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.list_item_artists_textview);
                holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_artists_artistImageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // default image if there is no artist image
            String imageUrl = "https://lh4.ggpht.com/NjSeU8ya6h8cNL6JntWZqhlkmAHKcy0vJmxDBqF0x_y4izs6skpxg6a4TRsf3Jza7kk=w300";
            if (artist.images.size() > 0) {
                imageUrl = artist.images.get(0).url;
            }
            Glide.with(getActivity()).load(imageUrl).into(holder.imageView);
            holder.textView.setText(artist.name);
            // Return the completed view to render on screen
            return convertView;
        }

    }
    class ViewHolder {
        // declare your views here
        TextView textView;
        ImageView imageView;
    }
}
