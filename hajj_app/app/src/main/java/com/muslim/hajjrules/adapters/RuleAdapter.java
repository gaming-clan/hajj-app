package com.muslim.hajjrules.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muslim.hajjrules.R;
import com.muslim.hajjrules.model.HajjRule;

import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.RuleViewHolder> {
    private Context context;
    private List<HajjRule> rules;
    private OnItemClickListener listener;
    private OnFavoriteClickListener favoriteListener;

    public interface OnItemClickListener {
        void onItemClick(HajjRule rule);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(HajjRule rule);
    }

    public RuleAdapter(Context context, List<HajjRule> rules) {
        this.context = context;
        this.rules = rules;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener favoriteListener) {
        this.favoriteListener = favoriteListener;
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rule, parent, false);
        return new RuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleViewHolder holder, int position) {
        HajjRule rule = rules.get(position);
        holder.title.setText(rule.getTitle());
        holder.description.setText(rule.getDescription());
        holder.favorite.setVisibility(favoriteListener == null ? View.GONE : View.VISIBLE);
        if (favoriteListener != null) {
            holder.favorite.setImageResource(rule.isFavorite()
                    ? R.drawable.ic_bookmark_filled_24 : R.drawable.ic_bookmark_outline_24);
            holder.favorite.setColorFilter(androidx.core.content.ContextCompat.getColor(context,
                    rule.isFavorite() ? R.color.accent_dark : R.color.navigation_unselected));
            holder.favorite.setContentDescription(context.getString(
                    rule.isFavorite() ? R.string.remove_saved_rule : R.string.save_rule));
            holder.favorite.setOnClickListener(v -> favoriteListener.onFavoriteClick(rule));
        }
        
        // Set the image if available
        if (rule.getImageResourceId() != 0) {
            holder.imageIcon.setImageResource(rule.getImageResourceId());
            holder.imageIcon.setVisibility(View.VISIBLE);
        } else {
            holder.imageIcon.setVisibility(View.GONE);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(rule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rules != null ? rules.size() : 0;
    }

    public void updateRules(List<HajjRule> newRules) {
        this.rules = newRules;
        notifyDataSetChanged();
    }

    static class RuleViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        ImageView imageIcon;
        ImageButton favorite;

        public RuleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_rule_title);
            description = itemView.findViewById(R.id.text_rule_description);
            imageIcon = itemView.findViewById(R.id.image_rule_icon);
            favorite = itemView.findViewById(R.id.button_save_rule);
        }
    }
}
