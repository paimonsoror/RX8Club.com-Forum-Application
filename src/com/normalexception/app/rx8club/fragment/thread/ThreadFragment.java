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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.TimeoutFactory;
import com.normalexception.app.rx8club.fragment.AdminFragment;
import com.normalexception.app.rx8club.fragment.FragmentUtils;
import com.normalexception.app.rx8club.fragment.PaginationFragment;
import com.normalexception.app.rx8club.fragment.StylerFragment;
import com.normalexception.app.rx8club.fragment.category.CategoryFragment;
import com.normalexception.app.rx8club.html.HtmlFormUtils;
import com.normalexception.app.rx8club.html.LoginFactory;
import com.normalexception.app.rx8club.html.VBForumFactory;
import com.normalexception.app.rx8club.preferences.PreferenceHelper;
import com.normalexception.app.rx8club.state.AppState;
import com.normalexception.app.rx8club.task.SubmitTask;
import com.normalexception.app.rx8club.user.UserProfile;
import com.normalexception.app.rx8club.utils.Utils;
import com.normalexception.app.rx8club.view.ViewHolder;
import com.normalexception.app.rx8club.view.threadpost.PostModel;
import com.normalexception.app.rx8club.view.threadpost.PostViewArrayAdapter;

/**
 * Activity used to display thread contents.  Within this activity a user can
 * create new posts.
 * 
 * Required Intent Parameters:
 * link - The link to the thread
 * title - The title of the thread
 * page - The page number of the thread
 */
public class ThreadFragment extends Fragment {

	private Logger TAG =  LogManager.getLogger(this.getClass());

	private String currentPageLink;
	private String currentPageTitle;

	private String threadNumber;

	private String pageNumber = "1";

	private String securityToken = "none";
	private String postNumber = "none";
	private String finalPage = "1";
	private String thisPage = "1";
	
	private ArrayList<PostModel> postlist;
	private PostViewArrayAdapter pva;
	private ListView lv;

	private List<String> bmapList;
	
	private boolean isLocked = false;
	
	private ProgressDialog loadingDialog;
	
	private final String MODERATION_TOOLS = "Moderation Tools";
	
	private ThreadFragmentListener tal = 
			new ThreadFragmentListener(this);
	
	private View adminContent = null;
	
	private CategoryFragment parentCategory = null;
	
	public static ThreadFragment newInstance() {
		ThreadFragment tf = new ThreadFragment();
		return tf;
	}
	
	/**
	 * Report our registered onClickListener
	 * @return	The thread's on click listener
	 */
	public ThreadFragmentListener getOnClickListener() {
		return tal;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);   

		lv = (ListView) rootView.findViewById(R.id.mainlistview);
		
		//this.parentCategory = (CategoryFragment) getArguments().getSerializable("parent");
		this.parentCategory = (CategoryFragment) getParentFragment();
		adminContent = inflater.inflate(R.layout.view_newreply_header, lv, false);
		
		// Inflate the header if we are an admin
		//adminContent = inflater.inflate(R.layout.fragment_admin, null);
		getChildFragmentManager()
			.beginTransaction()
			.replace(R.id.fragment_content_admin, AdminFragment.newInstance())
			.commit();
		lv.addHeaderView(adminContent);
		
		// Inflate the footer (pagination, styler, reply box)
		View v = inflater.inflate(R.layout.view_newreply_footer, lv, false);
		
		getChildFragmentManager()
			.beginTransaction()
			.replace(R.id.fragment_content_styler, new StylerFragment())
			.commit();
		
		getChildFragmentManager()
			.beginTransaction()
			.replace(R.id.nr_pagination, PaginationFragment.newInstance(this.tal))
			.commit();

		lv.addFooterView(v);
		v.findViewById(R.id.submitButton).setOnClickListener(tal);
		
