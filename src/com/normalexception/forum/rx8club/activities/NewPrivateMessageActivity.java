package com.normalexception.forum.rx8club.activities;

import android.os.Bundle;

import com.normalexception.forum.rx8club.R;

public class NewPrivateMessageActivity extends ForumBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle("RX8Club.com Forums");
        
        setContentView(R.layout.activity_new_private_message);
        
        // Register the titlebar gui buttons
        this.registerGuiButtons();
        
        findViewById(R.id.newPmButton).setOnClickListener(this);
    }

	@Override
	protected void enforceVariants(int currentPage, int lastPage) {	
	}
}
