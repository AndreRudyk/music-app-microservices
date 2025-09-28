package resourceservice.service;

import org.apache.commons.lang3.tuple.Pair;
import resourceservice.entity.ResourceEntity;
import resourceservice.entity.StorageType;

import java.util.List;

public interface ResourceService {

    ResourceEntity saveResourceToStaging(byte[] file);

    Pair<String, byte[]> getResourceById(Integer id);

    List<Integer> deleteByIds(String ids, String bucket);

    boolean resourceExists(String id);

    void updateStorageTypeByResourceId(String resourceId, StorageType storageType);
}
