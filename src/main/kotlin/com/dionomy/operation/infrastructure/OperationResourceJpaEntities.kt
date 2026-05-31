package com.dionomy.operation.infrastructure

import com.dionomy.operation.domain.Instructor
import com.dionomy.operation.domain.InstructorAvailability
import com.dionomy.operation.domain.InstructorAvailabilityRepository
import com.dionomy.operation.domain.InstructorRepository
import com.dionomy.operation.domain.Place
import com.dionomy.operation.domain.PlaceRepository
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
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

@Entity
@Table(name = "instructor_availabilities")
class InstructorAvailabilityJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "instructor_id", nullable = false)
    var instructorId: UUID = UUID.randomUUID(),
    @Column(name = "starts_at", nullable = false)
    var startsAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "ends_at", nullable = false)
    var endsAt: LocalDateTime = LocalDateTime.now().plusHours(1),
    @Column(name = "memo")
    var memo: String? = null,
) {
    fun toDomain(): InstructorAvailability =
        InstructorAvailability(
            id = id,
            tenantId = tenantId,
            instructorId = instructorId,
            startsAt = startsAt,
            endsAt = endsAt,
            memo = memo,
        )

    companion object {
        fun fromDomain(availability: InstructorAvailability): InstructorAvailabilityJpaEntity =
            InstructorAvailabilityJpaEntity(
                id = availability.id,
                tenantId = availability.tenantId,
                instructorId = availability.instructorId,
                startsAt = availability.startsAt,
                endsAt = availability.endsAt,
                memo = availability.memo,
            )
    }
}

interface SpringDataInstructorJpaRepository : JpaRepository<InstructorJpaEntity, UUID> {
    fun findByTenantIdOrderByNameAsc(tenantId: UUID): List<InstructorJpaEntity>
}

interface SpringDataPlaceJpaRepository : JpaRepository<PlaceJpaEntity, UUID> {
    fun findByTenantIdOrderByNameAsc(tenantId: UUID): List<PlaceJpaEntity>
}

interface SpringDataInstructorAvailabilityJpaRepository : JpaRepository<InstructorAvailabilityJpaEntity, UUID> {
    fun findByTenantIdOrderByStartsAtAsc(tenantId: UUID): List<InstructorAvailabilityJpaEntity>
    fun findByTenantIdAndInstructorIdOrderByStartsAtAsc(tenantId: UUID, instructorId: UUID): List<InstructorAvailabilityJpaEntity>
    fun findByTenantIdAndId(tenantId: UUID, id: UUID): InstructorAvailabilityJpaEntity?
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

@Repository
class JpaInstructorAvailabilityRepository(
    private val springDataRepository: SpringDataInstructorAvailabilityJpaRepository,
) : InstructorAvailabilityRepository {
    override fun findByTenant(tenantId: UUID): List<InstructorAvailability> =
        springDataRepository.findByTenantIdOrderByStartsAtAsc(tenantId).map { it.toDomain() }

    override fun findByInstructor(tenantId: UUID, instructorId: UUID): List<InstructorAvailability> =
        springDataRepository.findByTenantIdAndInstructorIdOrderByStartsAtAsc(tenantId, instructorId).map { it.toDomain() }

    override fun findByTenantAndId(tenantId: UUID, availabilityId: UUID): InstructorAvailability? =
        springDataRepository.findByTenantIdAndId(tenantId, availabilityId)?.toDomain()

    override fun save(availability: InstructorAvailability): InstructorAvailability =
        springDataRepository.save(InstructorAvailabilityJpaEntity.fromDomain(availability)).toDomain()
}
