package org.codejudge.shopping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static android.content.ContentValues.TAG;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

public class ProductBuyActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView product;
    TextView pname, pprice;
    Button cart;
    private Integer id, quantity;
    private String name, imageUrl;
    private Double price;
    private CartDatabase cartDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_buy);
        product = (ImageView) findViewById(R.id.product_buy_image);
        pname = (TextView) findViewById(R.id.product_buy_name);
        pprice = (TextView) findViewById(R.id.product_buy_price);
        cart = (Button) findViewById(R.id.add_to_cart);
        Bundle b = getIntent().getExtras();
        imageUrl = b.getString("url");
        name = b.getString("name");
        price = b.getDouble("price");
        id = b.getInt("id");
        cartDatabase = new CartDatabase(getApplicationContext());
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);

        ImageLoader.getInstance().displayImage(imageUrl, product);
        pname.setText(name);
        pprice.setText("Rs. " + price);
        cart.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        List<CartProduct> cartProducts = cartDatabase.selectAllRecords();
        CartProduct updatedCartProduct = null;
        for (CartProduct cartProduct : cartProducts) {
            if (id == cartProduct.getId()) {
                updatedCartProduct = cartProduct;
                updatedCartProduct.setQuantity(cartProduct.getQuantity() + 1);
            }
        }
        long value;
        if (updatedCartProduct != null) {
            value = cartDatabase.createRecords(updatedCartProduct.getId(), updatedCartProduct.getName(), updatedCartProduct.getPrice(), updatedCartProduct.getImageUrl(), updatedCartProduct.getQuantity());
        }
        else {
            value = cartDatabase.createRecords(id, name, price, imageUrl, 1);
        }
        Log.i(TAG, "onClick: value is = " + value);
        Toast.makeText(getApplicationContext(), "Product Added to Cart", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart) {
            Intent intent = new Intent(ProductBuyActivity.this, CartActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

//    public class CartTask extends AsyncTask<String, Product, List<Product>> {
//
//        @Override
//        protected List<Product> doInBackground(String... params) {
//            HttpURLConnection httpURLConnection;
//            URL url = null;
//            try{
//                if (cartId == null) {
//                    url = new URL(params[0]);
//                    httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("POST");
//                    httpURLConnection.setRequestProperty("Authorization", authToken);
//                    httpURLConnection.connect();
//                    StringBuilder stringBuilder = new StringBuilder("");
//                    String line = "";
//                    InputStream inputStream = httpURLConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    while ((line = bufferedReader.readLine()) != null){
//                        stringBuilder.append(line);
//                    }
//                    httpURLConnection.disconnect();
//                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
//                    cartId = jsonObject.getString("cartId");
//                }
//                url = new URL(params[1] + cartId);
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.connect();;
//                StringBuilder stringBuilder = new StringBuilder("");
//                String line = "";
//                InputStream inputStream = httpURLConnection.getInputStream();
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                while ((line = bufferedReader.readLine()) != null){
//                    stringBuilder.append(line);
//                }
//                httpURLConnection.disconnect();
//                List<Product> ProductList = new ArrayList<>();
//                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
//                JSONArray jsonArray = jsonObject.getJSONArray("products");
//                int count = 0;
//                Product Product;
//                while(count < jsonArray.length()){
//                    JSONObject jsonObject1 = jsonArray.getJSONObject(count);
//                    count++;
//                    Product = new Product();
//                    Product.setImageUrl(jsonObject1.getString("imageUrl"));
//                    Product.setName(jsonObject1.getString("name"));
//                    Product.setPrice(jsonObject1.getInt("price"));
//                    ProductList.add(Product);
//                }
//                return ProductList;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//
////        @Override
////        protected void onPostExecute(List<Product> result) {
////            super.onPostExecute(result);
////            ProductsActivity.ProductAdapter ProductAdapter = new ProductsActivity.ProductAdapter(getApplicationContext(), R.layout.product, result);
////            listView.setAdapter(ProductAdapter);
////
////            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                @Override
////                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                    Product Product  = (Product) listView.getItemAtPosition(position);
////                    Intent i = new Intent(ProductsActivity.this, ProductBuyActivity.class);
////                    i.putExtra("url", Product.getImageUrl());
////                    i.putExtra("name", Product.getName());
////                    i.putExtra("price", Product.getPrice());
////                    startActivity(i);
////                }
////            });
////        }
//    }
}
