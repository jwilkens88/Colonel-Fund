package com.colonelfund.colonelfund;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Activity for Member list creation.
 */
public class MemberListActivity extends AppCompatActivity {
    private ListView lv;
    ToggleButton toggleFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_member_list);

        lv = (ListView) findViewById(R.id.memberListView);
        toggleFavorite = (ToggleButton) findViewById(R.id.toggleFavorite);

        final MemberCollection mcf = new MemberCollection(getApplicationContext());
        Collection<Member> memberList = mcf.getMembersList();

        //make array adapter
        ArrayAdapter arrayAdapter = new MemberListAdapter(this, generateData(memberList));
        lv.setAdapter(arrayAdapter);

        // Add listeners for each list item.
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemberListModel item = (MemberListModel) lv.getItemAtPosition(position);
                String myItem = item.getUserID();
                Intent intent = new Intent(MemberListActivity.this, ViewMemberActivity.class);
                intent.putExtra("SelectedMember", mcf.get(myItem));
                startActivity(intent);
            }
        });
    }

    /**
     * Added for back button pre API 16
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about_you) {
            Intent intent = new Intent(this, ViewProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.your_history_events) {
            Intent intent = new Intent(this, MyHistoryEventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout_item) {
            AccessToken token = AccessToken.getCurrentAccessToken();
            if(token != null) {
                LoginManager.getInstance().logOut();
            }
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    toggleFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked) {
                //Change the icon state
                toggleFavorite.setBackgroundResource(R.mipmap.yellowstar);

            } else {
                toggleFavorite.setBackgroundResource(R.mipmap.whitestar);
            }
        }
    });
    */

    /**
     * Generates Initials and User Name for memberlist.
     *
     * @param memberList
     * @return
     */
    private ArrayList<MemberListModel> generateData(Collection memberList) {
        ArrayList<MemberListModel> models = new ArrayList<MemberListModel>();
        Iterator<Member> memberIter = memberList.iterator();
        while (memberIter.hasNext()) {
            Member temp = memberIter.next();
            String firstName = temp.getFirstName();
            String lastName = temp.getLastName();
            models.add(new MemberListModel(firstName.substring(0, 1) + lastName.substring(0, 1),
                    temp.getUserID(), firstName, lastName));
        }
        return models;
    }

    public void tbToggleFav_Click(View view) {
        if (toggleFavorite.isChecked()) {

        }
    }
}
