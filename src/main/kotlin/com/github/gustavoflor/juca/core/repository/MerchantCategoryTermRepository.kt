package com.github.gustavoflor.juca.core.repository

import com.github.gustavoflor.juca.core.domain.MerchantCategory

interface MerchantCategoryTermRepository {
    fun findAll(): Map<MerchantCategory, List<String>>
}
