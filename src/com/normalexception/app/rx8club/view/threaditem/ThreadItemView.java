package com.normalexception.app.rx8club.view.threaditem;

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
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.fragment.thread.EditPostFragment;
import com.normalexception.app.rx8club.fragment.thread.NewThreadFragment;

public class ThreadItemView extends RelativeLayout {
	
	private Button newThreadButton;
	private EditText threadPost;
	
	private OnClickListener _ocl;
	
	private Fragment _source;

	public ThreadItemView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_newthread_children, this, true);
		setupChildren();
	}

	public ThreadItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_newthread_children, this, true);
		setupChildren();
	}

	public ThreadItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_newthread_children, this, true);
		setupChildren();
	}

	/**
	 * Setup the children we contain in this view
	 */
	private void setupChildren() {
		newThreadButton = (Button)findViewById(R.id.newThreadButton);
		threadPost      = (EditText) findViewById(R.id.postPost);
	}
	
	/**
	 * Inflate the view, this technically only gets called the first time the
	 * view is accessed
	 * @param parent	The parent of the view
	 * @return			An inflated object
	 */
	public static ThreadItemView inflate(ViewGroup parent) {
		ThreadItemView threadView = (ThreadItemView)LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_newthread, parent, false);
		return threadView;
	}
	
	/**
	 * Setup our view here.  After the view has been inflated and all of the
	 * view objects have been initialized, we can inflate our view here
	 * @param ti	 The model we are going to use to populate the view
	 * @param source The source fragment, used to control what we populate
	 * @param ocl	 The listener object we are going to attach to the view
	 */
	public void setThreadItem(final ThreadItemModel ti, final Fragment source, final OnClickListener ocl) {
		this._ocl = ocl;
		this._source = source;
		
		 // Because we use this class when handling edits, we need to make
        // sure that we cast this properly and then rename our submit button
        if(_source instanceof NewThreadFragment) {
        	newThreadButton.setOnClickListener(_ocl);
        } else if (_source instanceof EditPostFragment) {
        	newThreadButton.setOnClickListener(_ocl);
        	newThreadButton.setText("Submit Changes");
        	
        	threadPost.setText(ti.getPost());
        }        
	}
}
