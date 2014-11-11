package com.normalexception.app.rx8club.navigation;

public class NavDrawerItem {
    
    private String title;
    private int icon;
    private boolean _isGuestEnabled = false;
     
    public NavDrawerItem(){}
 
    public NavDrawerItem(String title, int icon, boolean ige){
        this.title = title;
        this.icon = icon;
        this._isGuestEnabled = ige;
    }
    public String getTitle(){
        return this.title;
    }
    
    public boolean isGuestEnabled() {
    	return this._isGuestEnabled;
    }
    
    public void setGuestEnabled(boolean ige) {
    	this._isGuestEnabled = ige;
    }
    
    public int getIcon(){
        return this.icon;
    }
     
    public void setTitle(String title){
        this.title = title;
    }
     
    public void setIcon(int icon){
        this.icon = icon;
    }
}