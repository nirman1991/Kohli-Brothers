package in.co.retail.kohlibrothers.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Niha on 1/7/2016.
 */
public class CartModel  implements Serializable
{
    List<ProductCommonModel> mProductModel;
    String mTotal;
    String mDiscount;

    public List<ProductCommonModel> getProductItemList() {
        return mProductModel;
    }

    public void setProductItemList(List<ProductCommonModel> productModel) {
        this.mProductModel = productModel;
    }

    public String getmTotal() {
        return mTotal;
    }

    public void setmTotal(String mTotal) {
        this.mTotal = mTotal;
    }

    public String getmDiscount() {
        return mDiscount;
    }

    public void setmDiscount(String mDiscount) {
        this.mDiscount = mDiscount;
    }
}
