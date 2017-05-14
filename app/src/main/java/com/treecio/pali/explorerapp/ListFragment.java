package com.treecio.pali.explorerapp;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass used as the Fragment holding the files in a ListView/GridView
 * based od the orientation of the phone.
 * Activities that contain this fragment must implement the
 * {@link ListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    /**
     * Items and their positions when selected by long-press.
     */
    private List<String> items = new ArrayList<String>();
    private List<Integer> positions = new ArrayList<Integer>();

    private OnFragmentInteractionListener mListener;

    /**
     * Names of folders displayed.
     */
    private String[] names;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param names Pass the names of files/folders in a directory.
     * @return A new instance of fragment ListFragment.
     */
    public static ListFragment newInstance(String[] names) {
        ListFragment fragment = new ListFragment();
        fragment.names = names;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list, container, false);

        AbsListView absListView = (AbsListView) v.findViewById(R.id.list_view);

        String[] fileNames = names;

        /*
        if(fileNames == null)
            fileNames = new String[]{""};*/

        //Set up custom adapter which colors the views based on selection.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, fileNames){

            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                //if the view was selected
                if(positions.contains(new Integer(position)))
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.darker_gray));
                else
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
                return v;
            }
        };

        absListView.setAdapter(adapter);

        //Set the listener to be able to navigate through derectories and open files
        absListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = (((MainActivity)getActivity()).currentPath + "/" + parent.getItemAtPosition(position).toString());
                File file = new File(path);
                if (file.isDirectory())
                    ((MainActivity) getActivity()).changeDirectory(path);
                else
                    ((MainActivity) getActivity()).openFile(path);
            }
        });


        setupMultiChoiceListener(absListView, adapter);

        // Inflate the layout for this fragment
        return v;
    }

    /**
     * This methhod adds the long-press multiple choice functionality to the AbsListView.
     * @param absListView
     * @param adapter
     */
    private void setupMultiChoiceListener(final AbsListView absListView, final ArrayAdapter<String> adapter) {

        absListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        absListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {


            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {

                //store the values and positions of selected items and remove them if deselected
                if(checked) {
                    items.add(absListView.getItemAtPosition(position).toString());
                    positions.add(new Integer(position));
                } else {
                    items.remove(absListView.getItemAtPosition(position).toString());
                    positions.remove(new Integer(position));
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.item_delete:
                        ((MainActivity) getActivity()).deleteSelectedItems(items);
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                for(int i = 0; i < absListView.getChildCount(); i++) {
                    absListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
                //They are not selected anymore.
                items.clear();
                positions.clear();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.implement_listener));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
