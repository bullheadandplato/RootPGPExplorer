package com.cryptopaths.cryptofm.filemanager.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cryptopaths.cryptofm.CryptoFM;
import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.listview.AdapterCallbacks;
import com.cryptopaths.cryptofm.filemanager.utils.ExternalStorageHandler;
import com.cryptopaths.cryptofm.filemanager.utils.FragmentCallbacks;
import com.cryptopaths.cryptofm.filemanager.utils.PagerAdapter;
import com.cryptopaths.cryptofm.filemanager.utils.SharedData;
import com.cryptopaths.cryptofm.filemanager.utils.UiUtils;
import com.cryptopaths.cryptofm.services.CleanupService;
import com.cryptopaths.cryptofm.utils.ActionHandler;
import com.cryptopaths.cryptofm.utils.FileDocumentUtils;
import com.cryptopaths.cryptofm.utils.FileUtils;

import java.io.File;

public class FilemanagerTabs extends AppCompatActivity implements AdapterCallbacks, FragmentCallbacks{
    private int                     mTotalStorages;
    private boolean                 isEmptyFolder=false;
    private TabsFragmentOne         mCurrentFragment;
    private String[]                mStorageTitles;
    private TabsFragmentOne[]       mFragmentOnes;
    private static final int        GET_PERMISSION_CODE=432;
    private static boolean          isServiceStarted=false;
    private static final String TAG=FilemanagerTabs.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filemanager_tabs);
        SharedData.STARTED_IN_SELECTION_MODE=false;
        SharedPreferences prefs=getSharedPreferences("done",Context.MODE_PRIVATE);
        SharedData.KEYS_GENERATED=prefs.getBoolean("keys_gen",false);

        if(!isServiceStarted){
            startService(new Intent(CryptoFM.getContext(),CleanupService.class));
            isServiceStarted=true;
        }
        //see the external dirs
        mStorageTitles=ExternalStorageHandler.getStorageDirectories(this);
        mTotalStorages=mStorageTitles.length;
        Log.d(TAG, "onCreate: total storages are: " +mTotalStorages);
        SharedData.DB_PASSWWORD 	= getIntent().getExtras().getString("dbpass");
        SharedData.USERNAME		    = getIntent().getExtras().getString("username","default");
        setToolbar();


    }
    @ActionHandler(layoutResource = R.id.fab_add_folder)
    public void onAddFloatingClicked(View v){
        UiUtils.actionMode = this.actionMode;
        if(mCurrentFragment==null){
            Log.d(TAG, "onAddFloatingClicked: fragment is null ");
            mCurrentFragment=mFragmentOnes[0];
        }
        FileUtils.CURRENT_PATH=mCurrentFragment.getmCurrentPath();
        String uri=null;

        Log.d(TAG, "onAddFloatingClicked: current path is: "+FileUtils.CURRENT_PATH);
        if(isEmptyFolder) {
            removeNoFilesFragment();
        }
        final Dialog dialog = UiUtils.createDialog(
                this,
                "Create Folder",
                "create"
        );

        final EditText folderEditText = (EditText)dialog.findViewById(R.id.foldername_edittext);
        Button okayButton			  = (Button)dialog.findViewById(R.id.create_file_button);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName=folderEditText.getText().toString();
                if(folderName.length()<1){
                    folderEditText.setError("Give me the folder name");
                }else{
                    if(FileUtils.isSdCardPath("")){
                        Log.d(TAG, "onClick: Yes nigga creating folder via document");

                        FileDocumentUtils.createFolder(FileUtils.CURRENT_PATH,folderName);
                        dialog.dismiss();UiUtils.reloadData(
                                FilemanagerTabs.this,
                                mCurrentFragment.getmFileAdapter()
                        );

                    }
                    else if(!FileUtils.createFolder(folderName)){
                        Toast.makeText(
                                FilemanagerTabs.this,
                                "Cannot create folder make sure current path is writable",
                                Toast.LENGTH_SHORT
                        ).show();
                    }else{
                        dialog.dismiss();
                        UiUtils.reloadData(
                                FilemanagerTabs.this,
                                mCurrentFragment.getmFileAdapter()
                        );
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(SharedData.IS_IN_COPY_MODE ){
            getMenuInflater().inflate(R.menu.copy_menu,menu);
            return true;
        }
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        Log.d(TAG, "onCreateOptionsMenu: menu created");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: preparing menu");
        if(SharedData.IS_IN_COPY_MODE){
            return super.onPrepareOptionsMenu(menu);
        }
        MenuItem item=menu.getItem(0);
        if(!getPreferences(Context.MODE_PRIVATE).getBoolean("layout",true)){
            item.setIcon(getDrawable(R.drawable.ic_grid_view));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //change the current path in fragment
        if(mCurrentFragment==null){
            mCurrentFragment=mFragmentOnes[0];
        }
        if(SharedData.IS_IN_COPY_MODE){
            if(item.getItemId()==R.id.cancel_menu_item){
                SharedData.IS_IN_COPY_MODE=false;
                invalidateOptionsMenu();
                openOptionsMenu();
            }else if(item.getItemId()==R.id.paste_here_menu_item){
                SharedData.IS_IN_COPY_MODE=false;
                invalidateOptionsMenu();
                openOptionsMenu();
                removeNoFilesFragment();
                mCurrentFragment.executeCopyTask();

            }
            return true;
        }
        mCurrentFragment.toggleLayout(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        //change the current path in fragment
        if(mCurrentFragment==null){
            mCurrentFragment=mFragmentOnes[0];
        }
        if(isEmptyFolder) {
            removeNoFilesFragment();
        }
        String path=mCurrentFragment.getmCurrentPath();
        Log.d(TAG, "onBackPressed: Current path is: "+path);
        if(path.equals(SharedData.FILES_ROOT_DIRECTORY)|| path.equals(SharedData.EXTERNAL_SDCARD_ROOT_PATH)){
            super.onBackPressed();
        }else{
            path		   = path.substring(0,path.lastIndexOf('/'));
            path 		   = path.substring(0,path.lastIndexOf('/')+1);
            FileUtils.CURRENT_PATH = path;
            Log.d(TAG, "onBackPressed: Changing directory");
            changeTitle(path);
            mCurrentFragment.changeDirectory(path);
        }

    }

    @Override
    public void init() {

    }

    @Override
    public void finishActionMode() {
        if(this.actionMode!=null){
            this.actionMode.finish();
            this.actionMode=null;
        }else {
            SharedData.SELECTION_MODE=false;
        }
    }

    @Override
    public void tellNoFiles() {
        showNoFilesFragment();
    }

    @Override
    public void setCurrentFragment(TabsFragmentOne m, int position) {
        Log.d(TAG, "setCurrentFragment: Setting fragments at position: "+position);
        mFragmentOnes[position]=m;
        Log.d(TAG, "setCurrentFragment: fragment at position: "+position+"has path: "+m.getmCurrentPath());
        mFragmentOnes[position].setmCurrentPath(mStorageTitles[position]);

    }

    private void setToolbar(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        toolbar.setSubtitle("/storage/emulated/0");
        setSupportActionBar(toolbar);
        if (mTotalStorages > 1 && getContentResolver().getPersistedUriPermissions().size() < 1) {
            Log.d(TAG, "setToolbar: found external sdcard");
            getExternalStoragePermissions();   
        }
        // set the external sdcard path
        if (mTotalStorages > 1) {
            Log.d(TAG, "setToolbar: yes there is an sdcard");
            SharedData.EXTERNAL_SDCARD_ROOT_PATH = mStorageTitles[0];
            SharedData.EXT_ROOT_URI = getPreferences(Context.MODE_PRIVATE).getString("tree_uri", null);
            FileUtils.CURRENT_PATH=SharedData.EXTERNAL_SDCARD_ROOT_PATH;
            toolbar.setSubtitle(mStorageTitles[0]);
        }
        final ViewPager viewPager  = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), mTotalStorages);
        mPagerAdapter.setTitles(mStorageTitles);
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        mFragmentOnes=new TabsFragmentOne[mTotalStorages];
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentFragment=mFragmentOnes[position];
                if(actionMode!=null){
                    actionMode.finish();
                    actionMode=null;
                }
                if((mCurrentFragment==null)){
                  return;
                }
                if(mCurrentFragment.ismIsEmptyFolder()){
                    showNoFilesFragment();

                }else{
                    removeNoFilesFragment();
                }

                FileUtils.CURRENT_PATH=mCurrentFragment.getmCurrentPath();
                changeTitle(mCurrentFragment.getmCurrentPath());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
    private void getExternalStoragePermissions(){
        Log.d(TAG, "getExternalStoragePermissions: Getting permissions");
        //inform user what to do
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent,GET_PERMISSION_CODE);
            }
        });
        dialog.setMessage("Looks like you have external sdcard. " +
                "Please choose it in next screen to enable read and write on it. " +
                "Otherwise I will not be able to use it");
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mStorageTitles[0]=mStorageTitles[1];
                Toast.makeText(FilemanagerTabs.this,
                        "I wont be able to perform any operation on external sdcard",
                        Toast.LENGTH_LONG
                ).show();

            }
        });
        dialog.setTitle("Need permission");
        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==GET_PERMISSION_CODE){
                Uri treeUri = data.getData();
                Log.d(TAG, "onActivityResult: tree uri is: "+treeUri);
                //save the uri for later use
                SharedPreferences prefs=getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=prefs.edit();
                editor.putString("tree_uri",treeUri.toString());
                editor.apply();
                editor.commit();

                // Check for the freshest data.
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
    }

    /**
     * adapter callbacks section starting
     */


    ActionMode actionMode;
    @Override
    public void onLongClick() {
        //change the current path in fragment
        if(mCurrentFragment==null){
            mCurrentFragment=mFragmentOnes[0];
        }
        actionMode = startActionMode(mCurrentFragment.getmActionViewHandler());
        UiUtils.actionMode=actionMode;
    }
    @Override
    public void incrementSelectionCount(){
        actionMode.setTitle(++SharedData.SELECT_COUNT+"");
        if(SharedData.SELECT_COUNT>1){
            actionMode.getMenu().removeItem(R.id.rename_menu_item);
        }
    }

    @Override
    public void decrementSelectionCount() {
        if(actionMode!=null){
            actionMode.setTitle(--SharedData.SELECT_COUNT+"");
            if(SharedData.SELECT_COUNT==0){
                actionMode.finish();
            }
            else if(SharedData.SELECT_COUNT<2){
                actionMode.getMenu().add(0,R.id.rename_menu_item,0,"rename");
            }
        }
    }

    @Override
    public void changeTitle(String path) {
        String tmp=path;
        //change the current path in fragment
        if(mCurrentFragment==null){
            mCurrentFragment=mFragmentOnes[0];
        }
        mCurrentFragment.setmCurrentPath(path);


        if(path.equals(SharedData.FILES_ROOT_DIRECTORY)){
            path="Home";
        }else{
            path=path.substring(0,path.lastIndexOf('/'));
            path=path.substring(path.lastIndexOf('/')+1);
        }
        assert getSupportActionBar()!=null;
        getSupportActionBar().setTitle(path);
        getSupportActionBar().setSubtitle(tmp);
    }

    @Override
    public void showNoFilesFragment() {
        Log.d("google", "showNoFilesFragment: no files show");
        isEmptyFolder=true;
        FrameLayout layout=(FrameLayout)findViewById(R.id.no_files_frame_fragment);
        View view= getLayoutInflater().inflate(R.layout.no_files_layout,null);
        layout.addView(view);
        mCurrentFragment.setmIsEmptyFolder(true);
    }

    @Override
    public void removeNoFilesFragment() {
        isEmptyFolder=false;
        mCurrentFragment.setmIsEmptyFolder(false);
        FrameLayout layout=(FrameLayout)findViewById(R.id.no_files_frame_fragment);
        layout.removeAllViews();
    }

    public void showCopyDialog() {
        actionMode.finish();
        invalidateOptionsMenu();
        openOptionsMenu();
        //change directory to root path of current fragment
        if(mCurrentFragment==null){
            mCurrentFragment=mFragmentOnes[0];
        }
        changeTitle(mCurrentFragment.getRootPath());
        mCurrentFragment.changeDirectory(mCurrentFragment.getRootPath());

    }
}
