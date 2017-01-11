package in.co.retail.kohlibrothers.Model;

import java.io.Serializable;

/**
 * Created by Niha on 1/5/2016.
 */
public class ProductCommonModel implements Serializable
{
    private String ProductBrCode;
    private String ProductCode;
    private String ProductName;
    private String ProductMRP;
    private String ProductDFRate;
    private String ProductBalQty;
    private String SelectedQty;

    public String getProductBrCode() {
        return ProductBrCode;
    }

    public void setProductBrCode(String productBrCode) {
        ProductBrCode = productBrCode;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductMRP() {
        return ProductMRP;
    }

    public void setProductMRP(String productMRP) {
        ProductMRP = productMRP;
    }

    public String getProductDFRate() {
        return ProductDFRate;
    }

    public void setProductDFRate(String productDFRate) {
        ProductDFRate = productDFRate;
    }

    public String getProductBalQty() {
        return ProductBalQty;
    }

    public void setProductBalQty(String productBalQty) {
        ProductBalQty = productBalQty;
    }

    public String getSelectedQty() {
        return SelectedQty;
    }

    public void setSelectedQty(String selectedQty) {
        SelectedQty = selectedQty;
    }
}
