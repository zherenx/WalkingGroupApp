package com.cmpt276.project.walkinggroupapp.appactivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.project.walkinggroupapp.R;
import com.cmpt276.project.walkinggroupapp.model.ModelManager;
import com.cmpt276.project.walkinggroupapp.model.User;
import com.cmpt276.project.walkinggroupapp.proxy.ProxyBuilder;

import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

//    private static final String PREFERENCE_EMAIL = "saved.email.key";
//    public static final String INTENT_TOKEN = "com.cmpt276.project.walkinggroupapp.intentToken";
    private static final String PREFERENCE_IS_LOGOUT = "saved.logout.key";

    private Button btnAddNewMonitorsUser;
    private Button btnAddNewMonitoredByUser;
    private Button mLogoutButton;

    private ListView monitorsUsersListView;
    private ListView monitoredByUsersListView;

    private List<User> monitorsUsers;
    private List<User> monitoredByUsers;


//    private int selectedPosition;
//    private WGServerProxy proxy;
//    private User userLocal;
//    private String token;




    private ModelManager modelManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        modelManager = ModelManager.getInstance();


        ProxyBuilder.SimpleCallback<List<User>> getMonitorsUsersCallback = monitorsUsers -> getMonitorsUsersResponse(monitorsUsers);
        modelManager.getMonitorsUsers(MainMenuActivity.this, getMonitorsUsersCallback);

        ProxyBuilder.SimpleCallback<List<User>> getMonitoredByUsersCallback = monitoredByUsers -> getMonitoredByUsersResponse(monitoredByUsers);
        modelManager.getMonitoredByUsers(MainMenuActivity.this, getMonitoredByUsersCallback);


//        extractDataFromIntent();
//        createUser();
        setupAddNewMonitorsUserButton();
        setupAddNewMonitoredByUserButton();
        setupLogoutButton();
//        registerMonitorsUsersOnItemClick();
//        registerMonitoredByUsersOnItemClick();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // TODO
//        createUser();
        ProxyBuilder.SimpleCallback<List<User>> getMonitorsUsersCallback = monitorsUsers -> getMonitorsUsersResponse(monitorsUsers);
        modelManager.getMonitorsUsers(MainMenuActivity.this, getMonitorsUsersCallback);

        ProxyBuilder.SimpleCallback<List<User>> getMonitoredByUsersCallback = monitoredByUsers -> getMonitoredByUsersResponse(monitoredByUsers);
        modelManager.getMonitoredByUsers(MainMenuActivity.this, getMonitoredByUsersCallback);
    }

    private void setupLogoutButton() {
        mLogoutButton = findViewById(R.id.gerry_Logout_Button_main);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                //set the Logout string to show user logged out
                editor.putString(PREFERENCE_IS_LOGOUT, "true");

                //commit to preference
                editor.commit();


                //go to LoginActivity
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }

    private void setupAddNewMonitorsUserButton() {
        //register button
        btnAddNewMonitorsUser = findViewById(R.id.jacky_add_monitoring_button);
        btnAddNewMonitorsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddMonitorsUserActivity.makeIntent(getApplicationContext());
                startActivity(intent);
            }
        });
    }

    private void setupAddNewMonitoredByUserButton(){
        btnAddNewMonitoredByUser = findViewById(R.id.jacky_add_monitoring_by_button);
        btnAddNewMonitoredByUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddMonitoredByUserActivity.makeIntent(getApplicationContext());
                startActivity(intent);
            }
        });
    }

//    private void createUser() {
//        proxy = ProxyBuilder.getProxy(getString(R.string.gerry_apikey), token);
//        String email = getSavedEmail();
//        Call<User> caller = proxy.getUserByEmail(email);
//        ProxyBuilder.callProxy(MainMenuActivity.this, caller, returnedUser -> response(returnedUser));
//    }

