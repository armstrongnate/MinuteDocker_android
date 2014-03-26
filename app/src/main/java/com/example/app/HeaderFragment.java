package com.example.app;



import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HeaderFragment extends Fragment {
    protected TextView appName;

    public HeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_header, null);
        appName = (TextView) rootView.findViewById(R.id.header_app_name);
        Typeface extraBold = Typeface.createFromAsset(getActivity().getAssets(), "Proxima_Nova_Extrabold.ttf");
        appName.setTypeface(extraBold);

        return rootView;
    }


}