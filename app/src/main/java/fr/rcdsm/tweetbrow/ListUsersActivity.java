package fr.rcdsm.tweetbrow;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by rcdsm on 20/05/15.
 */
public class ListUsersActivity extends ActionBarActivity {

    ListView listview;

    ArrayList<User> allUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_users);

        // Listviewconstruct
        listview = (ListView) findViewById(R.id.listviewUsers);

        refreshListView();

    }
    @Override
    protected void onResume(){
        super.onResume();
        refreshListView();
    }

    private void refreshListView(){

        ClientAPI.getInstance().syncAllUsers(new ClientAPI.APIListener() {
            @Override
            public void callback() {

                TweetManager manager = new TweetManager(ListUsersActivity.this);
                allUsers = manager.listAllUsers();

                ListUsersAdapter adapter = new ListUsersAdapter(ListUsersActivity.this, allUsers);

                listview.setAdapter(adapter);
                listview.setTextFilterEnabled(true);
            }
        });

    }

}
