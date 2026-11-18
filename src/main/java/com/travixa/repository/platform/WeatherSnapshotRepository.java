package com.travixa.repository.platform;

import com.travixa.entity.platform_cms_support.WeatherSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherSnapshotRepository extends JpaRepository<WeatherSnapshot, Long> {
}
