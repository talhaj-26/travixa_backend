package com.travixa.services.platform;

import com.travixa.entity.platform_cms_support.WeatherSnapshot;
import com.travixa.repository.platform.WeatherSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class WeatherService {

    private final WeatherSnapshotRepository weatherSnapshotRepository;

    public WeatherService(WeatherSnapshotRepository weatherSnapshotRepository) {
        this.weatherSnapshotRepository = weatherSnapshotRepository;
    }

    public List<WeatherSnapshot> findAll() {
        return weatherSnapshotRepository.findAll();
    }

    public Optional<WeatherSnapshot> findById(Long id) {
        return weatherSnapshotRepository.findById(id);
    }

    @Transactional
    public WeatherSnapshot save(WeatherSnapshot weatherSnapshot) {
        return weatherSnapshotRepository.save(weatherSnapshot);
    }
}
