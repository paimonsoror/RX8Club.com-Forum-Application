package com.normalexception.forum.rx8club.activities;

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

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;
import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.dialog.FavoriteDialog;
import com.normalexception.forum.rx8club.dialog.LogoffDialog;
import com.normalexception.forum.rx8club.fragment.AboutFragment;
import com.normalexception.forum.rx8club.fragment.HomeFragment;
import com.normalexception.forum.rx8club.fragment.LoginFragment;
import com.normalexception.forum.rx8club.fragment.ProfileFragment;
import com.normalexception.forum.rx8club.fragment.SearchFragment;
import com.normalexception.forum.rx8club.fragment.UserCpFragment;
import com.normalexception.forum.rx8club.fragment.category.CategoryFragment;
import com.normalexception.forum.rx8club.fragment.category.FavoritesFragment;
import com.normalexception.forum.rx8club.fragment.pm.PrivateMessageInboxFragment;
import com.normalexception.forum.rx8club.navigation.NavDrawerItem;
import com.normalexception.forum.rx8club.navigation.NavDrawerListAdapter;
import com.normalexception.forum.rx8club.preferences.PreferenceHelper;
import com.normalexception.forum.rx8club.preferences.Preferences;

public class MainActivity extends FragmentActivity {

	private Logger TAG =  LogManager.getLogger(this.getClass());

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private String[] navGuestEnabled;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	enum MenuState {
		USER,
		GUEST
	}
	public MenuState menu_mode = MenuState.GUEST;
	
	/**
	 * Start our analytics tracking
	 */
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}
	
	/**
	 * End our analytics tracking
	 */
	@Override
	public void onStop() {
		super.onStop();
	    EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_basiclist);

		mTitle = mDrawerTitle = getTitle();

		constructNavMenu();

		if (savedInstanceState == null) {
			// Initial display
			displayView(7, false);
		}
	}
	
	private void constructNavMenu() {

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navGuestEnabled = getResources().getStringArray(R.array.nav_drawer_guest);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		for(int i = 0; i < navMenuTitles.length; i++) {
			navDrawerItems.add(
					new NavDrawerItem(
							navMenuTitles[i], 
							navMenuIcons.getResourceId(i, -1),
							Boolean.parseBoolean(navGuestEnabled[i])));
		}

		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				/*R.drawable.ic_drawer, //nav menu toggle icon*/
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void displayView(int position, boolean stack) {
		Fragment _fragment = null;
		switch(position) {
		case 0:
			_fragment = new HomeFragment();
			break;
		case 1:
			Bundle args = new Bundle();
			args.putBoolean("isNewTopics", true);
			_fragment = new CategoryFragment();
			_fragment.setArguments(args);
			break;
		case 2:
			if(PreferenceHelper.isFavoriteAsDialog(this)) {
				FavoriteDialog fd = new FavoriteDialog(this);
				fd.registerToExecute();
				fd.show();
			} else {
				_fragment = new FavoritesFragment();
			}
			break;
		case 3:
			_fragment = new ProfileFragment();
			break;

		case 4:
			_fragment = new PrivateMessageInboxFragment();
			break;
		case 5:
			_fragment = new SearchFragment();
			break;
		case 6:
			_fragment = new UserCpFragment();
			break;
		case 7:
			if(!stack) {
				_fragment = new LoginFragment(stack);
			} else {
				LogoffDialog ld = new LogoffDialog(this);
           		ld.show();
			}
			stack = false;
			break;
		case 8:
			_fragment = new Preferences();
			break;
		case 9:
			_fragment = new AboutFragment();
			break;
		default:
			_fragment = null;
		}

		if (_fragment != null) {
			FragmentManager fragmentManager = this.getSupportFragmentManager();
			FragmentTransaction ft = fragmentManager.beginTransaction();

			if(stack) {
				ft.addToBackStack(null);
				ft.add(R.id.content_frame, _fragment).commit();
			} else {
				ft.replace(R.id.content_frame,  _fragment).commit();
			}

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(String.format("%s - %s", 
					getResources().getString(R.string.app_name),
					navMenuTitles[position].split("\\|")[0]));
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e(TAG, "Error in creating fragment", null);
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	protected class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			displayView(position, true);
		}
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_new_posts:
			displayView(1,true);
			return true;
		case R.id.action_inbox:
			displayView(4,true);
			return true;
		case R.id.action_search:
			displayView(5,true);
			return true;
		case R.id.action_preferences:
			displayView(8,true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		Log.v(TAG, String.format("Setting Up Menu For %s Mode", 
				menu_mode == MenuState.USER? "user" : "guest"));
		menu.findItem(R.id.action_new_posts)
			.setVisible(menu_mode == MenuState.USER);
		menu.findItem(R.id.action_inbox)
			.setVisible(menu_mode == MenuState.USER);
		return true;
	}
	
	public void setUserMenu() {
		menu_mode = MenuState.USER;
	}
	
	public void setGuestMenu() {
		menu_mode = MenuState.GUEST;
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_new_posts).setVisible(!drawerOpen && menu_mode == MenuState.USER);
		menu.findItem(R.id.action_inbox).setVisible(!drawerOpen && menu_mode == MenuState.USER);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		menu.findItem(R.id.action_preferences).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}
