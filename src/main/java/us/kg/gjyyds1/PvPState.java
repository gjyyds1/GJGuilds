package us.kg.gjyyds1;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvPState {

    private Map<UUID, Boolean> pvPState = new HashMap<>();

    public Map<UUID, Boolean> getPvPState() {
        return pvPState;
    }

    public void put(UUID uuid, Boolean state) {
        pvPState.put(uuid, state);
    }

    public Boolean get(UUID uuid) {
        return pvPState.get(uuid);
    }

    public void remove(UUID uuid) {
        pvPState.remove(uuid);
    }
}



