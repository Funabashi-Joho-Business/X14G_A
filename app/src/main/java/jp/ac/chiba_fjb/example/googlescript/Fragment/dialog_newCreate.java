package jp.ac.chiba_fjb.example.googlescript.Fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import jp.ac.chiba_fjb.example.googlescript.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class dialog_newCreate extends DialogFragment implements View.OnClickListener {

    private EditText mEditText;

    public dialog_newCreate() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return d;
    }


    //インタフェイスの定義
    public interface OnDialogButtonListener{
        void onDialogButton(int value,String EditValue);
    }
    //インタフェイスのインスタンス保存用
    OnDialogButtonListener mListener;

    //ボタン動作のインスタンスを受け取る
    public void setOnDialogButtonListener(OnDialogButtonListener listener){
        mListener =  listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_new_create, container, false);
        mEditText=(EditText)view.findViewById(R.id.editText);
        view.findViewById(R.id.button2).setOnClickListener(this);
        view.findViewById(R.id.button3).setOnClickListener(this);

        return view;
    }




    @Override
    public void onClick(View v) {
        //リスナーに登録されているメソッドを呼び出す
        if(mListener != null) {
            if (v.getId() == R.id.button2)
                mListener.onDialogButton(0,mEditText.getText().toString());

}
        //ダイアログを閉じる
        getDialog().cancel();
    }



}