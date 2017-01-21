package com.cryptopaths.cryptofm.filemanager.ui;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.ActionViewHandler;
import com.cryptopaths.cryptofm.filemanager.FragmentCallbacks;
import com.cryptopaths.cryptofm.filemanager.PagerAdapter;
import com.cryptopaths.cryptofm.filemanager.SharedData;
import com.cryptopaths.cryptofm.filemanager.UiUtils;
import com.cryptopaths.cryptofm.filemanager.listview.AdapterCallbacks;
import com.cryptopaths.cryptofm.filemanager.listview.FileFillerWrapper;
import com.cryptopaths.cryptofm.filemanager.listview.FileListAdapter;
import com.cryptopaths.cryptofm.filemanager.listview.FileSelectionManagement;
import com.cryptopaths.cryptofm.filemanager.listview.RecyclerViewSwipeHandler;
import com.cryptopaths.cryptofm.utils.FileUtils;

public class FilemanagerTabs extends AppCompatActivity implements AdapterCallbacks, FragmentCallbacks{
    private FileListAdapter         mFileAdapter;
    private RecyclerView            mRecyclerView;
    private LinearLayoutManager     mLinearLayoutManager;
    private GridLayoutManager       mGridLayoutManager;
    private ItemTouchHelper         mHelper;
    private FileSelectionManagement mManager;
    private String                  mCurrentPath;
    private boolean                 isEmptyFolder=false;
    private NoFilesFragment         mNoFilesFragment;

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
            if(mRecyclerView.getLayoutManager()==mGridLayoutManager){
                item.setIcon(getDrawable(R.drawable.ic_grid_view));
                if(mLinearLayoutManager==null){
                    mLinearLayoutManager=new LinearLayoutManager(this);
                }
                mHelper.attachToRecyclerView(mRecyclerView);
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
            }else{
                item.setIcon(getDrawable(R.drawable.ic_items_view));
                if(mGridLayoutManager==null){
                    mGridLayoutManager=new GridLayoutManager(this,2);
                }
                mHelper.attachToRecyclerView(null);
                mRecyclerView.setLayoutManager(mGridLayoutManager);

            }
            mRecyclerView.requestLayout();
        }
        return true;
    }

    public void init(){
        mRecyclerView=(RecyclerView)findViewById(R.id.fragment_recycler_view);
        mLinearLayoutManager=new LinearLayoutManager(this);
        mGridLayoutManager=new GridLayoutManager(this,2);
        mFileAdapter= SharedData.getInstance().getFileListAdapter(this);
        mManager=SharedData.getInstance().getmFileSelectionManagement(this);
        mCurrentPath= Environment.getExternalStorageDirectory().getPath()+"/";
        mHelper=new ItemTouchHelper(new RecyclerViewSwipeHandler(this));

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        FileFillerWrapper.fillData(mCurrentPath,this);
        mRecyclerView.setAdapter(mFileAdapter);

    }
    void changeDirectory(String path) {
        changeTitle(path);
        Log.d("filesc","current path: "+path);
        FileFillerWrapper.fillData(path,this);
        if(FileFillerWrapper.getTotalFilesCount()<1){
            showNoFilesFragment();
            return;
        }else if(isEmptyFolder){
            removeNoFilesFragment();
        }
        mFileAdapter.notifyDataSetChanged();

    }
    @Override
    public void onBackPressed() {
        if(isEmptyFolder){
            removeNoFilesFragment();
        }
        mCurrentPath = FileUtils.CURRENT_PATH;
        if(mCurrentPath.equals(SharedData.FILES_ROOT_DIRECTORY)){
            SharedData.ALREADY_INSTANTIATED=false;
            super.onBackPressed();
        }else{
            //modify the mCurrentPath
            mCurrentPath		   = mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/'));
            mCurrentPath 		   = mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/')+1);
            FileUtils.CURRENT_PATH = mCurrentPath;
            changeDirectory(mCurrentPath);

        }
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
    }

    /**
     * adapter callbacks section starting
     */

    ActionMode actionMode;
    @Override
    public void onLongClick() {
        actionMode = startActionMode(new ActionViewHandler(this));
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
