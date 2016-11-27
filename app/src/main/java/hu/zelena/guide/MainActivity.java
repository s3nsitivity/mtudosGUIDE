package hu.zelena.guide;


import hu.zelena.guide.fragments.AboutFragment;
import hu.zelena.guide.fragments.InternetFragment;
import hu.zelena.guide.fragments.MagicbookFragment;
import hu.zelena.guide.fragments.OthersFragment;
import hu.zelena.guide.fragments.PhonesFragment;
import hu.zelena.guide.fragments.PostPaidFragment;
import hu.zelena.guide.fragments.PrePaidFragment;
import hu.zelena.guide.fragments.TabletFragment;
import hu.zelena.guide.fragments.WatchFragment;
import hu.zelena.guide.modell.Schedule;
import hu.zelena.guide.modell.Version;
import hu.zelena.guide.util.ActivityHelper;
import hu.zelena.guide.util.DownloadActivity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.support.design.widget.Snackbar;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


public class MainActivity extends AppCompatActivity {

	private String brand;

	String verName;

	private static final int RESULT_SETTINGS = 1;


    /**
	 *
	 * NavigationBar / Toolbar
     */
	private DrawerLayout mDrawer;
	private Toolbar toolbar;
	private  NavigationView nvDrawer;
	private ActionBarDrawerToggle drawerToggle;

	private boolean isDark = false;
	private boolean dataSaver = false;
    private boolean writeStorage = true;
    private boolean offlineMode=false;


    final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 0;

