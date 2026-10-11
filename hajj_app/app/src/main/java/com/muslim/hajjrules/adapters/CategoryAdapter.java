package com.muslim.hajjrules.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.muslim.hajjrules.R;
import com.muslim.hajjrules.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categories;
    private OnItemClickListener listener;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.textView.setText(category.getTitle());
        holder.descriptionView.setText(category.getDescription());
        String image = category.getImage();
        if (image != null) {
            String resourceName = image.replaceFirst("\\.[^.]+$", "");
            int imageId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            if (imageId != 0) holder.imageView.setImageResource(imageId);
            else holder.imageView.setImageResource(android.R.drawable.ic_menu_info_details);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    /**
     * Update categories data and refresh the adapter
     * This method is used by MVVM pattern to update data from ViewModel
     */
    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView descriptionView;
        ImageView imageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_category_title);
            descriptionView = itemView.findViewById(R.id.text_category_description);
            imageView = itemView.findViewById(R.id.image_category);
        }
    }
}
