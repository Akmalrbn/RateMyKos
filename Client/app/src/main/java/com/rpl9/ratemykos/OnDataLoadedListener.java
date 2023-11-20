package com.rpl9.ratemykos;

import com.rpl9.ratemykos.model.Kos;

import java.util.List;

public interface OnDataLoadedListener {
    void onDataLoaded(List<Kos> kosList);
    void onDataLoadError(Throwable throwable);
}