//    private void response(User user) {
//        Log.i("MyApp", "Server replied with user: " + user.toString());
//        userLocal = user;
//
//        Call<List<User>> caller = proxy.getMonitorsUsersById(userLocal.getId());
//        ProxyBuilder.callProxy(MainMenuActivity.this, caller, monitorsUsers -> getMonitorsUsersResponse(monitorsUsers));
//
//        Call<List<User>> newCaller = proxy.getMonitoredByUsersById(userLocal.getId());
//        ProxyBuilder.callProxy(MainMenuActivity.this, newCaller, monitoredByUsers -> getMonitoredByUsersResponse(monitoredByUsers));
//    }

    private void getMonitorsUsersResponse(List<User> monitorsUsers) {
        Log.i("MyApp","Inside update you");
        this.monitorsUsers = monitorsUsers;
        populateMonitorsUsersList();
        registerMonitorsUsersOnItemClick();
    }

    private void populateMonitorsUsersList() {
        ArrayAdapter<User> adapter = new monitorsUsersAdapter();
        //Configure ListView
        monitorsUsersListView = findViewById(R.id.jacky_monitoring_list);
        monitorsUsersListView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "Done Populating List", Toast.LENGTH_LONG).show();
    }

    private class monitorsUsersAdapter extends ArrayAdapter<User> {                                                 //Code for complexList based from Brian Frasers video
        public monitorsUsersAdapter() {
            super(MainMenuActivity.this, R.layout.list_layout, monitorsUsers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure We are given a view
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_layout, parent, false);
            }
            //Find a user to add
            User currentUser = monitorsUsers.get(position);

            //Name:
            TextView makeName = itemView.findViewById(R.id.jacky_user_name_dynamic);
            makeName.setText(currentUser.getName());

            //Email
            TextView makeEmail = itemView.findViewById(R.id.jacky_user_email_dynamic);
            makeEmail.setText(currentUser.getEmail());


            return itemView;
        }
    }

    private void getMonitoredByUsersResponse(List<User> monitoredByUsers) {
        Log.i("MyApp", "How many times CALLED???");
        this.monitoredByUsers = monitoredByUsers;
        populateMonitoredByUsersList();
        registerMonitoredByUsersOnItemClick();
    }

    private void populateMonitoredByUsersList() {
        ArrayAdapter<User> adapter = new monitoredByUsersAdapter();
        //Configure ListView
        monitoredByUsersListView = findViewById(R.id.jacky_monitoing_by_list);
        monitoredByUsersListView.setAdapter(adapter);
        Toast.makeText(getApplicationContext(), "Done Populating List", Toast.LENGTH_LONG).show();
    }

    private class monitoredByUsersAdapter extends ArrayAdapter<User> {                                                 //Code for complexList based from Brian Frasers video
        public monitoredByUsersAdapter() {
            super(MainMenuActivity.this, R.layout.list_layout, monitoredByUsers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Make sure We are given a view
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_layout, parent, false);
            }
            //Find a user to add
            Log.i("MyApp", "Inside Monitoring By");
            User currentUser = monitoredByUsers.get(position);

            //Name:
            TextView makeName = itemView.findViewById(R.id.jacky_user_name_dynamic);
            makeName.setText(currentUser.getName());

            //Email
            TextView makeEmail = itemView.findViewById(R.id.jacky_user_email_dynamic);
            makeEmail.setText(currentUser.getEmail());

            return itemView;
        }
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, MainMenuActivity.class);
    }

//    public static Intent makeIntent(Context context, String token){
//        Intent intent = new Intent(context, MainMenuActivity.class);
//        intent.putExtra(INTENT_TOKEN, token);
//        return intent;
//    }

//    private void extractDataFromIntent(){
//        Intent intent = getIntent();
//        token = intent.getStringExtra(INTENT_TOKEN);
//    }

