package com.tinyspeck.glitchhq;

import java.util.Iterator;
import java.util.Vector;

import org.json.JSONObject;

import com.tinyspeck.android.GlitchRequest;
import com.tinyspeck.android.GlitchRequestDelegate;
import com.tinyspeck.glitchhq.Sidebar.sidebarItem;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;

public class Sidebar extends Activity implements GlitchRequestDelegate {

	private SidebarListViewAdapter m_adapter;
	private LinearListView m_listView;

	private String m_actItemLast;

	private View m_root;
	private Activity m_activity;

	public class sidebarItem {
		 Boolean isHeader;
		 Boolean isTop = false;
		 String text; 		  
		 Page page;
		 int badge;
 	 };
	
	private Vector<sidebarItem> m_sbList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sidebar_view);
		
		m_sbList = getSidebarList();

		MyApplication app = (MyApplication) getApplicationContext();
		m_activity = app.homeScreen;
		m_adapter = new SidebarListViewAdapter(this, m_sbList);
		m_listView = (LinearListView) findViewById(R.id.SidebarListView);
		m_listView.setAdapter(m_adapter);
		
		GlitchRequest request = ((MyApplication)((HomeScreen)m_activity).getApplication()).glitch.getRequest("mail.getUnreadCount");
		request.execute(this);
	}

	public Activity getActivity() {
		return m_activity;
	}

	protected boolean doesSupportRefresh() {
		return false;
	}

	protected boolean doesSupportMore() {
		return false;
	}

	protected void scrollToTop() {
		ScrollView sv = (ScrollView) m_root.findViewById(R.id.SidebarScrolLView);
		sv.smoothScrollTo(0, 0);
	}
	
	public void setSidebarBadge(Page p, int count) {
		Iterator<sidebarItem> itr = m_sbList.iterator();
		sidebarItem item;
		
		while(itr.hasNext()) {
			item = itr.next();
			if (item.page == p) {
				item.badge = count;
				break;
			}
		}
		m_adapter.notifyDataSetChanged();
	}

	private Vector<sidebarItem> getSidebarList()
	{
		return new Vector<sidebarItem>() {
		
			private static final long serialVersionUID = -1411733025455724158L;
		{
			add(new sidebarItem() {{
				isHeader = true;
				text = " ";
				isTop = true;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Activity Feed";
				page = Page.Activity;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Friends";
				page = Page.Friends;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Mailbox";
				page = Page.Mailbox;
			}});
			
			add(new sidebarItem() {{
				isHeader = true;
				text = " ";
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Your Profile";
				page = Page.Profile;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Skill Picker";
				page = Page.Skills;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Achievements";
				page = Page.Achievements;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Quest Log";
				page = Page.Quests;
			}});
			
			// Settings section
			add(new sidebarItem() {{
				isHeader = true;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Recent Snaps";
				page = Page.RecentSnaps;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Encyclopedia";
				page = Page.Encyclopedia;
			}});
			
			add(new sidebarItem() {{
				isHeader = false;
				text = "Settings";
				page = Page.Settings;
			}});
		}}; 
	}

	public void onRequestBack(String method, JSONObject response)
	{
		if (method == "mail.getUnreadCount") {
			if (response.optInt("ok") == 1) {
				setSidebarBadge(Page.Mailbox, response.optInt("unread_count"));
			}
		}
	}
	
	public void requestFinished(GlitchRequest request) {
		if( getActivity() == null )
			return;
		
        if (request != null && request.method != null )
        {
        	JSONObject response = request.response;
        	if( response != null )
        	{
        		Log.i("response", " method: " + request.method + " response: " + request.response );
        		onRequestBack( request.method, response );
        	}
        }
	}

	public void requestFailed(GlitchRequest request) {
		((HomeScreen)getActivity()).requestFailed(request);		
	}
}
