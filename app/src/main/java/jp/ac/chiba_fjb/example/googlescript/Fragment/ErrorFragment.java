package jp.ac.chiba_fjb.example.googlescript.Fragment;


import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import jp.ac.chiba_fjb.example.googlescript.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorFragment extends DialogFragment implements View.OnClickListener {


    public ErrorFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return d;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_error, container, false);
        view.findViewById(R.id.dialogyes).setOnClickListener(this);
        view.findViewById(R.id.dialogNo).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        //リスナーに登録されているメソッドを呼び出す

        //ダイアログを閉じる
        getDialog().cancel();
    }

    public interface OnDialogButtonListener {
        void onDialogButton(int value);

    }

    //インタフェイスのインスタンス保存用
    BlankFragment.OnDialogButtonListener mListener;

    //ボタン動作のインスタンスを受け取る
    public void setOnDialogButtonListener(BlankFragment.OnDialogButtonListener listener) {
        mListener = listener;
    }

}
