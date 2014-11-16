package com.normalexception.app.rx8club.view.category;

/************************************************************************
 * NormalException.net Software, and other contributors
 * http://www.normalexception.net
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ************************************************************************/

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.fragment.FragmentUtils;
import com.normalexception.app.rx8club.fragment.category.CategoryFragment;
import com.normalexception.app.rx8club.utils.SpecialNumberFormatter;
import com.normalexception.app.rx8club.view.ViewHolder;

public class CategoryView extends RelativeLayout {
	
	private TextView title;
	private TextView desc;
	private TextView postCount;
	private TextView threadCount;
	private TextView subCount;
	
	private ImageView subCountLabel;
	private ImageView postCountLabel;
	private ImageView image;
	private ImageView threadCountLabel;
	
	/**
	 * Constructor to a category view object
	 * @param context	The source context
	 */
	public CategoryView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_category_children, this, true);
		setupChildren();
	}
	
	/**
	 * Constructor to a category view object
	 * @param context	The source context
	 * @param attrs		Attribute set for view
	 */
	public CategoryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_category_children, this, true);
		setupChildren();
	}

	/**
	 * Constructor to a category view object
	 * @param context	The source context
	 * @param attrs		Attribute set for view
	 * @param defStyle	Default style
	 */
	public CategoryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_category_children, this, true);
		setupChildren();
	}
	
	/**
	 * Setup the children we contain in this view
	 */
	private void setupChildren() {
		title = (TextView) findViewById(R.id.cv_title);
		desc  = (TextView) findViewById(R.id.cv_desc);
		postCount = (TextView) findViewById(R.id.cv_postCount);
		threadCount = (TextView) findViewById(R.id.cv_threadCount);
		subCount = (TextView) findViewById(R.id.cv_subCount);
		
		subCountLabel = (ImageView) findViewById(R.id.cv_subCount_label);
		postCountLabel = (ImageView) findViewById(R.id.cv_postCount_label);
		threadCountLabel = (ImageView) findViewById(R.id.cv_threadCount_label);
		
		image = (ImageView) findViewById(R.id.cv_image);
	}
	
	/**
	 * Inflate the view, this technically only gets called the first time the
	 * view is accessed
	 * @param parent	The parent of the view
	 * @return			An inflated object
	 */
	public static CategoryView inflate(ViewGroup parent) {
		CategoryView categoryView = (CategoryView)LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_category, parent, false);
		return categoryView;
	}
	
	/**
	 * Setup our view here.  After the view has been inflated and all of the
	 * view objects have been initialized, we can inflate our view here
	 * @param cv	The model we are going to use to populate the view
	 */
	public void setCategory(final CategoryModel cv) {
		
		title.setText(cv.getTitle());
        desc.setText(cv.getDescription());
        
        if(cv.getPostCount() == null || cv.getThreadCount() == null) {
        	setMode(this, true);
        } else {
        	setMode(this, false);
        	postCount.setText(
        			SpecialNumberFormatter.collapseNumber(cv.getPostCount()));
        	threadCount.setText(
        			SpecialNumberFormatter.collapseNumber(cv.getThreadCount()));
        	
        	// First we need to register a listener for a regular 
        	// click.  We simply launch the link here
        	this.setOnClickListener(new OnClickListener() {
    			@Override
	            public void onClick(View v) {
					if(cv.getLink() != null) {
						// Create new fragment and transaction
						Bundle args = new Bundle();
						args.putString("link", cv.getLink());
						Fragment newFragment = new CategoryFragment();
						FragmentUtils.fragmentTransaction((FragmentActivity)getContext(), 
								newFragment, true, true, args);
					}
				}
        	});
        	
        	// If the forum has sub forums, we can register a 
        	// long click listener that will pop up a menu
        	if(cv.getSubCategories().size() > 0) {
        		((TextView) ViewHolder.get(this, R.id.cv_subCount))
        			.setText(Integer.toString(cv.getSubCategories().size()));
        		this.setOnLongClickListener(new OnLongClickListener() {
        	        @Override
        	        public boolean onLongClick(View v) {
        	        	SubCategoryDialog scd = 
        	        			new SubCategoryDialog((FragmentActivity)getContext(), cv.getSubCategories());
        	        	scd.registerToExecute();
        	        	scd.show();
        	        	return true;
        	        }
        		});
        	}
        	else {
        		subCountLabel.setVisibility(View.GONE);
        		subCount.setVisibility(View.GONE);
        	}
        }
	}
	
	/**
	 * Set the mode of the category line
	 * @param vi		The view line
	 * @param isTitle	If we are going to represent a title
	 */
	private void setMode(View vi, boolean isTitle) {
		int showMode = isTitle? View.GONE : View.VISIBLE;
		int colorMode= isTitle? Color.DKGRAY : Color.GRAY;
		int textColor= isTitle? Color.WHITE : Color.BLACK;

		postCount.setVisibility(showMode);
		postCountLabel.setVisibility(showMode);
		threadCount.setVisibility(showMode);
		threadCountLabel.setVisibility(showMode);
		subCount.setVisibility(showMode);
		subCountLabel.setVisibility(showMode);
		image.setVisibility(showMode);
    	vi.setBackgroundColor(colorMode);
    	
    	title.setTextColor(textColor);
	}
}
