package com.gujjubites;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter adapterEng;
    private RecyclerView.Adapter adapterGuj;
    private RecyclerView.Adapter adapterEvents;
    private RecyclerViewPager mRecyclerView;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    private List<addNews> allNews = null;
    private List<addNews> addAd = null;
    private List<newEvents> events = null;
    private List<addNews> newsShowing = null;
    private DatabaseReference mRef;
    private DatabaseReference mRefEvents;
    private addNews an;
    private boolean initial_data_loaded = false;
    private boolean refresh_first_time = true;
    private List<String> bookmarks_key = null;
    private BookMarksDatabase db = new BookMarksDatabase(this);
    private List<BookMarks> bookMarks;
    private Menu menu;
    private int prefLanguage = 1;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(android.graphics.Color.WHITE);
        getSupportActionBar().hide();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains("PrefLan")) {
            if (sharedPreferences.getString("PrefLan", null).equals("English"))
                prefLanguage = 1;
            if (sharedPreferences.getString("PrefLan", null).equals("Gujarati"))
                prefLanguage = 2;
        } else
            changeSettings();

        allNews = new ArrayList<>();
        addAd = new ArrayList<>();
        newsShowing = new ArrayList<>();
        bookmarks_key = new ArrayList<>();
        bookMarks = new ArrayList<>();
        events = new ArrayList<>();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mRef = FirebaseDatabase.getInstance().getReference().child("approved");
        mRef.keepSynced(true);
        mRecyclerView = (RecyclerViewPager) findViewById(R.id.list);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layout);
        mRef.limitToLast(200).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allNews.clear();
                newsShowing.clear();
                bookmarks_key.clear();

                for (DataSnapshot addNewsSnapshot : dataSnapshot.getChildren()) {
                    an = addNewsSnapshot.getValue(addNews.class);
                    allNews.add(0, an);
                    bookmarks_key.add(0, addNewsSnapshot.getKey());
                }

                DatabaseReference mRefAd = FirebaseDatabase.getInstance().getReference().child("approvedad");
                mRefAd.keepSynced(true);
                mRefAd.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        addAd.clear();
                        for (DataSnapshot addNewsSnapshot : dataSnapshot.getChildren()) {

                            an = addNewsSnapshot.getValue(addNews.class);
                            addAd.add(0, an);
                        }
                        int j = 0;
                        for (int i = 3; i < allNews.size(); i += 3) {
                            if (addAd.size() > j) {
                                allNews.add(i, addAd.get(j));
                                bookmarks_key.add(i, "None");
                                j++;
                            } else {
                                allNews.add(i, addAd.get(j-1));
                                bookmarks_key.add(i, "None");
                            }
                        }
                        for (int i = 0; i < 5; i++) {
                            newsShowing.add(i, allNews.get(i));
                        }
                        adapterEng = new CustomSwiperAdapterEng(MainActivity.this, newsShowing);
                        adapterGuj = new CustomSwiperAdapterGuj(MainActivity.this, newsShowing);
                        if (prefLanguage == 1)
                            mRecyclerView.setAdapter(adapterEng);
                        else
                            mRecyclerView.setAdapter(adapterGuj);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mRefEvents = FirebaseDatabase.getInstance().getReference().child("approvedevent");
        mRefEvents.keepSynced(true);
        mRefEvents.limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot newEventsSnapshot : dataSnapshot.getChildren()) {
                    events.add(0,newEventsSnapshot.getValue(newEvents.class));

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //to check whether the pages are bookmarked or not, as user scroll
        mRecyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int i, int i1) {

                if (db.getBookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition())))
                    menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_blue_24dp);

                else
                    menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_white_24dp);

                if ((newsShowing.size() - i1) < 5) {
                    if (newsShowing.size() != allNews.size()) {
                        newsShowing.add(newsShowing.size(), allNews.get(newsShowing.size()));

                    }
                    adapterEng.notifyDataSetChanged();
                    adapterGuj.notifyDataSetChanged();
                }
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (refresh_first_time) {
                    mRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (initial_data_loaded) {
                                an = dataSnapshot.getValue(addNews.class);
                                newsShowing.add(0, an);
                                bookmarks_key.add(0, dataSnapshot.getKey());
                            }
                            refresh_first_time = false;
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    initial_data_loaded = true;
                    adapterEng.notifyDataSetChanged();
                    adapterGuj.notifyDataSetChanged();
                }
                mySwipeRefreshLayout.setRefreshing(false);

            }
        });

        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                return true;
            }

        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    if (getSupportActionBar().isShowing())
                        getSupportActionBar().hide();
                    else
                        getSupportActionBar().show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_top:
                if (mRecyclerView.getCurrentPosition() > 10)
                    mRecyclerView.scrollToPosition(10);
                mRecyclerView.smoothScrollToPosition(0);
                //add the function to perform here
                return (true);
            case R.id.action_bookmark:
                addBookMarks();
                //add the function to perform here
                return (true);
            case R.id.action_share:
                shareIt();
                //add the function to perform here
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {

            adapterGuj = new CustomSwiperAdapterGuj(MainActivity.this, newsShowing);
            adapterEng = new CustomSwiperAdapterEng(MainActivity.this, newsShowing);
            if (prefLanguage == 1)
                mRecyclerView.setAdapter(adapterEng);
            else
                mRecyclerView.setAdapter(adapterGuj);
            // Handle the camera action
        } else if (id == R.id.bookmarks) {
            bookMarks = db.getAllBookMarks();
            if (bookMarks.size() <= 0) {
                Toast.makeText(getApplicationContext(), "No Bookmarks added till now", Toast.LENGTH_SHORT).show();
                item.setChecked(false);
            } else
                populateBookMarks();
        } else if (id == R.id.events) {

            loadEvents();
        }else if (id == R.id.settings) {

            changeSettings();
        }
        else if (id == R.id.about) {
            item.setChecked(false);
            showAbout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void shareIt() {
        //sharing implementation here
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String path = newsShowing.get(mRecyclerView.getCurrentPosition()).getImg_url();
        String title;
        String description;
        if (prefLanguage == 1) {
            title = newsShowing.get(mRecyclerView.getCurrentPosition()).getTitle_eng();
            description = newsShowing.get(mRecyclerView.getCurrentPosition()).getDescription_eng();
        } else {
            title = newsShowing.get(mRecyclerView.getCurrentPosition()).getTitle_guj();
            description = newsShowing.get(mRecyclerView.getCurrentPosition()).getDescription_guj();
        }

        sharingIntent.putExtra(Intent.EXTRA_TEXT, "A bite from Gujjubites"
                + "\n------------\n" + title + "\n--------------\n" + description + "\n-----------\n\n" + "download this app from https://play.google.com/apps/testing/com.gujjubites");
        startActivity(Intent.createChooser(sharingIntent, "Share News"));
    }

    private void showAbout() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.about_dialog);
        dialog.show();
    }

    private void addBookMarks() {
        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        if (db.getBookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition()))) {
            db.deleteBookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition()));
            Toast.makeText(getApplicationContext(), "News Removed from Bookmark", Toast.LENGTH_SHORT).show();
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_white_24dp);
            adapterGuj.notifyDataSetChanged();
            adapterEng.notifyDataSetChanged();
        } else {
            db.addBookMarks(new BookMarks(bookmarks_key.get(mRecyclerView.getCurrentPosition())));
            Toast.makeText(getApplicationContext(), "Book Mark Added", Toast.LENGTH_SHORT).show();
            menu.findItem(R.id.action_bookmark).setIcon(R.drawable.ic_bookmark_border_blue_24dp);
            adapterGuj.notifyDataSetChanged();
            adapterEng.notifyDataSetChanged();
        }
        // Reading all contacts
    }

    private void populateBookMarks() {
        //String s = "";
        //for(int i=0; i < bookMarks.size(); i++)
        //    s = s + " " + bookMarks.get(i).getKey();
        //Toast.makeText(getApplicationContext(), s,Toast.LENGTH_LONG).show();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        final List<addNews> newsBookMarks = new ArrayList<>();
        for (int i = 0; i < bookMarks.size(); i++) {
            mRef.child("approved").child(bookMarks.get(i).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //for (DataSnapshot addNewsSnapshot : dataSnapshot.getChildren()) {
                    an = dataSnapshot.getValue(addNews.class);
                    newsBookMarks.add(0, an);
                    //}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            if (prefLanguage == 1) {
                adapterEng = new CustomSwiperAdapterEng(MainActivity.this, newsBookMarks);
                mRecyclerView.setAdapter(adapterEng);
            } else {
                adapterGuj = new CustomSwiperAdapterGuj(MainActivity.this, newsBookMarks);
                mRecyclerView.setAdapter(adapterGuj);
            }

        }
    }

    public void changeSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String [] pref_languages = {"English", "Gujarati"};
        builder.setTitle("Choose Your Preferred Language");
        builder.setItems(pref_languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPreferences.Editor edit;
                switch (which) {
                    case 0:
                        if(prefLanguage == 1)
                            break;
                        else {
                            edit = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).edit();
                            edit.putString("PrefLan", "English");
                            edit.commit();
                            prefLanguage = 1;
                            mRecyclerView.setAdapter(adapterEng);
                            break;
                        }
                    case 1:
                        if(prefLanguage == 2)
                            break;
                        else {
                            edit = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE).edit();
                            edit.putString("PrefLan", "Gujarati");
                            edit.commit();
                            prefLanguage = 2;
                            mRecyclerView.setAdapter(adapterGuj);
                            break;
                        }
                }
            }
        });
        builder.show();
    }

    private void loadEvents() {
        adapterEvents = new CustomSwiperAdapterEvents(MainActivity.this, events);
        mRecyclerView.setAdapter(adapterEvents);
    }
}
