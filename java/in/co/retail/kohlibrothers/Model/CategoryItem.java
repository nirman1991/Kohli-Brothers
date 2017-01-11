package in.co.retail.kohlibrothers.Model;

/**
 * Created by Niha on 11/26/2015.
 */
public class CategoryItem
{
    private String mCatCode;
    private String mCatName;

    public void setCatCode(String _catCode)
    {
        this.mCatCode = _catCode;
    }

    public String getCatCode()
    {
        return mCatCode;
    }

    public void setCatName(String _catName)
    {
        this.mCatName = _catName;
    }

    public String getCatName()
    {
        return this.mCatName;
    }
}