		Log.v(TAG, "ThreadFragment view loaded");
        return rootView;
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
		try{
			MainApplication.setState(AppState.State.THREAD, this);

			//threadId = Utils.randomInt(0, 9999);
			
			Log.v(TAG, "Thread Activity Started");
			
			if(TimeoutFactory.getInstance().checkTimeout(this)) {
				postlist = new ArrayList<PostModel>();
				bmapList = new ArrayList<String>();
				if(savedInstanceState == null ||
						(pva == null || pva.getCount() == 0))
					constructView();
				else
					updateList();
			}
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Thread Activity! " + e.getMessage(), e);
		}
	}

	/**
	 * Construct the thread activity view
	 */
	private void constructView() {
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
				currentPageLink = 
						getArguments().getString("link");
				currentPageTitle = 
						getArguments().getString("title");			
				pageNumber = 
						getArguments().getString("page");
				//isPoll = 
				//		getArguments().getBoolean("poll", false);
				isLocked =
						getArguments().getBoolean("locked", false);
				
				if(pageNumber == null) pageNumber = "1";

				Log.v(TAG, "Grabbing link: " + currentPageLink);

				Document doc = 
						VBForumFactory.getInstance().get(getActivity(), currentPageLink);
				
				// First check to see if the title is null, this is possible if the user
				// clicked a link on a thread and got here.
				currentPageTitle = doc.select("title").text();
				
				// Now check if the page is locked, again, we should have already known 
				// this, but if we got here by other means
				isLocked = doc.select("img[src*=threadclosed]").first() != null;
				
				// If the user is guest or if the thread is locked, 
				// then hide the items that they generally wont be able to use
				if(LoginFactory.getInstance().isGuestMode() || isLocked) {
					Log.d(TAG, "Thread Is Locked, Hiding Reply Container");
					ViewHolder.get(getView(), R.id.nr_replycontainer)
						.setVisibility(View.GONE);
				}
				
				// Grab the canonical link if it exists.  This is the safest way to
				// make sure that we got the actual URL of the page
				String canonUrl = null;
				try {
					if((canonUrl = doc.select("link[rel=canonical]").attr("href")) != null) {
						currentPageLink = canonUrl;
						Log.d(TAG, String.format("Grabbed Canonical URL: %s", currentPageLink));
					} 
				} catch (Exception e) {}

				if(doc != null) {	
					publishProgress(getString(R.string.asyncDialogGrabThreadContents));
					
					try {
						getThreadContents(doc);
					} catch (Exception e) {
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(
									getActivity(), R.string.timeout, Toast.LENGTH_SHORT)
									.show();
							}
						});
						Log.e(TAG, "Exception Grabbing Thread Contents", e);
					}
					publishProgress(getString(R.string.asyncDialogPopulating));
					updateList();
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
	 * Update our list with the contents
	 */
	private void updateList() {
		final Fragment _frag = this;
		getActivity().runOnUiThread(new Runnable() {
			public void run() {	
				getView().findViewById(R.id.mainlisttitle).setVisibility(View.VISIBLE);
				((TextView)getView().findViewById(R.id.mainlisttitle))
					.setText(String.format("%s [Page %s]", 
							currentPageTitle, pageNumber.equals("last")? finalPage : pageNumber));
				pva = new PostViewArrayAdapter(_frag, R.layout.view_thread, postlist, tal);
				//pva.setThreadId(threadId);
				lv.setAdapter(pva);
				FragmentUtils.updatePagination(_frag, thisPage, finalPage);
			}
		});
	}

	/**
	 * Grab contents from the forum that the user clicked on
	 * @param doc	The document parsed from the link
	 * @param id	The id number of the link
	 * @return		An arraylist of forum contents
	 */
	public void getThreadContents(Document doc) {    	
		// Update pagination
		try {
			Elements pageNumbers = doc.select("div[class=pagenav]");
			if(pageNumbers.first() != null) {
				Elements pageLinks = 
					pageNumbers.first().select("td[class^=vbmenu_control]");
				thisPage = pageLinks.text().split(" ")[1];
				finalPage = pageLinks.text().split(" ")[3];
				Log.d(TAG, String.format("This Page: %s, Final Page: %s", thisPage, finalPage));
			} else {
				Log.d(TAG, "Thread only contains one page");
			}
		} catch (Exception e) {
			Log.e(TAG, "We had an error with pagination", e);
		}
		
		// Is user thread admin??
		Elements threadTools = doc.select("div[id=threadtools_menu] > form > table");
		if(threadTools.text().contains(MODERATION_TOOLS)) {
			Log.d(TAG, "<><> User has administrative rights here! <><>");
		} else {
			//adminContent.setVisibility(View.GONE);
			lv.removeHeaderView(adminContent);
		}

		// Get the user's actual ID, there is a chance they never got it
		// before
		UserProfile.getInstance().setUserId(
				HtmlFormUtils.getInputElementValueByName(doc, "loggedinuser"));

		// Get Post Number and security token
		securityToken = HtmlFormUtils.getInputElementValueByName(doc, "securitytoken");
		
		Elements pNumber = 
				doc.select("a[href^=http://www.rx8club.com/newreply.php?do=newreply&noquote=1&p=]");
		String pNumberHref = pNumber.attr("href");
		postNumber = pNumberHref.substring(pNumberHref.lastIndexOf("=") + 1);
		threadNumber = doc.select("input[name=searchthreadid]").attr("value");

		Elements posts = doc.select("div[id=posts]").select("div[id^=edit]");
		Log.v(TAG, String.format("Parsing through %d posts", posts.size()));
		for(Element post : posts) {
			try {
				Elements innerPost = post.select("table[id^=post]");
	
				// User Control Panel
				Elements userCp = innerPost.select("td[class=alt2]");
				Elements userDetail = userCp.select("div[class=smallfont]");
				Elements userSubDetail = userDetail.last().select("div"); 
				Elements userAvatar = userDetail.select("img[alt$=Avatar]");
	
				// User Information
				PostModel pv = new PostModel();
				pv.setUserName(userCp.select("div[id^=postmenu]").text());
				pv.setIsLoggedInUser(
						LoginFactory.getInstance().isLoggedIn()?
								UserProfile.getInstance().getUsername().equals(
										pv.getUserName()) : false);	
				pv.setUserTitle(userDetail.first().text());
				pv.setUserImageUrl(userAvatar.attr("src"));
				pv.setPostDate(innerPost.select("td[class=thead]").first().text());
				pv.setPostId(Utils.parseInts(post.attr("id")));
				pv.setRootThreadUrl(currentPageLink);
				
				// get Likes if any exist
				Elements eLikes = innerPost.select("div[class*=vbseo_liked] > a");
				List<String> likes = new ArrayList<String>();
				for(Element eLike : eLikes)
					likes.add(eLike.text());
				pv.setLikes(likes);
	
				Iterator<Element> itr = userSubDetail.listIterator();
				while(itr.hasNext()) {
					String txt = itr.next().text();
					if(txt.contains("Location:"))
						pv.setUserLocation(txt);
					else if (txt.contains("Posts:"))
						pv.setUserPostCount(txt);
					else if (txt.contains("Join Date:"))
						pv.setJoinDate(txt);
				}
	
				// User Post Content
				pv.setUserPost(formatUserPost(innerPost));
				
				// User signature
				try {
					Element userSig = innerPost.select("div[class=konafilter]").first();
					pv.setUserSignature(userSig.html());
				} catch (NullPointerException npe) {}
	
				Elements postAttachments = innerPost.select("a[id^=attachment]");
				if(postAttachments != null && !postAttachments.isEmpty()) {
					ArrayList<String> attachments = new ArrayList<String>();
					for(Element postAttachment : postAttachments) {
						attachments.add(postAttachment.attr("href"));
					}
					pv.setAttachments(attachments);
				}
				
				pv.setSecurityToken(securityToken);
				
				// Make sure we aren't adding a blank user post
				if(pv.getUserPost() != null)
					postlist.add(pv);
			} catch (Exception e) {
				Log.w(TAG, "Error Parsing Post...Probably Deleted");
			}
		}		
	}
	
	/**
	 * Format the user post by removing the vb style quotes and the 
	 * duplicate youtube links
	 * @param innerPost	The element that contains the inner post
	 * @return			The formatted string
	 */
	private String formatUserPost(Elements innerPost) {
		try {
			Element ipost = 
					innerPost.select("td[class=alt1]").select("div[id^=post_message]").first();
			
			// Only if there is a post to key off of
			if(ipost != null) {
				// Remove the duplicate youtube links (this is caused by a plugin on 
				// the forum that embeds youtube videos automatically)
				for(Element embedded : ipost.select("div[id^=ame_doshow_post_]"))
					embedded.remove();
			
			
				// Remove the vbulletin quotes
				return Utils.reformatQuotes(ipost.html());
			} else {
				return null;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error Parsing Post", e);
			return null;
		}
	}

	/**
	 * Listener that handles the clicks of buttons that are found on the thread
	 * posts
	 */
	public class ThreadFragmentListener implements OnClickListener {
		private Fragment src = null;
		
		public ThreadFragmentListener(Fragment _src) {
			src = _src;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View arg0) {
			Fragment _fragment = null;
			Bundle args = new Bundle();
			args.putString("title", currentPageTitle);
			
			// Make sure we aren't manipulating on 'last' as a page
			if(pageNumber.contains("last"))
				pageNumber = finalPage;
	
			switch(arg0.getId()) {
			case R.id.nr_downButton:
				Log.d(TAG, "Scrolling To Bottom of Screen");
				lv.setSelection(lv.getCount());
				break;
			case R.id.previousButton:
				_fragment = ThreadFragment.newInstance();
				args.putString("link", Utils.decrementPage(currentPageLink, pageNumber));
				args.putString("page", String.valueOf(Integer.parseInt(pageNumber) - 1));
				break;
			case R.id.nextButton:
				_fragment = ThreadFragment.newInstance();
				args.putString("link", Utils.incrementPage(currentPageLink, pageNumber));
				args.putString("page", String.valueOf(Integer.parseInt(pageNumber) + 1));
				break;
			case R.id.submitButton:
				_fragment = null;
				String advert = PreferenceHelper.isAdvertiseEnabled(MainApplication.getAppContext())?
						LoginFactory.getInstance().getSignature() : "";
				String thePost = ((TextView)getActivity()
						.findViewById(R.id.postBox)).getText().toString();
				if(thePost != null && !thePost.equals("")) {
					String toPost = 
						String.format("%s\n\n%s", thePost, advert);
					SubmitTask sTask = new SubmitTask(
						src, bmapList, securityToken, 
						threadNumber, postNumber,
						toPost, currentPageTitle, pageNumber);
					//sTask.debug();
					sTask.execute();
				} else {
					Toast.makeText(getActivity(), R.string.enterPost, Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.paginationText:
				final EditText input = new EditText(getActivity());
				input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
				new AlertDialog.Builder(getActivity())
				.setTitle("Go To Page...")
				.setMessage("Enter New Page Number")
				.setView(input)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();
						Fragment __fragment = ThreadFragment.newInstance();
						
						Bundle _args = new Bundle();
						_args.putString("link", Utils.getPage(currentPageLink, value));
						_args.putString("page", value);
						_args.putString("title", currentPageTitle);

						FragmentUtils.fragmentTransaction(getActivity(), 
								__fragment, true, false, _args, "thread");
					}
				}).setNegativeButton("Cancel", null).show();
				break;
	
			case R.id.firstButton:
				_fragment = ThreadFragment.newInstance();
				args.putString("link", Utils.getPage(currentPageLink, Integer.toString(1)));
				args.putString("page", "1");
				break;
	
			case R.id.lastButton:
				_fragment = ThreadFragment.newInstance();
				args.putString("link", Utils.getPage(currentPageLink, finalPage));
				args.putString("page", finalPage);
				break;	
	
			default:
				_fragment = null;
				break;
			}	
	
			if(_fragment != null) {
				FragmentUtils.fragmentTransaction(getActivity(), _fragment, true, false, args, "thread");
			}
		}
	}

	/**
	 * Handle the result of the image attachment dialog
	 * @param requestCode	We are expecting the code for the image result
	 * @param resultCode	We are expecting an OK result
	 * @param data			The image object
	 */
	// NOT USED
	/**
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == StylerFragment.RESULT_LOAD_IMAGE 
				&& resultCode == RESULT_OK && null != data) {
			Log.d(TAG, "Image Loaded...");
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			bmapList.add(picturePath);         
		}     
	}
	*/
	
	/**
	 * Report the thread number
	 * @return	The thread number
	 */
	public String getThreadNumber() {
		return threadNumber;
	}
	
	/**
	 * Report the security token for the user
	 * @return	The security token for the user
	 */
	public String getSecurityToken() {
		return this.securityToken;
	}
	
	/**
	 * Report the parent category
	 * @return	The parent category
	 */
	public Fragment getParentCategory() {
		return this.parentCategory;
	}
}
