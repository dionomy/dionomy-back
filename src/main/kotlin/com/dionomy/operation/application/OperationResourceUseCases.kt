package com.dionomy.operation.application

import com.dionomy.operation.domain.Instructor
import com.dionomy.operation.domain.InstructorRepository
import com.dionomy.operation.domain.Place
import com.dionomy.operation.domain.PlaceRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListInstructorsUseCase(
    private val instructorRepository: InstructorRepository,
) {
    fun execute(tenantId: UUID): List<Instructor> =
        instructorRepository.findByTenant(tenantId)
}

@Service
class CreateInstructorUseCase(
    private val instructorRepository: InstructorRepository,
) {
    fun execute(tenantId: UUID, name: String, phone: String?): Instructor =
        instructorRepository.save(
            Instructor(
                id = UUID.randomUUID(),
                tenantId = tenantId,
                name = name,
                phone = phone,
            ),
        )
}

@Service
class ListPlacesUseCase(
    private val placeRepository: PlaceRepository,
) {
    fun execute(tenantId: UUID): List<Place> =
        placeRepository.findByTenant(tenantId)
}

@Service
class CreatePlaceUseCase(
    private val placeRepository: PlaceRepository,
) {
    fun execute(tenantId: UUID, name: String, memo: String?): Place =
        placeRepository.save(
            Place(
                id = UUID.randomUUID(),
                tenantId = tenantId,
                name = name,
                memo = memo,
            ),
        )
}
