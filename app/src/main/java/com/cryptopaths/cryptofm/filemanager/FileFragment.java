package com.cryptopaths.cryptofm.filemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cryptopaths.cryptofm.R;

import java.io.File;


/**
 * Created by tripleheader on 12/8/16.
 */

public class FileFragment extends Fragment {
    public interface onClickListener{
        public void onItemClick(String path);
    }
    private onClickListener onItemClickedListener;

    private String                  mCurrentPath;
    private ListView                mFileListView;

    private Boolean                 mIsFragmentAlreadyLoaded=false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("googlef","im in create view");
        this.mCurrentPath=getArguments().getString("dir");
        //if file is empty them do nopt render listview
        File file=new File(mCurrentPath);
        if(file.listFiles().length<1){
            Log.d("googlef", "onCreateView: yeah length is less than one.");
            mIsFragmentAlreadyLoaded=true;
            return inflater.inflate(R.layout.no_files_layout,container,false);
        }
        View rootView=inflater.inflate(R.layout.file_fragment,container,false);
        mFileListView=(ListView)rootView.findViewById(R.id.fileListView);
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String viewName=((TextView)(view.findViewById(R.id.list_textview))).getText().toString();

                mCurrentPath+="/"+viewName;
                File f=new File(mCurrentPath);
                if(f.isDirectory()) {
                    onItemClickedListener.onItemClick(mCurrentPath);
                }

            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( !mIsFragmentAlreadyLoaded){
            FileListAdapter adapter=new FileListAdapter(getActivity());

            adapter.fillAdapter(mCurrentPath);
            Log.d("googlef","im in activity created"+mCurrentPath);

            mFileListView.setAdapter(adapter);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("googlef","im on create");

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState==null){

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
        onItemClickedListener=(onClickListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException("google fucked up");
        }
    }



}
