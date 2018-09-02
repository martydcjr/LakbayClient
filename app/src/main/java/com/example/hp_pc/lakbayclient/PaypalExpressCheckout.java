package com.example.hp_pc.lakbayclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class PaypalExpressCheckout extends AppCompatActivity {
    String pboxPricex;
    String pboxdriverID;
    BraintreeFragment mBraintreeFragment;
    String mAuthorization;
    HashMap<String, String> paramsHash;
    TextView tvpboxPrice, tvpboxCartype, tvpboxSuccessID;
    Button btnpboxPaynow, btnpbozReturn;
    String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_express_checkout);

        tvpboxPrice = findViewById(R.id.tvpboxPrice);
        tvpboxCartype = findViewById(R.id.tvpboxCartype);
        tvpboxSuccessID = findViewById(R.id.tvpboxSuccessID);

        btnpboxPaynow = findViewById(R.id.btnpboxPaynow);
//        btnpbozReturn = findViewById(R.id.btnpboxReturn);




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            userUID = user.getUid();
        }

        Intent intent = getIntent();

        pboxdriverID = intent.getStringExtra("driverID");

        getToken();

        payTheRide();

//        btnpbozReturn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(PaypalExpressCheckout.this, "Back pressed", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void payTheRide() {
        DatabaseReference getPendingData = FirebaseDatabase.getInstance().getReference().child("pending").child(pboxdriverID).child("price_to_pay");

        getPendingData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String c_ride_price = dataSnapshot.child("c_ride_price").getValue().toString();

                    tvpboxPrice.setText(c_ride_price);
                    tvpboxCartype.setText("wait");
                    pboxPricex = c_ride_price;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnpboxPaynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PaypalExpressCheckout.this, "Clicked", Toast.LENGTH_SHORT).show();
                setupBraintreeAndStartExpressCheckout();
            }
        });
    }

    private void getToken() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://10.0.2.2/braintree-php-3.33.0/client_token.php", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(PaypalExpressCheckout.this, "Failure " + statusCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String clientToken) {
                mAuthorization = clientToken;

                try {

                    mBraintreeFragment = BraintreeFragment.newInstance(PaypalExpressCheckout.this, mAuthorization);
                    Toast.makeText(PaypalExpressCheckout.this, "use " + mAuthorization, Toast.LENGTH_SHORT).show();
                    // mBraintreeFragment is ready to use!
                } catch (InvalidArgumentException e) {
                    // There was an issue with your authorization string.
                }
            }
        });
    }

    public void setupBraintreeAndStartExpressCheckout() {
        PayPalRequest request = new PayPalRequest("1")
                .currencyCode("PHP")
                .intent(PayPalRequest.INTENT_AUTHORIZE);
        PayPal.requestOneTimePayment(mBraintreeFragment, request);

        mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
            @Override
            public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                String nonce = paymentMethodNonce.getNonce();

                if(paymentMethodNonce instanceof PayPalAccountNonce) {
                    PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce)paymentMethodNonce;

                    // Access additional information
                    String email = payPalAccountNonce.getEmail();
                    String firstName = payPalAccountNonce.getFirstName();
                    String lastName = payPalAccountNonce.getLastName();
                    String phone = payPalAccountNonce.getPhone();

                    // See PostalAddress.java for details
                    PostalAddress billingAddress = payPalAccountNonce.getBillingAddress();
                    PostalAddress shippingAddress = payPalAccountNonce.getShippingAddress();
                }

//                tvtransaction.setText(nonce);
//                etNonce.setText(nonce);
                postNonceToServer(nonce);
            }
        });
    }

    public void postNonceToServer(final String nonce) {
        final RequestParams params = new RequestParams();
//        params.put("payment_method_nonce", nonce);
//        params.put("amount", etNonce.getText().toString());

        paramsHash = new HashMap<>();
        paramsHash.put("amount", pboxPricex);
        paramsHash.put("nonce", nonce);
        RequestParams requestParams = new RequestParams(paramsHash);

        AsyncHttpClient client2 = new AsyncHttpClient();
        client2.post("http://10.0.2.2/braintree-php-3.33.0/checkout.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(PaypalExpressCheckout.this, "Success Post", Toast.LENGTH_SHORT).show();

//                btnpbozReturn.setVisibility(View.VISIBLE);
                btnpboxPaynow.setVisibility(View.GONE);
                tvpboxSuccessID.setVisibility(View.VISIBLE);

                try {
                    String str = IOUtils.toString(responseBody, "UTF-8");

                    tvpboxSuccessID.setText("Payment success! ID: " +str);

                    DatabaseReference updatePayment = FirebaseDatabase.getInstance().getReference().child("payments").child(userUID);

                    String requestID = updatePayment.push().getKey();
                    updatePayment.child(requestID).setValue(true);

                    HashMap map = new HashMap();
                    map.put("price_payed", pboxPricex);
                    map.put("payment_method", "Paypal Express Checkout");
                    map.put("push_date", ServerValue.TIMESTAMP);

                    updatePayment.child(requestID).updateChildren(map);

                } catch (IOException e) {
                    e.printStackTrace();
                }

//                AsyncHttpClient postSuccess = new AsyncHttpClient();
//
//                postSuccess.get("http://10.0.2.2/braintree-php-3.33.0/checkout.php", new TextHttpResponseHandler() {
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        Toast.makeText(MainActivity.this, "Failed: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                        tvEcho.setText(responseString);
//                    }
//                });

//                tvEcho.setText("Success: ");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


    }
}
