package com.como.laps.byu;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {

    private PendingIntent nfcPendingIntent;
    private IntentFilter[] intentFiltersArray;
    private NfcAdapter nfcAdpt;
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private String clientToken = "";
    private final static int REQUEST_CODE = 100;
    private final static boolean DEBUG_MODE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final View layout = findViewById(R.id.list_layout);
        layout.setBackground(getDrawable(R.mipmap.background));
        final ListView productListView = (ListView) findViewById(R.id.product_list);
        productList = new ArrayList<>();

        productAdapter = new ProductAdapter(this, 0, productList);
        productListView.setAdapter(productAdapter);

        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        nfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, 0);

        IntentFilter tagIntentFilter =
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            tagIntentFilter.addDataType("text/plain");
            intentFiltersArray = new IntentFilter[]{tagIntentFilter};
        } catch (Throwable t) {
            t.printStackTrace();
        }
        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        // Check if the smartphone has NFC
        if (nfcAdpt == null) {
            Toast.makeText(this, "NFC not supported", Toast.LENGTH_LONG).show();
            finish();
        }
        // Check if NFC is enabled
        if (!nfcAdpt.isEnabled()) {
            Toast.makeText(this, "Enable NFC before using the app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d("Nfc", "New intent");
        try {
            getTag(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getTag(Intent i) throws UnsupportedEncodingException {
        if (i == null)
            return;
        String type = i.getType();
        String action = i.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d("Nfc", "Action NDEF Found");
            Parcelable[] parcs = i.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            // List record

            for (Parcelable p : parcs) {
                NdefMessage msg = (NdefMessage) p;
                final int numRec = msg.getRecords().length;

                NdefRecord[] records = msg.getRecords();
                for (NdefRecord record : records) {
                    byte[] payload = record.getPayload();
                    String payloadText = new String(payload, "UTF-8");
                    String[] values = payloadText.split(",");
                    //payload format: "id_shop,id_product,id_instance"
                    getInfoOfProduct(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                }
            }
        }
    }


    private void handleIntent(Intent i) {
        Log.d("NFC", "Intent [" + i + "]");
        try {
            getTag(i);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdpt.enableForegroundDispatch(
                this,
                nfcPendingIntent,
                intentFiltersArray,
                null);
        handleIntent(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdpt.disableForegroundDispatch(this);
    }

    public void getClientTokenFromServer() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://ec2-52-27-136-49.us-west-2.compute.amazonaws.com/getToken.php", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                if (DEBUG_MODE)
                    Toast.makeText(getApplicationContext(), "fail to get Token", Toast.LENGTH_SHORT).show();
                clientToken = "";
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                clientToken = s;
                if (!clientToken.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), BraintreePaymentActivity.class);
                    intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, clientToken);
                    startActivityForResult(intent, 100);
                }
                if (DEBUG_MODE)
                    Toast.makeText(getApplicationContext(), "got token", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void onBraintreeSubmit(View v) {
        getClientTokenFromServer();
    }


    protected void getInfoOfProduct(int id_shop, int id_product, int id_instance) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("idShop", id_shop);
        params.put("idProduct", id_product);
        params.put("idInstance", id_instance);
        client.post("http://ec2-52-27-136-49.us-west-2.compute.amazonaws.com/getInfo.php", params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        String productResult = new String(bytes);
                        String[] values = productResult.split("_");
                        String name = values[1];
                        String photoUrl = values[0];
                        float price = Float.parseFloat(values[2]);
                        boolean deliverable = Boolean.parseBoolean(values[3]);
                        productList.add(new Product(name,photoUrl,price,deliverable));
                        productAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

                    }
                    // Your implementation here
                }
        );
    }

    void postNonceToServer(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", nonce);
        params.put("delivery", deliverable);
        params.put("idShop", id_shop);
        params.put("idProduct", id_product);
        params.put("idInstance", id_instance);
        client.post("http://ec2-52-27-136-49.us-west-2.compute.amazonaws.com/pay.php", params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                        ((TextView) findViewById(R.id.text)).setText(new String(bytes));
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

                    }
                    // Your implementation here
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == BraintreePaymentActivity.RESULT_OK) {
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                postNonceToServer(paymentMethodNonce);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
