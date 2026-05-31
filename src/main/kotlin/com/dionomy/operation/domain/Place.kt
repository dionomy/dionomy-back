package com.dionomy.operation.domain

import java.util.UUID

class Place(
    val id: UUID,
    val tenantId: UUID,
    val name: String,
    val memo: String?,
) {
    init {
        require(name.isNotBlank())
    }
}

interface PlaceRepository {
    fun findByTenant(tenantId: UUID): List<Place>
    fun save(place: Place): Place
}
