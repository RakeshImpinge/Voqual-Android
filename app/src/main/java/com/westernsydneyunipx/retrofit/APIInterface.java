package com.westernsydneyunipx.retrofit;

import com.westernsydneyunipx.localdata.AudioModel;
import com.westernsydneyunipx.model.DeletePostResponse;
import com.westernsydneyunipx.model.ForgotPassword;
import com.westernsydneyunipx.model.MediaData;
import com.westernsydneyunipx.model.Participant;
import com.westernsydneyunipx.model.User;
import com.westernsydneyunipx.retrofit.response.ListResponse;
import com.westernsydneyunipx.retrofit.response.RestResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * @author PA1810.
 */


// for api calling stuff

public interface APIInterface {

    @GET("researcher_list")
    Call<ListResponse<User>> researcherList();

    @POST("signup")
    Call<RestResponse<User>> signup(@Body HashMap<String, Object> hashMap);

    @Multipart
    @POST("signup")
    Call<RestResponse<User>> signupp(@PartMap HashMap<String, RequestBody> map, @Part MultipartBody.Part part);

    @POST("login")
    Call<RestResponse<User>> login(@Body HashMap<String, Object> hashMap);

    @POST("save_profile")
    Call<RestResponse<User>> saveProfile(@Body HashMap<String, Object> hashMap);


    @Multipart
    @POST("save_profile")
    Call<RestResponse<User>> saveProfilee(@PartMap HashMap<String, RequestBody> hashMap, @Part MultipartBody.Part part);

    @GET("get_researcher_info")
    Call<RestResponse<User>> getResearcherInfo(@Query("user_id") int user_id);

    @POST("change_password")
    Call<RestResponse> changePassword(@Body HashMap<String, Object> hashMap);


    @POST("save_consent")
    Call<RestResponse> saveConsent(@Body HashMap<String, Object> hashMap);

    @GET("audio_list")
    Call<ListResponse<MediaData>> audioList(@Query("user_id") int user_id);

    @GET("video_list")
    Call<ListResponse<MediaData>> videoList(@Query("user_id") int user_id);

    @GET("participant_list")
    Call<ListResponse<Participant>> participantList(@Query("user_id") int user_id);

    @GET("delete_media")
    Call<RestResponse> deleteMedia(@Query("id") int id);

    @POST("delete_postlist")
    Call<DeletePostResponse> deletePost(@Query("id") String id);


    @Multipart
    @POST("upload_media")
    Call<RestResponse<MediaData>> uploadMedia(@Query("user_id") int user_id,
                                              @Query("media_type") int mediaType,
                                              @Query("title") String title,
                                              @Part MultipartBody.Part image);


    @Multipart
    @POST("upload_multimedia")
    Call<RestResponse<MediaData>> uploadMediaoffline(@Query("user_id") int user_id,
                                                     @Query("media_type") int mediaType,
                                                     @Query("title") JSONArray name,
                                                     @Part ArrayList<MultipartBody.Part> image);




    @GET("forgotPassword")
    Call<ForgotPassword> forgotPass(@Query("email") String email);

}