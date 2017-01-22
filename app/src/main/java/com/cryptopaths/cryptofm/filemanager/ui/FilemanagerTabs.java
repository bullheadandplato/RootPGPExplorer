package com.cryptopaths.cryptofm.filemanager.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.FragmentCallbacks;
import com.cryptopaths.cryptofm.filemanager.PagerAdapter;
import com.cryptopaths.cryptofm.filemanager.SharedData;
import com.cryptopaths.cryptofm.filemanager.UiUtils;
import com.cryptopaths.cryptofm.filemanager.listview.AdapterCallbacks;
import com.cryptopaths.cryptofm.utils.FileUtils;

public class FilemanagerTabs extends AppCompatActivity implements AdapterCallbacks, FragmentCallbacks{
    private boolean                 isEmptyFolder=false;
    private TabsFragmentOne         mCurrentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filemanager_tabs);

        setToolbar();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.items_view_menu_item){

            }else{


            }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(isEmptyFolder) {
            removeNoFilesFragment();
        }
        String path=mCurrentFragment.getmCurrentPath();
        if(mCurrentFragment.getmCurrentPath().equals(SharedData.FILES_ROOT_DIRECTORY)){
            super.onBackPressed();
        }else{
            path		   = path.substring(0,path.lastIndexOf('/'));
            path 		   = path.substring(0,path.lastIndexOf('/')+1);
            FileUtils.CURRENT_PATH = path;
            mCurrentFragment.changeDirectory(path);
        }

    }

    @Override
    public void init() {

    }

    @Override
    public void finishActionMode() {
        Log.d("osama", "finishActionMode: Finishing action mode");
        if(this.actionMode!=null){
            Log.d("osama", "finishActionMode: Cannot finish activity");
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

    private void setToolbar(){
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
       Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Yoo");
       setSupportActionBar(toolbar);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentFragment=adapter.getCurrentFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * adapter callbacks section starting
     */

    ActionMode actionMode;
    @Override
    public void onLongClick() {
        //actionMode = startActionMode(new ActionViewHandler(this));
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
        if(path.equals(SharedData.FILES_ROOT_DIRECTORY)){
            path="Home";
        }else{
            path=path.substring(0,path.lastIndexOf('/'));
            path=path.substring(path.lastIndexOf('/')+1);
        }
        assert getSupportActionBar()!=null;
        getSupportActionBar().setTitle(path);
    }

    @Override
    public void showNoFilesFragment() {
        Log.d("google", "showNoFilesFragment: no files show");
        isEmptyFolder=true;
        FrameLayout layout=(FrameLayout)findViewById(R.id.no_files_frame_fragment);
        View view= getLayoutInflater().inflate(R.layout.no_files_layout,null);
        layout.addView(view);
    }

    @Override
    public void removeNoFilesFragment() {
        isEmptyFolder=false;
        FrameLayout layout=(FrameLayout)findViewById(R.id.no_files_frame_fragment);
        layout.removeAllViews();
    }
}
