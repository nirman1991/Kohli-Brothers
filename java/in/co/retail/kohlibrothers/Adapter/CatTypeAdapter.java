package in.co.retail.kohlibrothers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import in.co.retail.kohlibrothers.Model.CatTypeNavItem;
import in.co.retail.kohlibrothers.Model.CategoryItem;
import in.co.retail.kohlibrothers.R;

/**
 * Created by Niha on 11/27/2015.
 */
public class CatTypeAdapter extends BaseExpandableListAdapter
{
    List<CatTypeNavItem> mCatTypeData;
    private LayoutInflater mInflater;
    private Context mContext;

    public CatTypeAdapter(Context _context, List<CatTypeNavItem> data)
    {
        this.mContext = _context;
        //LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //mInflater = LayoutInflater.from(mContext);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mCatTypeData = data;
    }
    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount()
    {
        if(mCatTypeData != null)
            return mCatTypeData.size();
        else
            return 0;
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition)
    {
        List<CategoryItem> _catList = mCatTypeData.get(groupPosition).getCatList();
        if(_catList != null)
            return _catList.size();
        else
            return 0;
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition)
    {
        return mCatTypeData.get(groupPosition).getTitle();
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return mCatTypeData.get(groupPosition).getCatList().get(childPosition);
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
     * @see CatTypeAdapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getGroupView(int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.nav_drawer_row, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.title)).setText(mCatTypeData.get(groupPosition).getTitle());
        convertView.setTag(mCatTypeData.get(groupPosition));
        /*ImageButton imgExpand = (ImageButton) convertView.findViewById(R.id.imgExpand);
        if (mCatTypeData.get(groupPosition).getCatList() != null) {
            imgExpand.setVisibility(View.VISIBLE);
        }
        else {
            imgExpand.setVisibility(View.GONE);
        }/**/

        return convertView;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created by
     *                      {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        List<CategoryItem> _catItem  = mCatTypeData.get(groupPosition).getCatList();
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.common_list_row, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.titleName)).setText(_catItem.get(childPosition).getCatName());
        ((TextView) convertView.findViewById(R.id.titleCode)).setText(_catItem.get(childPosition).getCatCode());
        convertView.setTag(_catItem);
        return convertView;
    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    /*@Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }
/*
expandablelistview in navigation drawer in android studio
android navigation drawer expandablelistview example
    /*http://www.myandroidsolutions.com/2012/08/10/android-expandable-list-example/**/
}
