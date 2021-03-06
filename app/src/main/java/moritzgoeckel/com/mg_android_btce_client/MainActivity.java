package moritzgoeckel.com.mg_android_btce_client;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import moritzgoeckel.com.mg_android_btce_client.Client.AsyncBtcApi;
import moritzgoeckel.com.mg_android_btce_client.Client.GlobalData;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlobalData.MainActivity = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        GlobalData.API = new AsyncBtcApi("121B9FF2-KZEAGDM9-DPO0PBAN-UVISX7YV-QJWRSAV1", "5a1446dca2af336dbffcf00e2ff290386960421fee7a152c71c16aaf290847f4", this);

        requestData();
    }

    long lastUpdateTimeStamp = 0;
    private void requestData() {
        if(lastUpdateTimeStamp < System.currentTimeMillis() - 30 * 1000) {
            lastUpdateTimeStamp = System.currentTimeMillis();

            Toast.makeText(getBaseContext(), "Updating ...", Toast.LENGTH_SHORT).show();

            GlobalData.API.requestAccountData();
            GlobalData.API.requestHistoryData();
            GlobalData.API.requestOpenOrdersData();
            GlobalData.API.requestPairData("btc_usd");
            GlobalData.API.requestPairData("ltc_usd");
            GlobalData.API.requestPairData("nmc_usd");
            GlobalData.API.requestPairData("nvc_usd");
            GlobalData.API.requestPairData("eur_usd"); //Todo: Liste nicht vollständig
            GlobalData.API.requestPairData("rur_usd");
            GlobalData.API.requestPairData("ppc_usd");
        }
        else
        {
            Toast.makeText(getBaseContext(), "Wait 30s", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position){
            case 0: startFragment(new FragmentAccount()); break;
            case 1: startFragment(new FragmentOrders()); break;
            case 2: startFragment(new FragmentHistory()); break;
            case 3: startFragment(new FragmentPair()); break;
        }
    }

    public void startFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void setTitle(String newTitle) {
        getSupportActionBar().setTitle(newTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {

            requestData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
