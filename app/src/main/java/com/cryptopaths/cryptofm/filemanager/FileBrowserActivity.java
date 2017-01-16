package com.cryptopaths.cryptofm.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.services.CleanupService;
import com.cryptopaths.cryptofm.tasks.DecryptTask;
import com.cryptopaths.cryptofm.tasks.EncryptTask;
import com.cryptopaths.cryptofm.utils.ActionHandler;
import com.cryptopaths.cryptofm.utils.FileUtils;



public class FileBrowserActivity extends AppCompatActivity
		implements FileListAdapter.LongClickCallBack {

	private String 				mCurrentPath;
	private String 				mRootPath;
    private FileListAdapter 	mmFileListAdapter;
	private RecyclerView 		mFileListView;
	private LinearLayoutManager	mFileViewLinearLayoutManager;
	private GridLayoutManager	mFileViewGridLayoutManager;
    private static final String TAG = "FileBrowser";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_activity);
		setResult(RESULT_OK);

        SharedData.DB_PASSWWORD 	= getIntent().getExtras().getString("dbpass");
		SharedData.USERNAME		    = getIntent().getExtras().getString("username","default");
		mCurrentPath 	           	= Environment.getExternalStorageDirectory().getPath()+"/";
		mRootPath	 	           	= mCurrentPath;
		mFileListView				= (RecyclerView) findViewById(R.id.fileListView);
		mmFileListAdapter 			= SharedData.getInstance().getFileListAdapter(this);

		//set layout manager for the recycler view according to user choice
		SharedPreferences preferences   = getPreferences(Context.MODE_PRIVATE);
		boolean linearLayout           = preferences.getBoolean("key",false);
		if(linearLayout){
			mFileViewLinearLayoutManager=new LinearLayoutManager(this);
			mFileListView.setLayoutManager(mFileViewLinearLayoutManager);
		}else{
			mFileViewGridLayoutManager=new GridLayoutManager(this,2);
			mFileListView.setLayoutManager(mFileViewGridLayoutManager);
		}
//hello change
		FileFillerWrapper.fillData(mCurrentPath,this);
		mFileListView.setAdapter(mmFileListAdapter);


	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.appbar_menu,menu);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.items_view_menu_item){
			if(mFileListView.getLayoutManager()==mFileViewGridLayoutManager){
				item.setIcon(getDrawable(R.drawable.ic_grid_view));
				if(mFileViewLinearLayoutManager==null){
					mFileViewLinearLayoutManager=new LinearLayoutManager(this);
				}
				mFileListView.setLayoutManager(mFileViewLinearLayoutManager);
			}else{
				item.setIcon(getDrawable(R.drawable.ic_items_view));
				if(mFileViewGridLayoutManager==null){
					mFileViewGridLayoutManager=new GridLayoutManager(this,2);
				}
				mFileListView.setLayoutManager(mFileViewGridLayoutManager);
			}
			mFileListView.requestLayout();
		}
		return true;
	}

	@ActionHandler(layoutResource = R.id.floating_add)
	public void onAddFloatingClicked(View v){
        UiUtils.actionMode = this.actionMode;
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
					if(!FileUtils.createFolder(folderName)){
						Toast.makeText(
								FileBrowserActivity.this,
								"Folder name already exist",
								Toast.LENGTH_SHORT
						).show();
					}else{
						dialog.dismiss();
						UiUtils.reloadData(
								FileBrowserActivity.this,
								mmFileListAdapter
						);
					}
				}
			}
		});
	}
	void showCopyDialog(){
		CoordinatorLayout.LayoutParams layoutParams=new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,CoordinatorLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0,getResources().getDimensionPixelSize(R.dimen.recycler_view_margin_top),0,0);
		mFileListView.setLayoutParams(layoutParams);
		FrameLayout layout=(FrameLayout)findViewById(R.id.add_copy_file_dialog_layout);
		View view= getLayoutInflater().inflate(R.layout.copy_file_dialog_layout,null);
		layout.addView(view);

	}

	void changeDirectory(String path) {
		changeTitle(path);
		Log.d("files","current path: "+path);
        FileFillerWrapper.fillData(path,this);
		mmFileListAdapter.notifyDataSetChanged();

	}
	@ActionHandler(layoutResource = R.id.cancel_copy_button)
	public void onCancelButtonClicked(View v){
		CoordinatorLayout.LayoutParams layoutParams=new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,CoordinatorLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0,0,0,0);
		mFileListView.setLayoutParams(layoutParams);
		FrameLayout layout=(FrameLayout)findViewById(R.id.add_copy_file_dialog_layout);
		layout.removeAllViews();
	}
	@Override
	public void onBackPressed() {
		mCurrentPath = FileUtils.CURRENT_PATH;
		if(mCurrentPath.equals(mRootPath)){
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
	protected void onResume() {
		Log.d(TAG, "onResume: resuming activity");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause: pausing activity");
		super.onPause();
	}

    @Override
    protected void onDestroy() {
		startService(new Intent(this,CleanupService.class));
		Log.d(TAG, "onDestroy: destroying activity");
		DecryptTask decryptTask=SharedData.getInstance().getTaskHandler(this).getDecryptTask();
		if(decryptTask!=null){
			if(decryptTask.getStatus()==AsyncTask.Status.RUNNING){
				Log.d(TAG, "onDestroy: canceling the task");
				decryptTask.cancel(true);
			}
		}
		EncryptTask encryptTask=SharedData.getInstance().getTaskHandler(this).getEncryptTask();
		if(encryptTask!=null ){
			if (encryptTask.getStatus()==AsyncTask.Status.RUNNING){
				encryptTask.cancel(true);
			}
		}
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


	ActionMode actionMode;
	@Override
	public void onLongClick() {
		if(actionMode==null) {
			actionMode = startSupportActionMode(new ActionViewHandler(this));
		}
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
		if(path.equals(mRootPath)){
			path="Home";
		}else{
			path=path.substring(0,path.lastIndexOf('/'));
			path=path.substring(path.lastIndexOf('/')+1);
		}
		assert getSupportActionBar()!=null;
		getSupportActionBar().setTitle(path);
	}


    public void resetmKeyPass(){
        SharedData.KEY_PASSWORD=null;
    }
    /**
	 * end of task executing section
	 */



}
