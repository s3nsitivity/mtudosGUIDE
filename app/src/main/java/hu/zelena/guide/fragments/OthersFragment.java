package hu.zelena.guide.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hu.zelena.guide.R;

/**
 * Created by patrik on 2016.11.14..
 */

public class OthersFragment extends Fragment {

    public OthersFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_others, container, false);

        return rootView;
    }
}