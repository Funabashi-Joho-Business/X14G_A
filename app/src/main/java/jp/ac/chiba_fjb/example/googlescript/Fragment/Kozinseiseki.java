package jp.ac.chiba_fjb.example.googlescript.Fragment;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.ac.chiba_fjb.example.googlescript.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Kozinseiseki extends DialogFragment {


    public Kozinseiseki() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.kozinseiseki, container, false);
    }

}
