package com.muslim.hajjrules;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String title = getIntent().getStringExtra("ruleTitle");
        String desc = getIntent().getStringExtra("ruleDescription");

        TextView tvTitle = findViewById(R.id.text_detail_title);
        TextView tvDesc = findViewById(R.id.text_detail_description);
        ImageView imageView = findViewById(R.id.image_detail_illustration);

        tvTitle.setText(title);
        tvDesc.setText(desc);

        // Set image based on title
        if (title != null) {
            switch (title) {
                case "Shtyllat e Islamit":
                    imageView.setImageResource(R.drawable.pillars_of_islam);
                    break;
                case "Detyrimi i Haxhit":
                    imageView.setImageResource(R.drawable.hajj_obligation);
                    break;
                case "Edukata e Udhëtimit":
                    imageView.setImageResource(R.drawable.travel_etiquette);
                    break;
                case "Ihrami":
                    imageView.setImageResource(R.drawable.ihram);
                    break;
                case "Ndalesat gjatë Ihramit":
                    imageView.setImageResource(R.drawable.prohibitions);
                    break;
                default:
                    imageView.setVisibility(View.GONE);
                    break;
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
