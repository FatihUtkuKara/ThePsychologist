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

     var purchasesResponseListener = PurchasesResponseListener { billingResult, purchasesList ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchasesList != null) {
            processPurchases(purchasesList)
        } else {
           // showDemoVersion()
        }
    }
    companion object {
        private lateinit var mContext: Context
        private lateinit var billingClient: BillingClient
        private val productIds = listOf("weekly-plan", "monthly", "lifetime")
        fun initializeContext(context: Context) {
            mContext = context
        }
    }
    private lateinit var withoutPayButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)
        if (intent.hasExtra("start")) {
            initialize()
            withoutPayButton = findViewById(R.id.withoutPayButton)
            queryPurchases()

            withoutPayButton.setOnClickListener{
                showDemoVersion()

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

                val productId = productDetailsList[0]
                val title = productDetailsList[0].title
                val productDetails = productDetailsList[0]
                val selectedOfferToken = productDetailsList[0].subscriptionOfferDetails?.get(0)?.offerToken
                val description = productDetailsList[0].description

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
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else {

                //showDemoVersion()
            }
         purchasesResponseListener = PurchasesResponseListener { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                processPurchases(purchasesList)
            } else {
               // showDemoVersion()
            }
        }

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

    fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Satın alma başarıyla onaylandı, tam sürümü aktif et
                        activateFullVersion()
                    }
                }
            }
        }
    }

    fun activateFullVersion() {
        // Uygulamanın tam sürüm özelliklerini aktifleştir
        // Örneğin, reklamları kaldır, sınırsız erişim sağla vs.
    }

    fun showDemoVersion() {
        val intent = Intent(mContext, MainActivity::class.java)
        intent.putExtra("start","start" )
        mContext.startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.no_animation)

    }

    fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.SkuType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params, purchasesResponseListener)
    }

    fun processPurchases(purchases: List<Purchase>?) {
        var isPremiumUser = false
        if (purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                    handlePurchase(purchase)  // Acknowledge the purchase if necessary
                }
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Check if the purchase is still valid
                    if (isSubscriptionValid(purchase)) {
                        isPremiumUser = true
                    }
                }
            }
        }
        if (isPremiumUser) {
            activateFullVersion()
        } else {
          //  showDemoVersion()
        }
    }

    fun isSubscriptionValid(purchase: Purchase): Boolean {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            return false
        }

           /* val purchaseTime = purchase.purchaseTime

            val currentTime = System.currentTimeMillis()
            val subscriptionPeriodMillis = convertPeriodToMillis(it)
            val expirationTime = purchaseTime + subscriptionPeriodMillis


            if (currentTime > expirationTime) {
                return false
            } */



        if (!purchase.isAutoRenewing) {
            return false
        }

        return true
    }

    /**
     * Örnek süre dönüştürme fonksiyonu, abonelik süre formatını milisaniyeye çevirir.
     */
    fun convertPeriodToMillis(periodStr: String): Long {
        // ISO 8601 süre formatı kullanılıyor varsayalım: "P1M" (1 ay)
        // Bu örnekte basit bir dönüşüm yapılacak, gerçek uygulamada detaylı bir parser gerekebilir
        return when {
            periodStr.contains("M") -> 30 * 24 * 60 * 60 * 1000L  // Ay için basit hesaplama
            periodStr.contains("W") -> 7 * 24 * 60 * 60 * 1000L   // Hafta için hesaplama
            periodStr.contains("Y") -> 365 * 24 * 60 * 60 * 1000L // Yıl için hesaplama
            else -> 0L
        }
    }

}