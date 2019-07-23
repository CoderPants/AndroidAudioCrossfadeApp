package donteco.testapp.audiohelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class Audio  implements Serializable {

    private String title;
    private String artist;
    private String data;//?
    private String album;
    private Uri uri;


    public Audio() {
        title = "";
        artist = "";
        data = "";
        album = "";
        uri = null;
    }

    public Audio(String title, String artist, String data, String album, Uri uri) {
        this.title = title;
        this.artist = artist;
        this.data = data;
        this.album = album;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Bitmap getAlbumImage()
    {
        MediaMetadataRetriever mmr;
        Bitmap bm;
        try{
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(new File(data).getPath());
            byte [] byteArrayForImg = mmr.getEmbeddedPicture();
            bm = BitmapFactory.decodeByteArray(byteArrayForImg, 0, byteArrayForImg.length);
            return bm;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Audio:" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", data='" + data + '\'' +
                ", album='" + album + '\'' +
                ' ';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audio audio = (Audio) o;
        return Objects.equals(title, audio.title) &&
                Objects.equals(artist, audio.artist) &&
                Objects.equals(data, audio.data) &&
                Objects.equals(album, audio.album);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist, data, album);
    }
}
