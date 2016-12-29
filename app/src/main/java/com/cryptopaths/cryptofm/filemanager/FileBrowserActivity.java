package com.cryptopaths.cryptofm.filemanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.tasks.DeleteTask;
import com.cryptopaths.cryptofm.tasks.RenameTask;
import com.cryptopaths.cryptofm.utils.ActionHandler;
import com.cryptopaths.cryptofm.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;


public class FileBrowserActivity extends AppCompatActivity
		implements ActionMode.Callback,FileListAdapter.LongClickCallBack {

	private String 			mCurrentPath;
	private String 			mRootPath;
	private RecyclerView 	mFileListView;
	private FileListAdapter mmFileListAdapter;

	public static HashMap<String,FileFillerWrapper> mFilesData	= new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_activity);
		setResult(RESULT_OK);


		mCurrentPath 	  = Environment.getExternalStorageDirectory().getPath();
		mRootPath	 	  = mCurrentPath+"/";
		mFileListView 	  = (RecyclerView) findViewById(R.id.fileListView);
		mmFileListAdapter = new FileListAdapter(this);

		mFileListView.setLayoutManager(new LinearLayoutManager(this));
		mmFileListAdapter.setmFile(mFilesData.get(mCurrentPath+"/"));
		// item decoration for displaying divider
		DividerItemDecoration dividerItemDecoration =
				new DividerItemDecoration(mFileListView.getContext(),
						1);
		mFileListView.addItemDecoration(dividerItemDecoration);

		mFileListView.setAdapter(mmFileListAdapter);


	}

	@ActionHandler(layoutResource = R.id.floating_add)
	public void onAddFloatingClicked(View v){
        UiUtils.actionMode = this.actionMode;
		createFile();
	}

	private void changeDirectory() {
		Log.d("files","current path: "+mCurrentPath);
		mmFileListAdapter.setmFile(mFilesData.get(mCurrentPath));
		mmFileListAdapter.notifyDataSetChanged();
		mFileListView.requestLayout();

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
			changeDirectory();

		}
	}

	/**
	 *start of ActionMode section
     */
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.file_select_options,menu);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		UiUtils.actionMode=this.actionMode;
		if (item.getItemId()==R.id.rename_menu_item){
			renameFile();
		}
		if(item.getItemId()==R.id.delete_menu_item){
			deleteFile();
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		selectCount = 0;
		actionMode  = null;
		mmFileListAdapter.resetFileIcons();
		mmFileListAdapter.setmSelectionMode(false);
	}


	ActionMode actionMode;
	private int selectCount=0;
	@Override
	public void onLongClick() {
		if(actionMode==null) {
			actionMode = startSupportActionMode(this);
		}
	}
	@Override
	public void incrementSelectionCount(){
		actionMode.setTitle(++selectCount+" Selected");
		if(selectCount>1){
			actionMode.getMenu().removeItem(R.id.rename_menu_item);
		}
	}

	@Override
	public void decrementSelectionCount() {
		if(actionMode!=null){
			actionMode.setTitle(--selectCount+" Selected");
			if(selectCount==0){
				actionMode.finish();
			}
			else if(selectCount<2){
				actionMode.getMenu().add(0,R.id.rename_menu_item,0,"rename");
			}
		}
	}
	/**
	 * end of action mode section
	 */

	/**
	 * task executing section
	 */
	private void renameFile(){
		final Dialog dialog = UiUtils.createDialog(
				this,
				"Rename file",
				"rename"
		);

		final EditText folderEditText = (EditText)dialog.findViewById(R.id.foldername_edittext);
		Button okayButton			  = (Button)dialog.findViewById(R.id.create_file_button);
		String currentFileName		  = mmFileListAdapter.getmSelectedFilePaths().get(0);

		currentFileName = currentFileName.substring(
				currentFileName.lastIndexOf('/')+1,
				currentFileName.length()
		);
		folderEditText.setText(currentFileName);

		okayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String folderName=folderEditText.getText().toString();
				if(folderName.length()<1){
					folderEditText.setError("Give me the folder name");
				}else{
					new RenameTask(
							FileBrowserActivity.this,
							mmFileListAdapter,
							mmFileListAdapter.getmSelectedFilePaths().get(0),
							folderName
					).execute();
					dialog.dismiss();
				}
			}
		});
	}
	private void deleteFile(){
		AlertDialog dialog=new AlertDialog.Builder(this).create();
		dialog.setTitle("Delete confirmation");
		dialog.setMessage("Do you really want to delete these files(s)?");
		dialog.setButton(
				DialogInterface.BUTTON_POSITIVE,
				"yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						DeleteTask task=new DeleteTask(
								FileBrowserActivity.this,
								mmFileListAdapter,
								mFileListView,
								(ArrayList<String>) mmFileListAdapter.getmSelectedFilePaths().clone());
						task.execute();

					}
				});
		dialog.setButton(
				DialogInterface.BUTTON_NEUTRAL,
				"No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						//do nothing
					}
				});
		dialog.show();
	}
	private void createFile(){
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
	/**
	 * end of task executing section
	 */

}
