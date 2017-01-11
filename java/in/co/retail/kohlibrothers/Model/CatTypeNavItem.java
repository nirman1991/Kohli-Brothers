package in.co.retail.kohlibrothers.Model;

import java.util.List;

/**
 * Created by Niha on 11/3/2015.
 */
public class CatTypeNavItem
{
    private boolean mShowNotify;
    private String mTitle;
    public List<CategoryItem> mCatList;

    public CatTypeNavItem()
    {

    }

    public CatTypeNavItem(boolean _showNotify, String _title)
    {
        this.mShowNotify = _showNotify;
        this.mTitle = _title;
    }

    public boolean isShowNotify()
    {
        return mShowNotify;
    }

    public void setShowNotify(boolean _showNotify)
    {
        this.mShowNotify = _showNotify;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String _title)
    {
        this.mTitle = _title;
    }

    public void setCatList(List<CategoryItem> _catList)
    {
        this.mCatList = _catList;
    }

    public List<CategoryItem> getCatList()
    {
        return this.mCatList;
    }
}
