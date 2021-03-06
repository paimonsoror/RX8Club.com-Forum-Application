package com.normalexception.app.rx8club.fragment.thread;

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

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.TimeoutFactory;
import com.normalexception.app.rx8club.html.HtmlFormUtils;
import com.normalexception.app.rx8club.state.AppState;
import com.normalexception.app.rx8club.task.UpdateTask;
import com.normalexception.app.rx8club.view.threaditem.ThreadItemModel;
import com.normalexception.app.rx8club.view.threaditem.ThreadItemViewArrayAdapter;

/**
 * Activity used whenever the user wants to edit the post
 * 
 * Required Intent Parameters:
 * postId - the post id of the post that is to be edited
 * securitytoken - the security token of the session
 * link - the original thread link
 * title - original thread title
 * page - page number
 * threadnumber - original thread number
 */
public class EditPostFragment extends Fragment {

	private Logger TAG =  LogManager.getLogger(this.getClass());
	private String postId, securityToken, postHash, poststart, 
	pageNumber, pageTitle, postMessage;
	private boolean delete = false, deleteThread = false;

	private ListView lv;

	private ArrayList<ThreadItemModel> tlist;
	private ThreadItemViewArrayAdapter pva;

	private ProgressDialog loadingDialog;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);    	
        return rootView;
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainApplication.setState(AppState.State.EDIT_POST, this);

		Log.v(TAG, "Edit Thread Activity Started");

		if(TimeoutFactory.getInstance().checkTimeout(this)) {
			postId = 
					getArguments().getString("postid");
			securityToken = 
					getArguments().getString("securitytoken");
			pageNumber =
					getArguments().getString("pagenumber");
			pageTitle =
					getArguments().getString("pagetitle");
			delete = 
					getArguments().getBoolean("delete", false);
			deleteThread = 
					getArguments().getBoolean("deleteThread", false);

			lv      = (ListView)getView().findViewById(R.id.mainlistview);
			lv.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
			lv.setScrollContainer(false);
			tlist   = new ArrayList<ThreadItemModel>();

			constructView();
		}
	}

	/**
	 * Construct the view items
	 */
	private void constructView() {
		final Fragment _src = this;
		AsyncTask<Void,String,Void> updaterTask = new AsyncTask<Void,String,Void>() {
			@Override
			protected void onPreExecute() {
				
				loadingDialog = 
						ProgressDialog.show(getActivity(), 
								getString(R.string.loading), 
								getString(R.string.pleaseWait), true);
			}
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Document editPage = 
							HtmlFormUtils.getEditPostPage(securityToken, postId);
					postMessage = editPage.select("textarea[name=message]").text();

					Elements pansurr = editPage.select("td[class=panelsurround]");
					securityToken = getInputElementValue(pansurr, "securitytoken");
					postId = getInputElementValue(pansurr, "p");
					postHash = getInputElementValue(pansurr, "posthash");
					poststart = getInputElementValue(pansurr, "poststarttime");

					if(delete)
						deletePost();

					ThreadItemModel ti = new ThreadItemModel();
					ti.setPost(postMessage);
					tlist.add(ti);


					getActivity().runOnUiThread(new Runnable() {
						public void run() {

							pva = new ThreadItemViewArrayAdapter(_src, 
									R.layout.view_newthread, tlist, new EditPostListener(_src));
							lv.setAdapter(pva);	
						}
					});
				} catch (Exception e) {
					Log.d(TAG, String.format("Security Token: %s, Post ID: %s", securityToken, postId));
					Log.e(TAG, e.getMessage(), e);
				}
				return null;
			}
			@Override
			protected void onProgressUpdate(String...progress) {
				if(loadingDialog != null)
        			loadingDialog.setMessage(progress[0]);
			}

			@Override
		    protected void onPostExecute(Void result) {
				try {
					loadingDialog.dismiss();
					loadingDialog = null;
				} catch (Exception e) {
					Log.w(TAG, e.getMessage());
				}
			}
		};
		updaterTask.execute();

	}

	/**
	 * Delete the post
	 */
	private void deletePost() {
		UpdateTask utask = 
				new UpdateTask(this, this.securityToken, this.postId,
						this.postHash, this.poststart, this.pageNumber, 
						this.pageTitle, null, true, deleteThread);
		utask.execute();
	}

	/**
	 * Report the value inside of an input element
	 * @param pan	The panel where all of the input elements reside
	 * @param name	The name of the input to get the value for
	 * @return		The string value of the input
	 */
	private String getInputElementValue(Elements pan, String name) {
		return pan.select("input[name=" + name + "]").attr("value");
	}

	class EditPostListener implements OnClickListener {
		private Fragment _src;
		public EditPostListener(Fragment src) {
			_src = src;
		}
		/*
		 * (non-Javadoc)
		 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onClick(android.view.View)
		 */
		@Override
		public void onClick(View arg0) {
			switch(arg0.getId()) {
			case R.id.newThreadButton:
				String toPost = 
				((TextView)getView().findViewById(R.id.postPost)).getText().toString();
				UpdateTask utask = 
						new UpdateTask(_src, securityToken, postId,
								postHash, poststart, pageNumber, 
								pageTitle, toPost, false, false);
				utask.execute();
				break;
			}
		}
	}
}
