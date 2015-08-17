package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.spotifystreamer.Utils.EllipsizedTextView;
import com.example.android.spotifystreamer.Utils.MiscUtils;
import com.example.android.spotifystreamer.Utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

import static android.widget.AdapterView.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private ImageAndTwoTextListAdapter mTracksAdapter;
    private final SpotifyApi spotifyApi = new SpotifyApi();
    private List<Track> trackListCache = new ArrayList<Track>();
    private String artistId = "";
    private String artistName = "";
    private final SpotifyService spotify = spotifyApi.getService();

    public TopTracksActivityFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state (during screen orientation changes)
            artistId = savedInstanceState.getString("artistId");
            String trackListCacheJson = savedInstanceState.getString("trackListCache");
            Gson gson = new Gson();
            trackListCache = gson.fromJson(trackListCacheJson, new TypeToken<List<Track>>(){}.getType());
        } else {
            // Probably initialize members with default values for a new instance
        }
        mTracksAdapter = new ImageAndTwoTextListAdapter (
                getActivity(), // current context
                (ArrayList<Track>)trackListCache);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putString("artistId", artistId);
        Gson gson = new Gson();
        String trackListCacheJson = gson.toJson(trackListCache, new TypeToken<List<Track>>(){}.getType());
        savedInstanceState.putString("trackListCache", trackListCacheJson);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        Intent intent = getActivity().getIntent();

        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTracksAdapter);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
            artistName = intent.getStringExtra("ArtistName");
            new updateTrackList().execute(new String[]{artistId});
        }
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MiscUtils.isNetworkAvailable(getActivity().getApplicationContext())){
                    Track track = mTracksAdapter.getItem(position);
                    Intent playTrackIntent = new Intent(getActivity(), TrackActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, track.id);
                    playTrackIntent.putExtra("ArtistName", artistName);
                    startActivity(playTrackIntent);
                } else
                    UIUtils.InternetAccessibilityAlert(getActivity().findViewById(R.id.listview_artists));
            }
        });
        return rootView;
    }

    private class updateTrackList extends AsyncTask<String, Void, List<Track>> {
        private final String LOG_TAG = updateTrackList.class.getSimpleName();
        @Override
        protected List<Track> doInBackground(String... params) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("country", "US"); // country is required, so pass US as its parameter
                return spotify.getArtistTopTrack(params[0], map).tracks;
            } catch(final RetrofitError e) {
                Log.e(LOG_TAG, "Error from spotify api access", e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<Track> topTracksResult) {
            mTracksAdapter.clear();
            for (Track track: topTracksResult) {
                mTracksAdapter.add(track);
            }
        }

    }
    private class ImageAndTwoTextListAdapter extends ArrayAdapter<Track> {
        public ImageAndTwoTextListAdapter(Context context, ArrayList<Track> trackList) {
            super(context, 0, trackList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Track track = getItem(position);
            ViewHolder holder;
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_tracks, parent, false);
                holder = new ViewHolder();
                holder.trackTextView = (TextView) convertView.findViewById(R.id.list_item_tracks_track_textview);
                holder.trackAlbumView = (EllipsizedTextView) convertView.findViewById(R.id.list_item_tracks_album_textview);
                holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_tracks_trackImageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // default image if there is no track image
            String imageUrl = "https://lh4.ggpht.com/NjSeU8ya6h8cNL6JntWZqhlkmAHKcy0vJmxDBqF0x_y4izs6skpxg6a4TRsf3Jza7kk=w300";
            if (track.album.images.size() > 0) {
                imageUrl = track.album.images.get(0).url;
            }
            Glide.with(getActivity()).load(imageUrl).into(holder.imageView);
            holder.trackTextView.setText(track.name);
            holder.trackAlbumView.setText(track.album.name);
            // Return the completed view to render on screen
            return convertView;
        }

    }
    class ViewHolder {
        TextView trackTextView;
        EllipsizedTextView trackAlbumView;
        ImageView imageView;
    }
}
