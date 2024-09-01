package com.github.gustavoflor.juca.data.repository

import com.github.gustavoflor.juca.core.MerchantCategory
import com.github.gustavoflor.juca.core.MerchantCategory.CASH
import com.github.gustavoflor.juca.core.MerchantCategory.FOOD
import com.github.gustavoflor.juca.core.MerchantCategory.MEAL
import com.github.gustavoflor.juca.core.repository.MerchantCategoryTermRepository
import org.springframework.stereotype.Repository

@Repository
class MerchantCategoryTermRepositoryImpl : MerchantCategoryTermRepository {
    companion object {
        private val MERCHANT_CATEGORY_TERMS = mapOf(
            MEAL to listOf("EATS", "RESTAURANTE", "PADARIA"),
            FOOD to listOf("MERCADO", "SUPERMERCADO"),
            CASH to listOf("BILHETEUNICO", "UBER TRIP")
        )
    }

    override fun findAll(): Map<MerchantCategory, List<String>> {
        return MERCHANT_CATEGORY_TERMS
    }
}
