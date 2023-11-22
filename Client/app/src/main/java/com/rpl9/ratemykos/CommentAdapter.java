//package com.rpl9.ratemykos;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
//
//    private List<String> comments; // Replace String with your comment model
//
//    public CommentAdapter(List<String> comments) {
//        this.comments = comments;
//    }
//
//    @NonNull
//    @Override
//    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
//        return new CommentViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
//        String comment = comments.get(position);
//        holder.commentTextView.setText(comment);
//    }
//
//    @Override
//    public int getItemCount() {
//        return comments.size();
//    }
//
//    public static class CommentViewHolder extends RecyclerView.ViewHolder {
//        TextView commentTextView;
//
//        public CommentViewHolder(@NonNull View itemView) {
//            super(itemView);
//            commentTextView = itemView.findViewById(R.id.commentTextView);
//        }
//    }
//}
