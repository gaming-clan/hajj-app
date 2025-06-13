package com.muslim.hajjrules;

import android.os.Bundle;
import android.view.MenuItem;
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

        tvTitle.setText(title);
        tvDesc.setText(desc);
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
