package in.co.retail.kohlibrothers.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.List;


import in.co.retail.kohlibrothers.Adapter.CatTypeAdapter;
import in.co.retail.kohlibrothers.Model.CatTypeNavItem;
import in.co.retail.kohlibrothers.R;

import static android.widget.ExpandableListView.OnChildClickListener;
import static android.widget.ExpandableListView.OnGroupClickListener;
import static android.widget.ExpandableListView.OnGroupExpandListener;

/**
 * Created by Niha on 11/3/2015.
 */

public class CatTypeFragmentDrawer extends Fragment
{
    private static String TAG = CatTypeFragmentDrawer.class.getSimpleName();

    private CatTypeFragmentListener mCatTypeFragDrawerListener;
    private List<CatTypeNavItem> mCatTypeItem;
    private ExpandableListView mExpandableLV;
    private View mContainerView;
    private DrawerLayout mDrawerLayout;
    private CatTypeAdapter mCatTypeAdapter;
    private ActionBarDrawerToggle mABDrawerToggle;

    public CatTypeFragmentDrawer()
    {
        super();
    }

    public void setFragmentListener(CatTypeFragmentListener listener)
    {
        this.mCatTypeFragDrawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.cat_type_frag_nav_drawer, container, false);
        mExpandableLV = (ExpandableListView) layout.findViewById(R.id.expandDrawerList);
        setNavDrawerList(null);
        return layout; //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setUp(int _fragmentId, final DrawerLayout _drawerLayout, final Toolbar _toolbar, List<CatTypeNavItem> _data)
    {
        mContainerView = getActivity().findViewById(_fragmentId);
        mDrawerLayout = _drawerLayout;
        mCatTypeItem = _data;
        setNavDrawerList(_data);

        mABDrawerToggle = new ActionBarDrawerToggle(getActivity(), _drawerLayout, _toolbar, R.string.drawer_open, R.string.drawer_close)
        {

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                System.out.println("Drawer Opened.");
                if (!isAdded()) {
                    return;
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
                System.out.println("Drawer Closed.");
                if (!isAdded()) {
                    return;
                }
                getActivity().invalidateOptionsMenu();
            }


            /*// for fading the toolbar
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, slideOffset);
                _toolbar.setAlpha(1 - slideOffset / 2);
            }/**/
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mABDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mABDrawerToggle);
    }

    private void setNavDrawerList(List<CatTypeNavItem> _data)
    {
        setUpAdapter(_data);
        setUpListeners();
    }

    private void setUpAdapter(List<CatTypeNavItem> _data)
    {
        mCatTypeAdapter = new CatTypeAdapter(this.getActivity().getBaseContext(), _data);
        mExpandableLV.setAdapter(mCatTypeAdapter);
        mExpandableLV.setIndicatorBounds(20, 10);
    }

    private void setUpListeners()
    {
        mExpandableLV.setOnGroupClickListener(myOnGroupClickListener);
        mExpandableLV.setOnChildClickListener(myOnChildClickListener);

        mExpandableLV.setOnGroupExpandListener(new OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousItem)
                    mExpandableLV.collapseGroup(previousItem);
                previousItem = groupPosition;
            }
        });
    }

    private OnGroupClickListener myOnGroupClickListener = new OnGroupClickListener()
    {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
        {
            System.out.println("CAT TYPE CLICKED...");
            if(parent.isGroupExpanded(groupPosition))
                parent.collapseGroup(groupPosition);
            else
                parent.expandGroup(groupPosition);

            if(mCatTypeItem.get(groupPosition).getCatList() == null)
            {
                mCatTypeFragDrawerListener.onCatTypeItemSelected(v, groupPosition);
                mDrawerLayout.closeDrawer(mContainerView);
            }
            return true;
        }

    };

    private OnChildClickListener myOnChildClickListener = new OnChildClickListener()
    {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
        {
            System.out.println("CATEGORY TYPE CLICKED...");
            String _catType = mCatTypeItem.get(groupPosition).getTitle();
            String _catCode = mCatTypeItem.get(groupPosition).getCatList().get(childPosition).getCatCode();
            String _catName = mCatTypeItem.get(groupPosition).getCatList().get(childPosition).getCatName();
            mExpandableLV.collapseGroup(groupPosition);
            mCatTypeFragDrawerListener.onCategoryItemSelected(v, _catType, _catCode, _catName);
            parent.expandGroup(groupPosition);
            return false;
        }
    };

    public interface CatTypeFragmentListener
    {
        void onCatTypeItemSelected(View view, int position);
        void onCategoryItemSelected(View view, String catType, String catCode, String catName);
    }
}
