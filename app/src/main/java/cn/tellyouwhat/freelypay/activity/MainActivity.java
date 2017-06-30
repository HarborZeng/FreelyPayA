package cn.tellyouwhat.freelypay.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.stephentuso.welcome.WelcomeHelper;

import butterknife.ButterKnife;
import cn.tellyouwhat.freelypay.R;
import cn.tellyouwhat.freelypay.fragment.AboutFragment;
import cn.tellyouwhat.freelypay.fragment.PayFragment;
import cn.tellyouwhat.freelypay.util.AliPayUtil;
import cn.tellyouwhat.freelypay.util.PhoneInfoProvider;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_VIEW;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    WelcomeHelper welcomeScreen;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        synchronized (MainActivity.class) {
            gotoIntro(savedInstanceState);
            setContentView(R.layout.activity_main);
        }
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.container, new PayFragment(), "pay")
                .commitNow();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void gotoIntro(Bundle savedInstanceState) {
        welcomeScreen = new WelcomeHelper(this, IntroActivity.class);
        welcomeScreen.show(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        Fragment payFragment = manager.findFragmentByTag("pay");
        Fragment aboutFragment = manager.findFragmentByTag("about");

        switch (id) {
            case R.id.nav_scan:
                if (payFragment != null && payFragment.isAdded()) {
                    if (payFragment.isHidden()) {
                        transaction.show(payFragment);
                    }
                }
                if (aboutFragment == null) {
                    break;
                }
                transaction.hide(aboutFragment).commitNow();
                break;
            case R.id.nav_help:
                startActivity(new Intent(MainActivity.this,
                        IntroActivity.class));
                break;
            case R.id.nav_about:
                transaction.hide(payFragment);

                if (aboutFragment != null && aboutFragment.isAdded()) {
                    if (aboutFragment.isHidden()) {
                        transaction.show(aboutFragment);
                    }
                } else {
                    transaction.add(R.id.container, new AboutFragment(), "about");
                }
                transaction.commitNow();
                break;
            case R.id.nav_share:
                Intent intent = new Intent(ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,
                        "我正在使用支付助手，提现免手续费哦！\n" +
                                " http://coolapk.com/apk/cn.tellyouwhat.freelypay");
                intent.setType("text/plain");
                startActivity(intent);
                break;
            case R.id.nav_feedback:
                String model = Build.MODEL;
                Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO);
                sendEmailIntent.setData(Uri.parse("mailto:feedback@tellyouwhat.cn"));
                sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "来自" + model + "支付助手用户的反馈");
                sendEmailIntent.putExtra(Intent.EXTRA_TEXT,
                        "您好，\n\n\n\n\n\n" +
                                PhoneInfoProvider.getInstance()
                                        .getBuildInfo());
                startActivity(sendEmailIntent);
                break;
            default:
                return false;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeScreen.onSaveInstanceState(outState);
    }

    public void gotoAlipay(View view) {
        if (AliPayUtil.hasInstalledAlipayClient(this)) {
            AliPayUtil.startAlipayClient(this, "tsx009216jz1qtdaoiqcw10");
        } else {
            Toast.makeText(this, R.string.no_alipay_installed, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ACTION_VIEW,
                    Uri.parse("https://qr.alipay.com/tsx009216jz1qtdaoiqcw10")));
        }
    }
}
