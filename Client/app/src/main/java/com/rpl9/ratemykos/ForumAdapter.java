package com.rpl9.ratemykos;

import static com.rpl9.ratemykos.KosDetailActivity.commentList;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rpl9.ratemykos.model.Comment;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {
    private List<Comment> comments;

    public ForumAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forumlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment currentComment = comments.get(position);
        Log.d("COMMENT", "Binding data: " + currentComment.username + " - " + currentComment.comment);
        holder.bind(currentComment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView commentTextView;
        TextView toggleReply;
//        RecyclerView replyRecyclerView;
//        ReplyAdapter replyAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.forumUser);
            commentTextView = itemView.findViewById(R.id.forumComment);
//            toggleReply = itemView.findViewById(R.id.forumSeeReply);
//            replyRecyclerView = itemView.findViewById(R.id.replyRecyclerView);
//            replyAdapter = new ReplyAdapter(new ArrayList<>());
//            replyRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
//            replyRecyclerView.setAdapter(replyAdapter);

//            toggleReply.setOnClickListener(v -> {
//                if (replyRecyclerView.getVisibility() == View.VISIBLE) {
//                    replyRecyclerView.setVisibility(View.GONE);
//                } else {
//                    replyRecyclerView.setVisibility(View.VISIBLE);
//                }
//            });
        }

        public void bind(Comment comment) {
            // Set data to views
            usernameTextView.setText(comment.username);
            commentTextView.setText(comment.comment);
            // Update data for the nested CommentAdapter (for replies)
//            if (comment.replies != null) {
//                replyAdapter.setReplies(comment.replies);
//                Log.d("REPLYYY", comment.replies.toString());
//                replyAdapter.notifyDataSetChanged();
//            }
        }
    }
}
