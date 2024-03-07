package com.example.thepsychologist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.billingclient.api.*
class PayActivity : AppCompatActivity() {

    companion object {
        private lateinit var mContext: Context
        private lateinit var billingClient: BillingClient
        private val productIds = listOf("product_id_1", "product_id_2", "product_id_3")
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

            billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener { billingResult, purchases ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (purchase in purchases) {
                            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                handlePurchase(purchase)
                            } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                                // Satın alma bekleniyor, gerekirse işlem yapabilirsiniz
                            } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                                // Satın alma durumu belirsiz, gerekirse işlem yapabilirsiniz
                            }
                        }
                    } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                        // Kullanıcı satın alma işlemini iptal etti
                    } else {
                        handleBillingError(billingResult.responseCode)
                    }
                }
                .build()

            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        loadProductDetails()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Bağlantı kaybedilirse burada yeniden bağlanma işlemi yapılabilir
                }
            })

            findViewById<TextView>(R.id.weeklyButton).setOnClickListener {
                purchaseProduct(productIds[0])
            }

            findViewById<TextView>(R.id.monthlyButton).setOnClickListener {
                purchaseProduct(productIds[1])
            }

            findViewById<TextView>(R.id.goToAppButton).setOnClickListener {
                purchaseProduct(productIds[2])
            }
        }


    }





        private fun loadProductDetails() {
            val skuDetailsParams = SkuDetailsParams.newBuilder()
                .setSkusList(productIds)
                .setType(BillingClient.SkuType.INAPP)
                .build()

            billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                    skuDetailsList.forEach { skuDetails ->
                        // SKU detayları burada işlenebilir
                    }
                }
            }
        }

    private fun purchaseProduct(productId: String) {
        val skuDetailsParams = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(productId))
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(skuDetailsParams) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                val skuDetails = skuDetailsList.first()

                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build()

                val responseCode = billingClient.launchBillingFlow(this@PayActivity, flowParams).responseCode
                // Satın alma işlemi başarılı olursa burada işlem yapılabilir
            }
        }
    }

        private fun handlePurchase(purchase: Purchase) {
            // Satın alma işlemi başarılı oldu, işlem yapabilirsiniz
        }

        private fun handleBillingError(responseCode: Int) {
            // Satın alma işlemi başarısız oldu, hata durumunu işleyin
        }

    private fun initialize() {
        initializeContext(this)
    }
}