//    private String getSavedEmail()
//    {
//        SharedPreferences saveEmail= getSharedPreferences("MyData", MODE_PRIVATE);
//        return saveEmail.getString(PREFERENCE_EMAIL, "0");
//    }

    private void registerMonitorsUsersOnItemClick()                                                                                    //For clicking on list object
    {
        final ListView list = findViewById(R.id.jacky_monitoring_list);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View viewClicked, int position, long id)
            {
                //Toast.makeText(getApplicationContext(), "Pressed Long to edit" + position, Toast.LENGTH_SHORT).show();
                Log.i("MyApp", "Pressed Long" + position);
//                selectedPosition = position;
                PopupMenu popupMenu = new PopupMenu(MainMenuActivity.this, viewClicked);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {          //Code from https://www.youtube.com/watch?v=LXUDqGaToe0
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch(menuItem.getItemId())
                        {
                            case R.id.cancel:
//                                doCancel();
                                break;
                            case R.id.delete:
                                removeMonitorsUserByPosition(position);
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

    private void registerMonitoredByUsersOnItemClick()                                                                                    //For clicking on list object
    {
        final ListView list = findViewById(R.id.jacky_monitoing_by_list);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View viewClicked, int position, long id)
            {
                //Toast.makeText(getApplicationContext(), "Pressed Long to edit" + position, Toast.LENGTH_SHORT).show();
                Log.i("MyApp", "Pressed Long" + position);
//                selectedPosition = position;
                PopupMenu popupMenu = new PopupMenu(MainMenuActivity.this, viewClicked);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {          //Code from https://www.youtube.com/watch?v=LXUDqGaToe0
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch(menuItem.getItemId())
                        {
                            case R.id.cancel:
//                                doCancel();
                                break;
                            case R.id.delete:
                                removeMonitoredByUserByPosition(position);
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


//    // TODO: not necessary.
//    private void doCancel()
//    {
//        //Do nothing XD
//    }

    private void removeMonitorsUserByPosition(int position)
    {
        long targetId = monitorsUsers.get(position).getId();
        ProxyBuilder.SimpleCallback<Void> callback = returnNothing -> removeMonitorsUserResponse(returnNothing);
        modelManager.removeMonitorsUser(MainMenuActivity.this, callback, targetId);
//        User tempUser = monitorsUsers.get(position);
//        Call<Void> caller = proxy.removeMonitorsUser(userLocal.getId(), tempUser.getId());
//        ProxyBuilder.callProxy(MainMenuActivity.this, caller, noResponse -> redrawMonitorUser(noResponse));
    }

    private void removeMonitorsUserResponse(Void returnNothing) {
        ProxyBuilder.SimpleCallback<List<User>> getMonitorsUsersCallback = monitorsUsers -> getMonitorsUsersResponse(monitorsUsers);
        modelManager.getMonitorsUsers(MainMenuActivity.this, getMonitorsUsersCallback);
    }

    private void removeMonitoredByUserByPosition(int position)
    {
        long targetId = monitoredByUsers.get(position).getId();
        ProxyBuilder.SimpleCallback<Void> callback = returnNothing -> removeMonitoredByUserResponse(returnNothing);
        modelManager.removeMonitoredByUser(MainMenuActivity.this, callback, targetId);
//        User tempUser = monitoredByUsers.get(position);
//        Call<Void> caller = proxy.removeMonitoredByUser(userLocal.getId(), tempUser.getId());
//        ProxyBuilder.callProxy(MainMenuActivity.this, caller, noResponse -> redrawMonitorUser(noResponse));
    }

    private void removeMonitoredByUserResponse(Void returnNothing) {
        ProxyBuilder.SimpleCallback<List<User>> getMonitoredByUsersCallback = monitoredByUsers -> getMonitoredByUsersResponse(monitoredByUsers);
        modelManager.getMonitoredByUsers(MainMenuActivity.this, getMonitoredByUsersCallback);
    }

//    private void redrawMonitorUser(Void nothing)
//    {
//        Log.i("MyApp", "Removed USER");
////        createUser();
//    }

}