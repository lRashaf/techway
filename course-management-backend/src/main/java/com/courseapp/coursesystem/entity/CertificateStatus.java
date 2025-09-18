package com.courseapp.coursesystem.entity;

// Enum لحالات الشهادة
public enum CertificateStatus {
    ACTIVE,     // شهادة فعالة
    REVOKED,    // شهادة مسحوبة
    EXPIRED     // شهادة منتهية الصلاحية (إذا كان هناك تاريخ انتهاء)
}
