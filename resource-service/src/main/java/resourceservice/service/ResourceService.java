package resourceservice.service;

import org.apache.commons.lang3.tuple.Pair;
import resourceservice.entity.ResourceEntity;

import java.util.List;

public interface ResourceService {

    ResourceEntity saveResource(byte[] file);

    Pair<String, byte[]> getResourceById(Integer id);

    List<Integer> deleteByIds(String ids);

    boolean resourceExists(String id);
}
