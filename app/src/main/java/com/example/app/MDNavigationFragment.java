package com.example.app;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class MDNavigationFragment extends Fragment {
  public enum MDNavigationPage {
    ENTRIES, ENTRY, SETTINGS
  }

  protected ImageView entriesIcon;
  protected ImageView entryIcon;
  protected ImageView settingsIcon;
  protected MDNavigationPage currentPage;

  public MDNavigationFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_mdnavigation, container, false);
    entriesIcon = (ImageView)rootView.findViewById(R.id.entries);
    entryIcon = (ImageView)rootView.findViewById(R.id.entry);
    settingsIcon = (ImageView)rootView.findViewById(R.id.settings);
    ImageView icons[] = {entriesIcon, entryIcon, settingsIcon};
    for (int i=0; i<icons.length; i++) {
      final int j = i;
      icons[i].setOnClickListener(new View.OnClickListener() {
        MDNavigationPage icon = MDNavigationPage.values()[j];
        @Override
        public void onClick(View view) {
          switch (icon) {
            case ENTRIES: {
              Intent intent = new Intent(getActivity(), EntriesActivity.class);
              startActivity(intent);
              break;
            }
            case ENTRY: {
              Intent intent = new Intent(getActivity(), CurrentEntryActivity.class);
              startActivity(intent);
              break;
            }
          }
        }
      });
    }

    return rootView;
  }

}
