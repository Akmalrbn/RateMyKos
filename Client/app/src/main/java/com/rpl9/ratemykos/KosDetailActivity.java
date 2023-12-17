package com.rpl9.ratemykos;


import static com.rpl9.ratemykos.HomeActivity.arrayOfKos;
import static com.rpl9.ratemykos.HomeActivity.kosView;
import static com.rpl9.ratemykos.LoginActivity.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rpl9.ratemykos.model.Comment;
import com.rpl9.ratemykos.model.Facility;
import com.rpl9.ratemykos.model.Kos;
import com.rpl9.ratemykos.model.Rating;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class KosDetailActivity extends AppCompatActivity {

    BaseApiService mApiService;
    Context mContext;
    ForumAdapter adapter;
    public static List<Comment> commentList = new ArrayList<Comment>();
    public static List<Comment> organizedComments = new ArrayList<Comment>();
    Rating kosRating, userRating;
    Kos updatedKos;
    TextView kosRatingText, sendText;
    EditText addComment;
    RatingBar SetRatingBar;
    float rate;
    String comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kos_detail);
        mApiService = UtilsApi.getApiService();
        mContext = this;
        Log.d("Listkos", "Masuk" + kosView.name);
        getcomment();
        TextView name = findViewById(R.id.detailKosTitle);
//        TextView size = findViewById(R.id.detailSizeText);
//        TextView price = findViewById(R.id.detailPriceText);
        TextView address = findViewById(R.id.detailLocationText);
        Button setRating = findViewById(R.id.detailButtonRating);
        kosRatingText = findViewById(R.id.detailRatingText);
        SetRatingBar = findViewById(R.id.detailSetRatingRatingBar);
        CheckBox WiFi = findViewById(R.id.Wifi);
        CheckBox AC = findViewById(R.id.AC);
        CheckBox Bathroom = findViewById(R.id.Bathroom);
        CheckBox Refrigerator = findViewById(R.id.Refrigerator);
        CheckBox Kitchen = findViewById(R.id.Kitchen);
        TextView KosType = findViewById(R.id.detailType);
        addComment = findViewById(R.id.forumEditText);
        sendText = findViewById(R.id.sendButton);

        name.setText(kosView.name);
        address.setText(kosView.location);
        KosType.setText(kosView.kos_type.toString());
        if (kosView.average_rating != 0){
            kosRatingText.setText(String.valueOf(kosView.average_rating));
        }
        if (account != null){
            getuserrating();
        }

        if (kosView.facilities != null) {
            if (kosView.facilities.contains(Facility.WiFi)) {
                WiFi.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.AC)) {
                AC.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.Bathroom)) {
                Bathroom.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.AC)) {
                AC.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.Kitchen)) {
                Kitchen.setChecked(true);
            }
            if (kosView.facilities.contains(Facility.Refrigerator)) {
                Refrigerator.setChecked(true);
            }
        }
        Button payment = findViewById(R.id.bookButton);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent move = new Intent(KosDetailActivity.this, BookActivity.class);
                startActivity(move);
            }
        });
        setRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account == null) {
                    Toast.makeText(mContext, "Harus Login Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                    Intent move = new Intent(KosDetailActivity.this, LoginActivity.class);
                    startActivity(move);
                } else {
                    // Get the rating from the RatingBar
                    rate = SetRatingBar.getRating();
                    Log.d("SET RATING", String.valueOf(rate));
                    addrating();
                }
            }
        });
        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account == null) {
                    Toast.makeText(mContext, "Harus Login Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                    Intent move = new Intent(KosDetailActivity.this, LoginActivity.class);
                    startActivity(move);
                } else {
                    // Get the rating from the RatingBar
                    comment = addComment.getText().toString();
                    addcomment();
                    getcomment();
                    recreate();
                }
            }
        });
    }
    protected void getcomment() {
        mApiService.getcomment(kosView.kos_id).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    commentList.clear();
                    commentList.addAll(response.body());
                    // Organize comments into a hierarchical structure
                    organizedComments = organizeComments(commentList);

                    // Update the adapter with the organized comments
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new ForumAdapter(organizedComments);
                            RecyclerView recyclerView = findViewById(R.id.commentRecyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });

                    Log.d("HASILKOMEN", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(mContext, "salah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Comment> organizeComments(List<Comment> comments) {
        List<Comment> organizedComments = new ArrayList<>();
        // Map to quickly find a comment based on its ID
        Map<Integer, Comment> commentMap = new HashMap<>();
        for (Comment comment : comments) {
            commentMap.put(comment.comment_id, comment);
        }
        // Build the hierarchy
        for (Comment comment : comments) {
            if (comment.reply_id != 0) {
                Comment parent = commentMap.get(comment.reply_id);
                if (parent != null) {
                    // Add the current comment as a reply to its parent
                    if (parent.replies == null) {
                        parent.replies = new ArrayList<>();
                    }
                    parent.replies.add(comment);
                }
            } else {
                // Comment with reply_id == 0 is a top-level comment
                organizedComments.add(comment);
            }
        }
        return organizedComments;
    }


    protected int getuserrating() {
        mApiService.getuserrating(kosView.kos_id, account.getID()).enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                if (response.isSuccessful()) {
                    userRating = response.body();
                    if (userRating.rating != 0){
                        SetRatingBar.setRating((float) userRating.rating);
                    }
                    else {
                        SetRatingBar.setRating(0);
                    }
                }
            }
            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                Toast.makeText(mContext, "salah", Toast.LENGTH_SHORT).show();
            }
        });
        return 0;
    }
    protected int addrating() {
        mApiService.addrating(kosView.kos_id, account.getID(),rate).enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(mContext, "Rating Set", Toast.LENGTH_SHORT).show();
                    Log.d("Rating Set", "Acc ID: " + account.getID() + " Kos ID: "+ kosView.kos_id + "rate ID: "+ rate);
                }
            }
            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mContext, "salah", Toast.LENGTH_SHORT).show();
            }
        });
        return 0;
    }

    protected int addcomment() {
        mApiService.addcomment(kosView.kos_id, account.getID(),comment).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(mContext, "Comment Added", Toast.LENGTH_SHORT).show();
                    Log.d("Comment", "Acc ID: " + account.getID() + " Kos ID: "+ kosView.kos_id + "comment: "+ comment);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mContext, "salah komen", Toast.LENGTH_SHORT).show();
            }
        });
        return 0;
    }
//    protected int getKos() {
//        mApiService.getkos(kosView.kos_id).enqueue(new Callback<Kos>() {
//            @Override
//            public void onResponse(Call<Kos> call, Response<Kos> response) {
//                if (response.isSuccessful()) {
//                    kosView = response.body();
//                    for (int i = 0; i < arrayOfKos.size(); i++) {
//                        Kos kos = arrayOfKos.get(i);
//                        if (kos.kos_id == kosView.kos_id) {
//                            // Update the existing object in arrayOfKos with kosView
//                            arrayOfKos.set(i, kosView);
//                            break;
//                        }
//                    }
//
//                }
//            }
//            @Override
//            public void onFailure(Call<Kos> call, Throwable t) {
//                Toast.makeText(mContext, "salah", Toast.LENGTH_SHORT).show();
//            }
//        });
//        return 0;
//    }
}