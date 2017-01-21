package com.cryptopaths.cryptofm.filemanager.listview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;

import com.cryptopaths.cryptofm.R;
import com.cryptopaths.cryptofm.filemanager.SharedData;
import com.cryptopaths.cryptofm.filemanager.UiUtils;
import com.cryptopaths.cryptofm.filemanager.listview.ViewHolder;
import com.cryptopaths.cryptofm.utils.FileUtils;

import java.util.ArrayList;

/**
 * Created by tripleheader on 1/17/17.
 * swipe action handler
 */

public class RecyclerViewSwipeHandler extends ItemTouchHelper.SimpleCallback{
    private Paint p;
    private Context mContext;
    public RecyclerViewSwipeHandler(Context context){
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        p=new Paint();
        this.mContext=context;
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        final String filePath= FileUtils.CURRENT_PATH+((ViewHolder)viewHolder).mTextView.getText();
        ArrayList<String> tmp=new ArrayList<>();
        tmp.add(filePath);
        if(direction==ItemTouchHelper.RIGHT){
            SharedData.getInstance().getTaskHandler(mContext).encryptTask((tmp));
        }else{
            if(SharedData.KEY_PASSWORD==null) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.password_dialog_layout);
                dialog.show();
                dialog.findViewById(R.id.cancel_decrypt_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                final EditText editText = (EditText) dialog.findViewById(R.id.key_password);
                dialog.findViewById(R.id.decrypt_file_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().length() < 1) {
                            editText.setError("please give me your encryption password");
                        } else {
                            SharedData.KEY_PASSWORD = editText.getText().toString();
                            dialog.dismiss();
                            ArrayList<String> innerTmp=new ArrayList<>();
                            innerTmp.add(filePath);
                            SharedData.getInstance().getTaskHandler(mContext).decryptFile(
                                    SharedData.USERNAME,
                                    SharedData.KEY_PASSWORD,
                                    SharedData.DB_PASSWWORD,
                                    innerTmp
                            );
                        }
                    }
                });
            }else{
                SharedData.getInstance().getTaskHandler(mContext).decryptFile(
                        SharedData.USERNAME,
                        SharedData.KEY_PASSWORD,
                        SharedData.DB_PASSWWORD,
                        tmp
                );
            }
        }
        UiUtils.reloadData(
                mContext,
                SharedData.getInstance().getFileListAdapter(mContext)
        );
    }

    @Override
    public void onChildDraw(
            Canvas c,
            RecyclerView recyclerView,
            RecyclerView.ViewHolder viewHolder,
            float dX,
            float dY,
            int actionState,
            boolean isCurrentlyActive) {
        Bitmap icon;
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if(dX > 0){
                p.setColor(Color.parseColor("#388E3C"));
                RectF background = new RectF((float) itemView.getLeft(),
                        (float) itemView.getTop(), dX,(float) itemView.getBottom());
                c.drawRect(background,p);
                icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_encrypt);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width ,
                        (float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,
                        (float)itemView.getBottom() - width);
                c.drawBitmap(icon,null,icon_dest,p);
            } else {
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF((float) itemView.getRight() + dX,
                        (float) itemView.getTop(),(float) itemView.getRight(),
                        (float) itemView.getBottom());
                c.drawRect(background,p);
                icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_decrypt);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,
                        (float) itemView.getTop() + width,
                        (float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                c.drawBitmap(icon,null,icon_dest,p);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
