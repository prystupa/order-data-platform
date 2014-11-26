package com.prystupa.core;

import com.hazelcast.core.IMap;

public class EventUtils {

    public static EventID getRoot(final EventID eventId, final IMap<EventID, String> parents) {
        EventID root = eventId;
        for (String parent = parents.get(root); parent != null && !parent.equals(root.getId()); parent = parents.get(root)) {
            root = new EventID(parent, eventId.getPartitionKey());
        }
        return root;
    }
}
