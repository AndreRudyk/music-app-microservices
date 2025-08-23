package songservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import songservice.entity.SongMetadataEntity;

@Repository
public interface SongMetadataRepository extends JpaRepository<SongMetadataEntity, Integer> {
}
