package com.techsolvit.rppayment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PayloadHelper;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PaymentResultWithDataListener, ExternalWalletListener {
    Button button;
    String HttpUrl = "http://bonsaigardening.in/craftzoneapi/api_v1/payment/make_data_txn_rpay2";
    String oid;
    private static final String TAG = MainActivity.class.getSimpleName();
    private AlertDialog.Builder alertDialogBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Preload payment resources
         */
         Checkout.preload(getApplicationContext());
        //Checkout.sdkCheckIntegration((Activity) MainActivity);

        // ...
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Payment Result");
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            //do nothing
        });

       // checkout.setKeyID("<YOUR_KEY_ID>");
         button = (Button) findViewById(R.id.btn_pay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startPayment();
                OrderId();
            }
        });

    }

    public void OrderId() {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST,HttpUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Hiding the progress dialog after all task complete.
                //progressDialog.dismiss();
                //Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_LONG).show();
               // Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                // Showing response message coming from server.
                try {
                    JSONObject object = new JSONObject(response);
                    String tet = object.getString("status");
                     oid = object.getString("orderid");
                    String i = "success";
                    if (i.equals(tet)) {
                        //Toast.makeText(MainActivity.this, oid, Toast.LENGTH_SHORT).show();
                        startPayment();
                        Toast.makeText(MainActivity.this, oid, Toast.LENGTH_SHORT).show();
                    } else {
                        String Oid = object.getString("status");

                    }
                    //Toast.makeText(DasboardPage.this, UserMobile, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Hiding the progress dialog after all task complete.
                       // progressDialog.dismiss();
                        // Showing error message if something goes wrong.
                        Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            //Pass Your Parameters here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("h_coupon_total_amt_payble","1");
                return params;
            }

          //  private static final String Username = "auth@pepaal";
           // private static final String Password = "auth@2k91";

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJydV9pZCI6IjMyIiwicnVfbmFtZSI6IlBhcmFtIiwicnVfbW9iaWxlIjoiODI1MDEzNjkwNiIsImlhdCI6MTY3MjAzNzEzMywiZXhwIjoxNjcyMjk2MzMzfQ.eYEHg9wtAMxscIJds8_uqnAKeHXIKReKJynlOx4wA-A");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }



    public void startPayment() {
        //checkout.setKeyID("<YOUR_KEY_ID>");
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        checkout.setKeyID("rzp_live_FgnyBstDklGJdI");

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.ic_launcher_background);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Merchant Name");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("order_id", oid);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", "100");//pass amount in currency subunits
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact","9988776655");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
           // Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */


    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("External Wallet Selected:\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("Payment Successful :\nPayment ID: "+s+"\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("Payment Failed:\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}