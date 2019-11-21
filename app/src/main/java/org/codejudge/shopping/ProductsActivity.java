package org.codejudge.shopping;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        listView = (ListView) findViewById(R.id.products_listview);
        Bundle b = getIntent().getExtras();
        String link = b.getString("key");
        new ProductTask().execute(link);
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
            Intent intent = new Intent(ProductsActivity.this, CartActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class ProductTask extends AsyncTask<String, Product, List<Product>> {

        @Override
        protected List<Product> doInBackground(String... params) {
            HttpURLConnection httpURLConnection;
            try{
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();;
                StringBuilder stringBuilder = new StringBuilder("");
                String line = "";
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                httpURLConnection.disconnect();
                List<Product> ProductList = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("products");
                int count = 0;
                Product Product;
                while(count < jsonArray.length()){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(count);
                    count++;
                    Product = new Product();
                    Product.setImageUrl(jsonObject1.getString("imageUrl"));
                    Product.setName(jsonObject1.getString("name"));
                    Product.setPrice(jsonObject1.getDouble("price"));
                    Product.setId(jsonObject1.getInt("productId"));
                    ProductList.add(Product);
                }
                return ProductList;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Product> result) {
            super.onPostExecute(result);
            ProductAdapter ProductAdapter = new ProductAdapter(getApplicationContext(), R.layout.product, result);
            listView.setAdapter(ProductAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Product Product  = (Product) listView.getItemAtPosition(position);
                    Intent i = new Intent(ProductsActivity.this, ProductBuyActivity.class);
                    i.putExtra("url", Product.getImageUrl());
                    i.putExtra("name", Product.getName());
                    i.putExtra("price", Product.getPrice());
                    i.putExtra("id", Product.getId());
                    startActivity(i);
                }
            });
        }
    }

    public class ProductAdapter extends ArrayAdapter {

        int resource;
        LayoutInflater layoutInflater;
        List<Product> ProductList;
        public ProductAdapter(Context context, int resource, List<Product> objects) {
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
            ImageView image = (ImageView) convertView.findViewById(R.id.product_image);
            TextView name = (TextView) convertView.findViewById(R.id.product_name);
            TextView price = (TextView) convertView.findViewById(R.id.product_price);
            name.setText(ProductList.get(position).getName());
            price.setText("$ "+ProductList.get(position).getPrice());
            ImageLoader.getInstance().displayImage(ProductList.get(position).getImageUrl(), image);
            return convertView;
        }
    }
}
