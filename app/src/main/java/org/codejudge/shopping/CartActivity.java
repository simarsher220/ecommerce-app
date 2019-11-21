package org.codejudge.shopping;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

public class CartActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private CartDatabase cartDatabase;
    private ListView cartListView;
    private List<CartProduct> cartProducts;
    private CartProductAdapter cartProductAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        cartDatabase = new CartDatabase(getApplicationContext());
        cartListView = findViewById(R.id.cart_list);
        cartProducts = cartDatabase.selectAllRecords();
//        if (cartProducts != null && cartProducts.size() > 0) {
        cartProductAdapter = new CartProductAdapter(getApplicationContext(), R.layout.cart_product, cartProducts);
            cartListView.setAdapter(cartProductAdapter);
//        }
        cartListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    public class CartProductAdapter extends ArrayAdapter {

        int resource;
        LayoutInflater layoutInflater;
        List<CartProduct> ProductList;

        public CartProductAdapter(Context context, int resource, List<CartProduct> objects) {
            super(context, resource, objects);
            this.resource = resource;
            ProductList = objects;
            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = layoutInflater.inflate(resource, null);
            }
            ImageView image = (ImageView) convertView.findViewById(R.id.product_cart_image);
            TextView name = (TextView) convertView.findViewById(R.id.product_cart_name);
            TextView price = (TextView) convertView.findViewById(R.id.product_cart_price);
            TextView quantity = (TextView) convertView.findViewById(R.id.product_cart_quantity);

            name.setText(ProductList.get(position).getName());
            price.setText("Rs. "+ProductList.get(position).getPrice());
            quantity.setText("Quantity: "+ProductList.get(position).getQuantity());
            ImageLoader.getInstance().displayImage(ProductList.get(position).getImageUrl(), image);
            return convertView;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light);
        alertDialog.setMessage("Are you sure you want to delete?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CartProduct selectedItem = cartProducts.get(position);
                if (cartDatabase.deleteById(selectedItem)) {
                    cartProducts.remove(selectedItem);
                    cartProductAdapter.notifyDataSetChanged();
                    dialogInterface.dismiss();
                }
                else {
                    dialogInterface.dismiss();
                    Toast.makeText(getApplicationContext(), "Error deleting product from cart!!", Toast.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        return false;
    }
}
