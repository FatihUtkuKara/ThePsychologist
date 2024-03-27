package com.example.thepsychologist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.factory.thepsychologist.R
import android.widget.TextView
import android.widget.Toast
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PayActivity : AppCompatActivity()   {

    companion object {
        private lateinit var mContext: Context
        private lateinit var billingClient: BillingClient
        private val productIds = listOf("weekly-plan", "monthly", "lifetime")
        fun initializeContext(context: Context) {
            mContext = context
        }
    }
    private lateinit var makePayButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        if (intent.hasExtra("start")) {
            initialize()
            makePayButton = findViewById(R.id.withoutPayButton)


            makePayButton.setOnClickListener{

                val intent = Intent(mContext, MainActivity::class.java)
                intent.putExtra("start","start" )
                mContext.startActivity(intent)
                overridePendingTransition(R.anim.slide_in_up, R.anim.no_animation)
            }


            findViewById<TextView>(R.id.weeklyButton).setOnClickListener {
                purchaseProduct("weekly")
            }

            findViewById<TextView>(R.id.monthlyButton).setOnClickListener {
                purchaseProduct("monthly")
            }

            findViewById<TextView>(R.id.yearlyButton).setOnClickListener {
                purchaseProduct("lifetime")
            }





        }
    }


    private fun purchaseProduct(productId : String) {

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build())
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->

            val responseCode = billingResult.responseCode
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                // Sorgulama başarılı oldu ve en az bir ürün detayı döndü

                // Ürün ayrıntıları ile yapılacak işlemler
                val productId = productDetailsList[0]// Ürün ID'si
                val title = productDetailsList[0].title // Ürün başlığı
                val productDetails = productDetailsList[0]  // Ürün fiyatı
                val selectedOfferToken = productDetailsList[0].subscriptionOfferDetails?.get(0)?.offerToken
                val description = productDetailsList[0].description // Ürün açıklaması

                val productList = listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("product_id_example")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
                val params = QueryProductDetailsParams.newBuilder()
                params.setProductList(productList)



                val productDetailsParamsList = listOf(
                    selectedOfferToken?.let {
                        BillingFlowParams.ProductDetailsParams.newBuilder()

                            .setProductDetails(productDetails)
                            .setOfferToken(it)
                            .build()
                    }
                )

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

                val billingResult = billingClient.launchBillingFlow(this, billingFlowParams)
            }
         else {
                Log.e("BillingError", "Query failed with code: $responseCode")

                // Sorgulama başarısız oldu veya ürün detayları boş döndü
            // Hata durumunu işleme alabilirsiniz
        }

    }

    }



    private fun initialize() {
        initializeContext(this)

        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            // Process purchases if needed
        }

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Billing client is ready
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request
            }
        })
    }
    }