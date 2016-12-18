package com.cryptopaths.cryptofm.filemanager;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
		mRootPath=mCurrentPath;
		mFileListView=(RecyclerView) findViewById(R.id.fileListView);
		mmFileListAdapter=new FileListAdapter(this);
		mmFileListAdapter.fillAdapter(mCurrentPath);
		mFileListView.setAdapter(mmFileListAdapter);

		mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String viewName=((TextView)(view.findViewById(R.id.list_textview))).getText().toString();

				String tmp=mCurrentPath+"/"+viewName;
				File f=new File(tmp);
				if(f.isDirectory()) {
					mCurrentPath=tmp;
					changeDirectory();
				}else {
					Toast.makeText(FileBrowserActivity.this,"You clicked at file",Toast.LENGTH_SHORT).show();
				}


			}
		});
	}

	private void changeDirectory() {
		mmFileListAdapter.fillAdapter(mCurrentPath);
		mmFileListAdapter.notifyDataSetChanged();
		mFileListView.requestLayout();

	}

	@Override
	public void onBackPressed() {
		if(mCurrentPath.equals(mRootPath)){
			super.onBackPressed();
		}else{
			//modify the mCurrentPath
			mCurrentPath=mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/'));
			changeDirectory();
		}
	}

}
