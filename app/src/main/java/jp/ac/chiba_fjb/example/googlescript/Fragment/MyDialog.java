package jp.ac.chiba_fjb.example.googlescript.Fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import jp.ac.chiba_fjb.example.googlescript.R;

public class MyDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(),
                R.style.TransparentDialogTheme);
        // ダイアログの背景を完全に透過。
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        // フルスクリーンでダイアログを表示。
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.setCanceledOnTouchOutside(false); // キャンセル不可の場合
        setCancelable(false); // キャンセル不可の場合
        return dialog;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.dialog, null);
        content.setPadding(50, 400, 50, 0);
        return content;
    }
}