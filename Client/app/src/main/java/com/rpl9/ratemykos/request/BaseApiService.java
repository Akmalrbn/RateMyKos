package com.rpl9.ratemykos.request;

import com.rpl9.ratemykos.model.Account;
import com.rpl9.ratemykos.model.Comment;
import com.rpl9.ratemykos.model.Facility;
import com.rpl9.ratemykos.model.Kos;
import com.rpl9.ratemykos.model.Rating;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BaseApiService {

//    @FormUrlEncoded
//    @POST("/login")
//    Call<Account> login(
//            @Field("identifier") String identifier,
//            @Field("password") String password
//    );

    @FormUrlEncoded
    @POST("login")
    Call<Account> login(
            @Field("identifier") String identifier,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("register")
    Call<Account> register(
            @Field("email") String email,
            @Field("username") String username,
            @Field("password") String password
    );
//    @FormUrlEncoded
    @GET("getallkos")
    Call<List<Kos>> getall();

    @FormUrlEncoded
    @POST("addcomment")
    Call<String> addcomment(
            @Field("kos_id") int kos_id,
            @Field("user_id") int user_id,
            @Field("comment") String comment
    );


    @GET("getcomment/{kos_id}")
    Call<List<Comment>> getcomment(
            @Path("kos_id") int kos_id
    );
    @GET("getkos/{kos_id}")
    Call<Kos> getkos(
            @Path("kos_id") int kos_id
    );

    @GET("getreply/{comment_id}")
    Call<List<Comment>> getreply(
            @Path("comment_id") int comment_id
    );

    @GET("getuserrating/{kos_id}/{user_id}")
    Call<Rating> getuserrating(
            @Path("kos_id") int kos_id,
            @Path("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("addkosnofacilities")
    Call<Kos> addkosnofacilities(
            @Field("name") String name,
            @Field("location") String location,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("description") String description
    );

    @FormUrlEncoded
    @POST("addkos")
    Call<Kos> addkos(
            @Field("name") String name,
            @Field("location") String location,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("description") String description,
            @Field("facilities") ArrayList<Facility> facilities,
            @Field("kos_type") String kos_type
    );
    @FormUrlEncoded
    @POST("addrating")
    Call<Rating> addrating(
            @Field("kos_id") int kos_id,
            @Field("user_id") int user_id,
            @Field("rating") float rating
    );



//    @GET("getallkos")
//    Call<List<Kos>> getAllKos(
//          @Query("page") int page,
//          @Query("pageSize") int pageSize
//    );

}

//    @GET("account/{id}")
//    Call<Account> getAccount (@Path("id") int id);
//
//    @POST ("account/login")
//    Call<Account> login (@Query("email") String email, @Query("password") String password);
//
//    @POST ("account/register")
//    Call<Account> register
//            (
//                    @Query("name") String name,
//                    @Query("email") String email,
//                    @Query("password") String password
//            );
//    @POST("/account/{id}/topUp")
//    Call<Account> topUp (@Path("id") int id, @Query("balance") double balance);
//
//    @GET("renter/{id}")
//    Call<Renter> getRenter (@Path("id") int id);
//
//    @POST("account/{id}/registerRenter")
//    Call<Renter> registerRenter
//            (
//                    @Path("id") int id,
//                    @Query("username") String username,
//                    @Query("address") String address,
//                    @Query("phoneNumber") String phoneNumber
//            );
//
//    @GET("room/{id}")
//    Call<Room> getRoom (@Path("id") int id);
//
//    @POST("room/create")
//    Call<Room> createRoom
//            (
//                    @Query("accountId") int accountId,
//                    @Query("name") String name,
//                    @Query("size") int size,
//                    @Query("price") int price,
//                    @Query("facility") ArrayList<Facility> facility,
//                    @Query("city") City city,
//                    @Query("address") String address,
//                    @Query("bedType")BedType bedType
//            );
//    @GET("room/getAllRoom")
//    Call<List<Room>> getAllRoom
//            (
//                  @Query("page") int page,
//                  @Query("pageSize") int pageSize
//            );
//
//    @GET("room/getAll")
//    Call<List<Room>> getAll();
//
//    @GET("room/getMyRoom")
//    Call<List<Room>> getMyRoom
//            (
//                    @Query("page") int page,
//                    @Query("pageSize") int pageSize,
//                    @Query("id") int id
//
//            );
//
//    @GET("price/{id}")
//    Call<Price> getPrice (@Path("id") int id);
//
//    @POST("payment/create")
//    Call<Payment> createPayment
//            (
//                    @Query("buyerId") int buyerId,
//                    @Query("renterId") int renterId,
//                    @Query("roomId") int roomId,
//                    @Query("from") String from,
//                    @Query("to") String to
//            );
//
//    @POST("payment/{id}/accept")
//    Call<Boolean> accept (@Path("id") int id);
//
//    @POST("payment/{id}/cancel")
//    Call<Boolean> cancel (@Path("id") int id);
//
//    @GET("payment/paymentlist")
//    Call<List<Payment>> getMyPayment
//            (
//                    @Query("roomId") int roomId,
//                    @Query("page") int page,
//                    @Query("pageSize") int pageSize
//            );
//
//
//
//}
