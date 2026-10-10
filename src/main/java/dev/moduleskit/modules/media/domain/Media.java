package dev.moduleskit.modules.media.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import dev.moduleskit.modules.media.enums.StorageDisk;
import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "media", indexes = {
        @Index(name = "idx_target_type_id", columnList = "target_type, target_id"),
        @Index(name = "idx_collection_order", columnList = "collectionName, orderColumn")
})
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String uuid;

    @Column(name = "target_type", nullable = false)
    private String targetType; // e.g., "Item", "User", "Article"

    @Column(name = "target_id", nullable = false)
    private Long targetId; // primary key of the target entity

    @Column(nullable = false)
    private String collectionName; // like 'images', 'thumbnails'

    private String name; // human-readable name

    @Column(nullable = false)
    private String fileName; // stored filename

    private String mimeType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StorageDisk disk; // 'local', 's3', etc.

    @Column
    @Enumerated(EnumType.STRING)
    private StorageDisk conversionsDisk; // e.g., store thumbnails on a different disk

    private Long size;

    // JSON metadata
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> manipulations = new HashMap<>(); // e.g., resizing info

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> customProperties = new HashMap<>(); // user-defined

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> generatedConversions = new HashMap<>(); // URLs to thumbnails etc.

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> responsiveImages = new HashMap<>();

    private Integer orderColumn;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public StorageDisk getDisk() {
        return disk;
    }

    public void setDisk(StorageDisk disk) {
        this.disk = disk;
    }

    public StorageDisk getConversionsDisk() {
        return conversionsDisk;
    }

    public void setConversionsDisk(StorageDisk conversionsDisk) {
        this.conversionsDisk = conversionsDisk;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Map<String, String> getManipulations() {
        return manipulations;
    }

    public void setManipulations(Map<String, String> manipulations) {
        this.manipulations = manipulations;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }

    public Map<String, String> getGeneratedConversions() {
        return generatedConversions;
    }

    public void setGeneratedConversions(Map<String, String> generatedConversions) {
        this.generatedConversions = generatedConversions;
    }

    public Map<String, String> getResponsiveImages() {
        return responsiveImages;
    }

    public void setResponsiveImages(Map<String, String> responsiveImages) {
        this.responsiveImages = responsiveImages;
    }

    public Integer getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(Integer orderColumn) {
        this.orderColumn = orderColumn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
