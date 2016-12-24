package com.cryptopaths.cryptofm.filemanager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.cryptopaths.cryptofm.R;

import java.io.File;
import java.util.HashMap;


public class FileBrowserActivity extends AppCompatActivity {
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
		//start the file filing task to avoid later time overhead
		//new FileFillingTask().execute();


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
	private class FileFillingTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... voids) {
			File file=new File(mCurrentPath);
			fillData(file);
			return null;
		}

		private void fillData(File file) {
			Log.d("Files","current dir: "+file.getPath());
			FileBrowserActivity.mFilesData.put(
					file.getPath()+"/",new FileFillerWrapper
							(file.getPath()+"/",FileBrowserActivity.this)
			);
			for (File f:
				 file.listFiles()) {
				if(f.isDirectory()){
					fillData(f);
				}
			}
		}
	}

}
