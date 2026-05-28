package com.dionomy.notice.infrastructure

import com.dionomy.notice.domain.Notice
import com.dionomy.notice.domain.NoticeRepository
import com.dionomy.notice.domain.NoticeTarget
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "notices")
class NoticeJpaEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: UUID = UUID.randomUUID(),
    @Column(name = "tenant_id", nullable = false)
    var tenantId: UUID = UUID.randomUUID(),
    @Column(name = "title", nullable = false)
    var title: String = "",
    @Column(name = "body", nullable = false)
    var body: String = "",
    @Column(name = "image_url")
    var imageUrl: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false)
    var target: NoticeTarget = NoticeTarget.ALL,
    @Column(name = "class_id")
    var classId: UUID? = null,
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): Notice =
        Notice(
            id = id,
            tenantId = tenantId,
            title = title,
            body = body,
            imageUrl = imageUrl,
            target = target,
            classId = classId,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(notice: Notice): NoticeJpaEntity =
            NoticeJpaEntity(
                id = notice.id,
                tenantId = notice.tenantId,
                title = notice.title,
                body = notice.body,
                imageUrl = notice.imageUrl,
                target = notice.target,
                classId = notice.classId,
                createdAt = notice.createdAt,
            )
    }
}

interface SpringDataNoticeJpaRepository : JpaRepository<NoticeJpaEntity, UUID> {
    fun findByTenantIdOrderByCreatedAtDesc(tenantId: UUID): List<NoticeJpaEntity>
}

@Repository
class JpaNoticeRepository(
    private val springDataRepository: SpringDataNoticeJpaRepository,
) : NoticeRepository {
    override fun save(notice: Notice): Notice =
        springDataRepository.save(NoticeJpaEntity.fromDomain(notice)).toDomain()

    override fun findByTenant(tenantId: UUID): List<Notice> =
        springDataRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).map { it.toDomain() }
}
