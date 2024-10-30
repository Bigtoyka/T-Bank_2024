package org.tbank.service.snapshot;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
@Component
public class SnapshotManager {
    private final Map<Long, Stack<LocationSnapshot>> locationSnapshots = new HashMap<>();
    private final Map<Integer, Stack<CategorySnapshot>> categorySnapshots = new HashMap<>();

    public void saveLocationSnapshot(LocationSnapshot snapshot) {
        locationSnapshots.computeIfAbsent(snapshot.getId(), k -> new Stack<>()).push(snapshot);
    }

    public void saveCategorySnapshot(CategorySnapshot snapshot) {
        categorySnapshots.computeIfAbsent(snapshot.getId(), k -> new Stack<>()).push(snapshot);
    }


}
