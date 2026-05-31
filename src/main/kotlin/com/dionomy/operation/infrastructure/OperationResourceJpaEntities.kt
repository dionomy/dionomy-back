package com.dionomy.operation.infrastructure

import com.dionomy.operation.domain.Instructor
import com.dionomy.operation.domain.InstructorRepository
import com.dionomy.operation.domain.Place
import com.dionomy.operation.domain.PlaceRepository
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Entity
@Table(name = "instructors")
class InstructorJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "phone")
    var phone: String? = null,
) {
    fun toDomain(): Instructor =
        Instructor(
            id = id,
            tenantId = tenantId,
            name = name,
            phone = phone,
        )

    companion object {
        fun fromDomain(instructor: Instructor): InstructorJpaEntity =
            InstructorJpaEntity(
                id = instructor.id,
                tenantId = instructor.tenantId,
                name = instructor.name,
                phone = instructor.phone,
            )
    }
}

@Entity
@Table(name = "places")
class PlaceJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "name", nullable = false)
    var name: String = "",
    @Column(name = "memo")
    var memo: String? = null,
) {
    fun toDomain(): Place =
        Place(
            id = id,
            tenantId = tenantId,
            name = name,
            memo = memo,
        )

    companion object {
        fun fromDomain(place: Place): PlaceJpaEntity =
            PlaceJpaEntity(
                id = place.id,
                tenantId = place.tenantId,
                name = place.name,
                memo = place.memo,
            )
    }
}

interface SpringDataInstructorJpaRepository : JpaRepository<InstructorJpaEntity, UUID> {
    fun findByTenantIdOrderByNameAsc(tenantId: UUID): List<InstructorJpaEntity>
}

interface SpringDataPlaceJpaRepository : JpaRepository<PlaceJpaEntity, UUID> {
    fun findByTenantIdOrderByNameAsc(tenantId: UUID): List<PlaceJpaEntity>
}

@Repository
class JpaInstructorRepository(
    private val springDataRepository: SpringDataInstructorJpaRepository,
) : InstructorRepository {
    override fun findByTenant(tenantId: UUID): List<Instructor> =
        springDataRepository.findByTenantIdOrderByNameAsc(tenantId).map { it.toDomain() }

    override fun save(instructor: Instructor): Instructor =
        springDataRepository.save(InstructorJpaEntity.fromDomain(instructor)).toDomain()
}

@Repository
class JpaPlaceRepository(
    private val springDataRepository: SpringDataPlaceJpaRepository,
) : PlaceRepository {
    override fun findByTenant(tenantId: UUID): List<Place> =
        springDataRepository.findByTenantIdOrderByNameAsc(tenantId).map { it.toDomain() }

    override fun save(place: Place): Place =
        springDataRepository.save(PlaceJpaEntity.fromDomain(place)).toDomain()
}
