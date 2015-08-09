package com.example.android.spotifystreamer;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.spotifystreamer.Utils.EllipsizedTextView;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackActivityFragment extends Fragment {

    private final SpotifyApi spotifyApi = new SpotifyApi();
    private final SpotifyService spotify = spotifyApi.getService();
    private String trackId = "";
    private String artistName = "";

    public TrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            trackId = intent.getStringExtra(Intent.EXTRA_TEXT);
            artistName = intent.getStringExtra("ArtistName");
            // call async task to load the track
            new loadTrack().execute(new String[]{trackId});
        }
        return rootView;
    }

    private class loadTrack extends AsyncTask<String, Void, Track> {
        private final String LOG_TAG = loadTrack.class.getSimpleName();
        @Override
        protected Track doInBackground(String... params) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("country", "US"); // country is required, so pass US as its parameter
                return spotify.getTrack(params[0], map);
            } catch(final RetrofitError e) {
                Log.e(LOG_TAG, "Error from spotify api access", e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Track track) {
            AlbumSimple album = track.album;
            /*
            player UI should display the following information:
                artist name
                album name
                album artwork
                track name
                track duration
            */
            String preview_url = track.preview_url;
            String trackName = track.name;
            String albumArtWorkUrl = album.images.get(0).url;
            String albumName = album.name;
            ((TrackActivity) getActivity()).getSupportActionBar().setTitle(trackName);
            TextView trackNameTextView = (TextView)  getActivity().findViewById(R.id.track_textview);
            TextView albumNameTextView = (TextView)  getActivity().findViewById(R.id.album_textview);
            EllipsizedTextView artistNameTextView = (EllipsizedTextView)  getActivity().findViewById(R.id.artist_textview);
            ImageView albumArtView = (ImageView) getActivity().findViewById(R.id.albumart_imageView);
            trackNameTextView.setText(trackName);
            albumNameTextView.setText(albumName);
            artistNameTextView.setText(artistName);
            Glide
                    .with(getActivity())
                    .load(albumArtWorkUrl)
                    .listener(GlidePalette.with(albumArtWorkUrl)

                                    .use(GlidePalette.Profile.VIBRANT)
                                    .intoBackground(getView(), GlidePalette.Swatch.RGB)
                                    .intoTextColor(trackNameTextView, GlidePalette.Swatch.BODY_TEXT_COLOR)
                                    .intoTextColor(albumNameTextView, GlidePalette.Swatch.BODY_TEXT_COLOR)
                                    .intoTextColor(artistNameTextView, GlidePalette.Swatch.BODY_TEXT_COLOR)
                    )
                    .into(albumArtView);
        }

    }

}
