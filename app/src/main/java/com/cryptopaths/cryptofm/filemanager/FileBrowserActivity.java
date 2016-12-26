package com.cryptopaths.cryptofm.filemanager;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cryptopaths.cryptofm.R;

import java.util.HashMap;


public class FileBrowserActivity extends AppCompatActivity implements ActionMode.Callback,FileListAdapter.LongClickCallBack {
	private String mCurrentPath;
	private String mRootPath;
	private RecyclerView mFileListView;
	private FileListAdapter mmFileListAdapter;

	public static HashMap<String,FileFillerWrapper> mFilesData=new HashMap<>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_activity);
		setResult(RESULT_OK);


		mCurrentPath=Environment.getExternalStorageDirectory().getPath();
		mRootPath=mCurrentPath+"/";

		mFileListView=(RecyclerView) findViewById(R.id.fileListView);
		mFileListView.setLayoutManager(new LinearLayoutManager(this));
		mmFileListAdapter=new FileListAdapter(this);
		mmFileListAdapter.setmFile(mFilesData.get(mCurrentPath+"/"));
		DividerItemDecoration dividerItemDecoration =
				new DividerItemDecoration(mFileListView.getContext(),
						1);
		mFileListView.addItemDecoration(dividerItemDecoration);
		mFileListView.setAdapter(mmFileListAdapter);


	}

	private void changeDirectory() {
		Log.d("files","current path: "+mCurrentPath);
		mmFileListAdapter.setmFile(mFilesData.get(mCurrentPath));
		mmFileListAdapter.notifyDataSetChanged();
		mFileListView.requestLayout();

	}

	@Override
	public void onBackPressed() {
		mCurrentPath=FileUtils.CURRENT_PATH;
		if(mCurrentPath.equals(mRootPath)){
			super.onBackPressed();
		}else{
			//modify the mCurrentPath
			mCurrentPath=mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/'));
			mCurrentPath=mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/')+1);
			FileUtils.CURRENT_PATH=mCurrentPath;
			changeDirectory();

		}
	}


	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.file_select_options,menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if(item.getItemId()==R.id.encrypt_menu_item){

		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		selectCount=0;
		mmFileListAdapter.setmSelectionMode(false);
		actionMode=null;
	}


	ActionMode actionMode;
	private int selectCount=0;
	@Override
	public void onLongClick() {
		if(actionMode==null){
			actionMode=startSupportActionMode(this);
			actionMode.setTitle(++selectCount+" Selected");
		}else{
			actionMode.setTitle(++selectCount+" Selected");
		}
	}
	@Override
	public void incrementSelectionCount(){
		actionMode.setTitle(++selectCount+" Selected");
	}
}
