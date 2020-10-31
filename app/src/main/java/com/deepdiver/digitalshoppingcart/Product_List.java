package com.deepdiver.digitalshoppingcart;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.deepdiver.digitalshoppingcart.data.database.OnSearchFromMenu;
import com.deepdiver.digitalshoppingcart.data.database.OncheckListListener;
import com.deepdiver.digitalshoppingcart.data.model.SessionManager;
import com.deepdiver.digitalshoppingcart.data.model.ShoppingListAdapter;
import com.deepdiver.digitalshoppingcart.ui.home.HomeFragment;
import com.deepdiver.digitalshoppingcart.ui.login.LoginActivity;
import com.deepdiver.digitalshoppingcart.ui.slideshow.SlideshowFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Product_List extends AppCompatActivity implements OncheckListListener {

    private SlideshowFragment slideshowFragment = new SlideshowFragment();
    public HomeFragment homeFragment = new HomeFragment();
    private AppBarConfiguration mAppBarConfiguration;
    private RecyclerView recyclerView;
    public OnSearchFromMenu searchFromMenu;
    public MenuItem mySearchMenuItem;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        recyclerView = findViewById(R.id.shopping_list_recycler_view);

       /* navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.bar_code_scanner){

                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });*/

        BottomNavigationView bottomNavigationView = findViewById(R.id.btm_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.navigationscanbarcodes){

                    Toast.makeText(getApplicationContext(), "started barcode scanner", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Product_List.this, BarScanner.class));
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView user_header_view = navigationView.getHeaderView(0).findViewById(R.id.u_header_view);
        TextView e_header_view = navigationView.getHeaderView(0).findViewById(R.id.e_header_view);

        String uStr = getSharedPreferences("login_session", MODE_PRIVATE).getString("username", "Your name");
        String eStr = getSharedPreferences("login_session", MODE_PRIVATE).getString("email", "Yourname@host");

        if(uStr != null && eStr != null){

            user_header_view.setText(uStr);
            e_header_view.setText(eStr);
        }else{

            user_header_view.setText("");
            e_header_view.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product__list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.app_bar_search) {

            mySearchMenuItem = item;

            SearchView searchView1 = (SearchView) mySearchMenuItem.getActionView();

              homeFragment.setSearchListener(searchView1, getApplicationContext());
        }

        if (item.getItemId() == R.id.action_settings) {

            Toast.makeText(this, "setting menu item selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Product_List.this, SettingsActivity.class));

        } else if (item.getItemId() == R.id.enter_as_admin) {
            Toast.makeText(this, "admin meu item selected", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Product_List.this, Admin.class));
        } else {
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!new SessionManager(this).retriveSession()){
            Intent intent = new Intent(Product_List.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void pro_name_checked(int n) {

        slideshowFragment.checkItemAddedToCart(n,getApplicationContext());
    }

   
}