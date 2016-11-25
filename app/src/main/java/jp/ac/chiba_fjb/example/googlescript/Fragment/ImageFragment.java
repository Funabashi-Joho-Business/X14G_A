package jp.ac.chiba_fjb.example.googlescript.Fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jp.ac.chiba_fjb.example.googlescript.Camera.CameraFragment;
import jp.ac.chiba_fjb.example.googlescript.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment implements View.OnTouchListener {


    public ImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_image, container, false);

        view.setOnTouchListener(this);

        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        imageView.setImageBitmap((Bitmap)getArguments().getParcelable("bitmap"));

        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainLayout,new CameraFragment());
        ft.commit();

        return false;
    }
}
