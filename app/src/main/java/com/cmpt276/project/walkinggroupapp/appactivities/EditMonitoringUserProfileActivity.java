package com.cmpt276.project.walkinggroupapp.appactivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.project.walkinggroupapp.R;
import com.cmpt276.project.walkinggroupapp.model.ModelManager;
import com.cmpt276.project.walkinggroupapp.model.User;
import com.cmpt276.project.walkinggroupapp.model.WalkingGroup;
import com.cmpt276.project.walkinggroupapp.proxy.ProxyBuilder;

import java.util.ArrayList;
import java.util.List;

public class EditMonitoringUserProfileActivity extends AppCompatActivity {

    public static final String USER_ID = "UserID";

    private List<WalkingGroup> groupsList = new ArrayList<>();

    private ListView groupsListView;

    private long userId;


    private ModelManager modelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_monitoring_user_profile);

        modelManager = ModelManager.getInstance();

        extractDataFromIntent();


        ProxyBuilder.SimpleCallback<List<Long>> callback = groupIdsList -> getIdsOfGroupsAUserIsMemberOfResponse(groupIdsList);
        modelManager.getIdsOfGroupsAUserIsMemberOf(EditMonitoringUserProfileActivity.this, callback, userId);






        populateGroupsList();
        registerGroupsListOnItemClick();

        //TODO need to populate member list;
    }

    private void getIdsOfGroupsAUserIsMemberOfResponse(List<Long> groupIdsList) {
        for (Long groupId: groupIdsList) {
            ProxyBuilder.SimpleCallback<WalkingGroup> callback = returnedGroup -> getMemberOfGroupResponse(returnedGroup);
            modelManager.getWalkingGroupById(EditMonitoringUserProfileActivity.this, callback, groupId);
        }
    }

    private void getMemberOfGroupResponse(WalkingGroup returnedGroup) {
        groupsList.add(returnedGroup);
        populateGroupsList();
        registerGroupsListOnItemClick();
    }

    private void populateGroupsList() {
        ArrayAdapter<WalkingGroup> adapter = new EditMonitoringUserProfileActivity.memberListAdapter();
        //Configure ListView
        groupsListView = findViewById(R.id.jacky_edit_user_member_list);
        groupsListView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "Done Populating List", Toast.LENGTH_LONG).show();
    }

    private class memberListAdapter extends ArrayAdapter<WalkingGroup> {                                                 //Code for complexList based from Brian Frasers video
        public memberListAdapter() {
            super(EditMonitoringUserProfileActivity.this, R.layout.group_layout, groupsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure We are given a view
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.group_layout, parent, false);
            }
            //Find a group to add
            Log.i("MyApp", "Inside Monitoring By");
            WalkingGroup currentGroup = groupsList.get(position);

            //Group Description
            TextView makeName = itemView.findViewById(R.id.jacky_group_description_dynamic);
            makeName.setText(currentGroup.getGroupDescription());


            return itemView;
        }
    }

    private void registerGroupsListOnItemClick()                                                                                    //For clicking on list object
    {
        final ListView list = findViewById(R.id.jacky_edit_user_member_list);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Pressed Long to edit" + position, Toast.LENGTH_SHORT).show();
                Log.i("MyApp", "Pressed Long" + position);
//                selectedPosition = position;
                PopupMenu popupMenu = new PopupMenu(EditMonitoringUserProfileActivity.this, viewClicked);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {          //Code from https://www.youtube.com/watch?v=LXUDqGaToe0
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.cancel:
//                                doCancel();
                                break;
                            case R.id.delete:
                                removeUser(position);
                                break;
                        }
                        return true;
                    }

                });

                popupMenu.show();
                return true;
            }
        });
    }

    private void removeUser(int position){
        long groupId = groupsList.get(position).getId();
        ProxyBuilder.SimpleCallback<List<User>> callback = returnedMembersList -> removeFromGroupResponse(returnedMembersList);
        modelManager.removeUserFromGroup(EditMonitoringUserProfileActivity.this, callback, groupId, userId);
    }

    private void removeFromGroupResponse(List<User> returnedMembersList) {
        ProxyBuilder.SimpleCallback<List<Long>> callback = groupIdsList -> getIdsOfGroupsAUserIsMemberOfResponse(groupIdsList);
        modelManager.getIdsOfGroupsAUserIsMemberOf(EditMonitoringUserProfileActivity.this, callback, userId);
    }

    private void extractDataFromIntent(){
        Intent intent = getIntent();
        userId = intent.getLongExtra(USER_ID, 0);
        if(userId == 0)
        {
            Toast.makeText(getApplicationContext(), "Invalid User", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public static Intent makeIntent(Context context, long editUserId){
        Intent intent = new Intent(context, EditMonitoringUserProfileActivity.class);
        intent.putExtra(USER_ID, editUserId);
        return intent;
    }
}