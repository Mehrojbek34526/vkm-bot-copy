package uz.pdp.service;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import uz.pdp.model.Music;

import java.io.File;
import java.util.*;


public class MusicService {

    private static MusicService instance = new MusicService();

    public static MusicService getInstance(){
        return instance;
    }

    public MusicService() {
        readMusicFromDisk();
    }

    private final List<Music> musicList = new ArrayList<>();

    public List<Music> getMusicList() {
        return musicList;
    }

    public List<Music> search(String text){

        //Sherali jo'rayev gulbadan -> 2 ta so'z
        String[] strings = text.split(" ");

        if (strings.length == 0){
            return new ArrayList<>();
        }

        return musicList.stream()
                .filter(music -> Arrays.stream(strings)
                        .anyMatch(name -> music.getTitle().toLowerCase().contains(name.toLowerCase()))
                        || Arrays.stream(strings)
                        .anyMatch(name -> music.getArtist().toLowerCase().contains(name.toLowerCase()))
                )
                .toList();

    }

    private void readMusicFromDisk() {
        File directory = new File("music");

        int counter = 1;

        for (File audioFile : Objects.requireNonNull(directory.listFiles())) {
            try {

                // Reading the audio file
                AudioFile file = AudioFileIO.read(audioFile);
                Tag tag = file.getTag();

                // Extracting metadata
                String title = tag.getFirst(org.jaudiotagger.tag.FieldKey.TITLE);
                String artist = tag.getFirst(org.jaudiotagger.tag.FieldKey.ARTIST);
                String album = tag.getFirst(org.jaudiotagger.tag.FieldKey.ALBUM);
                int duration = file.getAudioHeader().getTrackLength(); // duration in seconds
                long length = audioFile.length();

                // Displaying the metadata
//                System.out.println("Title: " + title);
//                System.out.println("Artist: " + artist);
//                System.out.println("Album: " + album);
//                System.out.println("Duration: " + duration + " seconds");
//                System.out.println("audioFile.length() = " + length);

                Music music = new Music(
                        counter++,
                        title,
                        artist,
                        length,
                        duration,
                        audioFile.getPath()
                );

                musicList.add(music);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<Music> getMusicById(Integer musicId) {

        return musicList.stream()
                .filter(music -> music.getId().equals(musicId))
                .findFirst();
    }
}
