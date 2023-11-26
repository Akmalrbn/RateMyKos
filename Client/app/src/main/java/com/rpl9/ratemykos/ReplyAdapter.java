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
import androidx.recyclerview.widget.RecyclerView;

import com.rpl9.ratemykos.model.Comment;
import com.rpl9.ratemykos.request.BaseApiService;
import com.rpl9.ratemykos.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private List<Comment> replies;
    Comment reply;
    public ReplyAdapter(List<Comment> replies) {
        this.replies = replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forumlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        reply = replies.get(position);
        Log.d("REPLY", "Binding data: " + reply.username + " - " + reply.comment);
        // Set data to views
        holder.usernameTextView.setText(reply.username);
        holder.commentTextView.setText(reply.comment);
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView commentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.replyUser);
            commentTextView = itemView.findViewById(R.id.replyComment);
        }
    }
}
