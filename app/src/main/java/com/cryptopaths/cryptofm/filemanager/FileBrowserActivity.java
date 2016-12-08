package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;

import org.w3c.dom.Text;

import java.io.File;
import java.math.BigDecimal;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FileBrowserActivity extends AppCompatActivity implements FileFragment.onClickListener{
	private String mCurrentPath;
	int previousEntryCount=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_activity);
		setResult(RESULT_OK);

		mCurrentPath=Environment.getExternalStorageDirectory().getPath();
		chanegFragment(mCurrentPath);
		//fill list view
		fillList();
	}
	private void fillList(){
		getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
			//	if(!mCurrentPath.equals(Environment.getExternalStorageDirectory().getPath())){
				//	if(getSupportFragmentManager().getBackStackEntryCount()<previousEntryCount){
				//		Log.d("googlef","wao changed to: "+getSupportFragmentManager().getBackStackEntryCount());
				//		chanegFragment(mCurrentPath.substring(0,mCurrentPath.lastIndexOf('/')));
				//		previousEntryCount--;
				//	}
//
				//}
			}
		});

	}
	private void chanegFragment(String path){
		FileFragment fileFragment=new FileFragment();
		mCurrentPath=path;
		Bundle args=new Bundle();
		args.putString("dir",path);
		fileFragment.setArguments(args);
		getSupportFragmentManager().beginTransaction().
				setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,
						R.anim.enter_from_left, R.anim.exit_to_right).
				replace(R.id.file_fragment,fileFragment).
				addToBackStack(path).
				commit();
		previousEntryCount++;
	}


	@Override
	public void onItemClick(String path) {
		chanegFragment(path);
	}
}
