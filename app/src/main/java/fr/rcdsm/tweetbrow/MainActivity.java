package fr.rcdsm.tweetbrow;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ListView listview;
    FloatingActionButton fab;

    ArrayList<Tweet> allNotes;
    TweetManager manager;
    TweetAdapter mListAdapter;
    Menu MainMenu;

    ArrayAdapter<String> searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.getInstance();

        SharedPreferences preferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = preferences.getString("token", null);
        user.setToken(token);

        if(user.isNotConnected()){

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

            finish();
        }
        else
            Log.d("User instance","Login: "+user.getLogin()+" - Token: "+user.getToken());

        setContentView(R.layout.activity_main);

        manager = new TweetManager(this);
        ClientAPI.createInstance(this.getApplicationContext());

        // Listviewconstruct
        listview = (ListView) findViewById(R.id.listview);

        refreshListView();

        // Floating button construct
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(listview);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Reply.class);
                intent.putExtra("action", "add");
                intent.putExtra("login", User.getInstance().getLogin());
                intent.putExtra("pseudo",User.getInstance().getPseudo());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        refreshListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MainMenu = menu;

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String query)
            {
                TweetAdapter noteAdapter = new TweetAdapter(getApplicationContext(), manager.searchTweetOnList(query));
                listview.setAdapter(noteAdapter);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                TweetAdapter noteAdapter = new TweetAdapter(getApplicationContext(), manager.searchTweetOnList(query));
                listview.setAdapter(noteAdapter);
                return true;
            }
        };
        searchView.setOnQueryTextListener(textChangeListener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent intent = new Intent(MainActivity.this, TweetView.class);
        intent.putExtra("action", "view");
        intent.putExtra("id", id);

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_refresh) {
            refreshListView();
        }
        else if(id == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshListView(){

        ClientAPI.getInstance().syncTweetbrowAPI(new ClientAPI.APIListener() {
            @Override
            public void callback() {
                allNotes = manager.listTweets();
                TweetAdapter adapter = new TweetAdapter(MainActivity.this, allNotes);

                listview.setOnItemClickListener(MainActivity.this);
                listview.setAdapter(adapter);
                listview.setTextFilterEnabled(true);
            }
        });

    }

    private void logout(){

        ClientAPI.getInstance().logout(new ClientAPI.APIListener() {
            @Override
            public void callback() {}
        });

        SharedPreferences preferences = this.getApplicationContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        User.getInstance().disconnect();

        manager.deleteAll();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
