package com.muslim.hajjrules.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public interface OnItemClickListener {
        void onItemClick(HajjRule rule);
    }

    public RuleAdapter(Context context, List<HajjRule> rules) {
        this.context = context;
        this.rules = rules;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(rule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    public void updateRules(List<HajjRule> newRules) {
        this.rules = newRules;
        notifyDataSetChanged();
    }

    static class RuleViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        public RuleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_rule_title);
            description = itemView.findViewById(R.id.text_rule_description);
        }
    }
}