    @SuppressWarnings("ResourceType")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		/* Request user permissions in runtime */
		ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_STORAGE);

        /* Request user permissions in runtime */


		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if(sharedPrefs.getBoolean("darkMode", false)){
			setTheme(R.style.AppDarkTheme);
			isDark = true;
		}

		if(sharedPrefs.getBoolean("dataSaver", false)){
			dataSaver = true;
		}

        if(sharedPrefs.getBoolean("offline", false)){
            offlineMode = true;
        }

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActivityHelper.initialize(this);
        CheckVersion();

		// Toolbar az ActionBar helyére
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// DrawerLayout beálíltása
		mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = setupDrawerToggle();

		// Toogle beállítása
		mDrawer.addDrawerListener(drawerToggle);

		// DrawerView beállítása
		nvDrawer = (NavigationView) findViewById(R.id.nvView);
		setupDrawerContent(nvDrawer);

		Fragment fragment = new AboutFragment();
		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
            Intent i = new Intent(this, ErrorActivity.class);
            i.putExtra("darkMode",isDark);
            i.putExtra("error", "Nem sikerült a HOME-FRAGMENT betöltése. [Null object]");
            startActivity(i);

            Log.e("MainActivity", "Error in creating fragment"); }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		switch (item.getItemId()) {
			case android.R.id.home:
				mDrawer.openDrawer(GravityCompat.START);
				return true;
            case R.id.menu_settings:
                Intent i = new Intent(this, UserSettingsActivity.class);
				i.putExtra("darkMode", isDark);

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    writeStorage = true;

                } else {
                    writeStorage = false;
                }

                i.putExtra("writeStorage",writeStorage);
               	startActivityForResult(i, RESULT_SETTINGS);
                break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						selectDrawerItem(menuItem);
						return true;
					}
				});
	}

	public void selectDrawerItem(MenuItem menuItem) {
		Fragment fragment = null;
		Bundle bundle = new Bundle();

		switch(menuItem.getItemId()) {
			case R.id.alcatel_frag:
				fragment = new PhonesFragment();
				brand = "Alcatel";
				break;
			case R.id.apple_frag:
				brand = "Apple";
				break;
			case R.id.balckberry_frag:
				brand = "BlackBerry";
				break;
			case R.id.coolpad_frag:
				brand = "Coolpad";
				break;
			case R.id.honor_frag:
				brand = "Honor";
				break;
			case R.id.HTC_frag:
				brand = "HTC";
				break;
			case R.id.huawei_frag:
				brand = "Huawei";
				break;
			case R.id.lg_frag:
				brand = "LG";
				break;
			case R.id.microsoft_frag:
				brand = "Microsoft";
				break;
			case R.id.samsung_frag:
				brand = "Samsung";
				break;
			case R.id.sony_frag:
				brand = "Sony";
				break;
			case R.id.watch_frag:
				fragment = new WatchFragment();
				break;
			case R.id.tablet_frag:
				fragment = new TabletFragment();
                break;
			case R.id.postpaid_frag:
				fragment = new PostPaidFragment();
				break;
			case R.id.prepaid_frag:
				fragment = new PrePaidFragment();
				break;
			case R.id.net_frag:
				fragment = new InternetFragment();
				break;
			case R.id.magicbook_frag:
				fragment = new MagicbookFragment();
				break;
			case R.id.others_frag:
				fragment = new OthersFragment();
				break;
			default:
                break;
		}

		if (fragment != null) {
			bundle.putString("brand", brand);
			fragment.setArguments(bundle);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
		} else {
			Intent i = new Intent(this, ErrorActivity.class);
			i.putExtra("darkMode",isDark);
			i.putExtra("error", "Nem sikerült a FRAGMENT betöltése. [Null object]");
			startActivity(i);
			Log.e("MainActivity", "Error in creating fragment");
		}

		// Highlight the selected item has been done by NavigationView
		menuItem.setChecked(true);
		// Set action bar title
		setTitle(menuItem.getTitle());
		// Close the navigation drawer
		mDrawer.closeDrawers();
	}

	/**
	 * ActionBarDrawerToggle szinkronizáció
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// Sync the toggle state after onRestoreInstanceState has occurred.
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	private ActionBarDrawerToggle setupDrawerToggle() {
		return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
	}

    /**
     *
     * Engedély kérés lekezelése
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("REQUEST:","GRANTED");

                } else {
                    Log.d("REQUEST:","DENIED");
                    sendSnack("Offline mód letiltva.");
                }
                return;
            }
        }
    }

	/**
	 *  Publikus függvények / Metódusok
	 */

	public void sendSnack(String msg) {
		View Snack = findViewById(R.id.snack_container);

		Snackbar snackbar = Snackbar
				.make(Snack, msg, Snackbar.LENGTH_LONG);
		snackbar.show();
	}

	public boolean checkInternet(){
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void sendFeedback(View view) {
		String version = BuildConfig.VERSION_NAME;

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "zelena.patrikgergo@ext.telekom.hu" });
		intent.putExtra(Intent.EXTRA_SUBJECT, "Mobiltudós Guide - Visszajelző");
		intent.putExtra(Intent.EXTRA_TEXT, "Verzió: " + version);
		startActivity(Intent.createChooser(intent, ""));

	}

	public boolean getSaverMode(){
		return dataSaver;
	}

    public boolean getOfflineMode(){
        return offlineMode;
    }


    public void  startTutorial(View view){
        Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
        startActivity(intent);
    }

	/**
	 *
	 * Back gomb gombnyomásra történő NavBar zárás vagy kilépés
     */

	@Override
	public void onBackPressed() {
		if(mDrawer.isDrawerOpen(GravityCompat.START)){
			mDrawer.closeDrawers();
		} else finish();
	}


	/**
	 *  Menu Layoutok létrehozása
	 */

	public void aboutClick(MenuItem item) {
		Fragment fragment = new AboutFragment();

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            setTitle("Névjegy");
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

    public void changelogClick(MenuItem item) {
		Intent i = new Intent(this, DownloadActivity.class);
		startActivity(i);
    }

    /**
     * Verzió ellenőrzés (async)
     */

    private void VersionNotify(){

        NotificationManager manager;
        Notification myNotication;

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        String url = "http://users.iit.uni-miskolc.hu/~zelena5/work/telekom/mobiltud/version/current/guide.apk";
        intent.setData(Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(MainActivity.this);

        builder.setAutoCancel(false);
        builder.setTicker("Új verzió érhető el");
        builder.setContentTitle("Új verzió érhető el");
        builder.setContentText("Letöltéshez érintsd meg");
        builder.setSmallIcon(R.drawable.ic_stat_name);
        builder.setContentIntent(pendingIntent);
        builder.setSubText("Verzió változás");   //API level 16
        builder.setNumber(100);
        builder.build();
        builder.setAutoCancel(true);

        myNotication = builder.getNotification();
        manager.notify(11, myNotication);
	}

    private class HttpRequestVersion extends AsyncTask<Void, Void, Version> {
        @Override
        protected Version doInBackground(Void... params) {
            try {
                final String url = "http://users.iit.uni-miskolc.hu/~zelena5/work/telekom/mobiltud/version/current/ver";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Version ver= restTemplate.getForObject(url, Version.class);
                return ver;
            } catch (Exception e) {
                Log.e("VersionCheck", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Version ver) {
            verName = ver.getVersion();


            if (Integer.valueOf(verName) > Integer.valueOf(BuildConfig.VERSION_CODE)){
                VersionNotify();
            }
        }
    }


    private void CheckVersion(){

        if (checkInternet()) {
            new HttpRequestVersion().execute();
        }

    }

	/**
	 *
	 *   View clicekre iterakciók
     */



	public void magicbookClick(View view){
		Intent i = new Intent(this, WebViewActivity.class);
		i.putExtra("URL","http://magicbook.telekom.intra/");
		i.putExtra("title","Magic Book");
		startActivity(i);
	}

	public void phoneBookClick(View view){
		Intent i = new Intent(this, WebViewActivity.class);
		i.putExtra("URL","http://telefonkonyv.telekom.intra/applications/phonebook/");
		i.putExtra("title","Telefonkönyv");
		startActivity(i);
	}

	public void phoneListkClick(View view){
		Intent i = new Intent(this, WebViewActivity.class);
		i.putExtra("URL","https://docs.google.com/gview?embedded=true&url=http://www.telekom.hu/static-la/sw/file/Akcios_keszulek_arlista_kijelolt_dijcsomagokhoz.pdf");
		i.putExtra("title","Készülék árlista");
		startActivity(i);
	}

	public void repairListClick(View view){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String url = "http://magicbook.telekom.intra/mb/tmobile/tevekenysegek/jav_kesz_atv/gyartok_gyartoi_szervizek.pdf";
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	public void stickCompClick(View view){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String url = "http://magicbook.telekom.intra/mb/tmobile/tevekenysegek/jav_kesz_atv/Adateszkozok_operacios_rendszer_kompatibilitasa.pdf";
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	public void accessoryListClick(View view){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String url = "http://magicbook.telekom.intra/mb/tmobile/arlistak/tartozek_arlista.xls";
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

	public void rssRead(View view) {
		Intent i = new Intent(this, RssActivity.class);
        i.putExtra("darkMode",isDark);
		startActivity(i);
	}

	public void ussdCodeActivity(View view){
		Intent i = new Intent(this, UssdCodeActivity.class);
		i.putExtra("darkMode",isDark);
		startActivity(i);
	}

	public void emailCollectIn(View view){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String url ="http://ugyfelelmeny.telekom.intra/mobiltudik/Default.aspx";
		intent.setData(Uri.parse(url));
		startActivity(intent);

	}

	public void emailCollectOut(View view){
		Intent i = new Intent(this, WebViewActivity.class);
		i.putExtra("URL","http://www.telekom.hu/lakossagi/szolgaltatasok/mobiltudos");
		i.putExtra("title","Email gyűjtő - Public");
		startActivity(i);
	}

	public void workSchedules(View view){
		new HttpRequestSchedule().execute();
	}

	private class HttpRequestSchedule extends AsyncTask<Void, Void, Schedule> {


		@Override
		protected Schedule doInBackground(Void... params) {
			try {


				final String url = "http://users.iit.uni-miskolc.hu/~zelena5/work/telekom/mobiltud/schedule/current";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				Schedule sch = restTemplate.getForObject(url, Schedule.class);
				return sch;
			} catch (Exception e) {
				Log.e("Main Activity", e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Schedule sch) {
			Intent i = new Intent(MainActivity.this, WebViewActivity.class);
			i.putExtra("URL", sch.getSchedule());
			i.putExtra("title","Beosztás");
			startActivity(i);

		}
	}

    /**
	 *  Tablet Click-ek
	 */

	public void SpecAct(String no){

			Intent i = new Intent(MainActivity.this, SpecsAvtivity.class);
			i.putExtra("brand", "tablet");
			i.putExtra("phone", no);
			i.putExtra("darkMode", isDark);
			startActivity(i);
	}
	public void tab1Click(View view){
        SpecAct("1");
	}
	public void tab2Click(View view){
		SpecAct("2");
	}
	public void tab3Click(View view){
		SpecAct("3");
	}
	public void tab4Click(View view){
		SpecAct("4");
	}
	public void tab5Click(View view){
		SpecAct("5");
	}
	public void tab6Click(View view){
		SpecAct("6");
	}
}
