package com.westernsydneyunipx.localdata;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM audiomodel")
    List<AudioModel> getAll();

    @Insert
    void insert(AudioModel task);

    @Delete
    void delete(AudioModel task);

    @Update
    void update(AudioModel task);

    @Query("DELETE FROM audiomodel")
     void deleteAudio();



    @Query("SELECT * FROM videomodel")
    List<VideoModel> getAllVideo();

    @Insert
    void insertvideo(VideoModel task);


    @Query("DELETE FROM videomodel")
    void deleteVideo();




}
