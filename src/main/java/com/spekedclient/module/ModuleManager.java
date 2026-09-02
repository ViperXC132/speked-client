package com.spekedclient.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ModuleManager {
    private final Map<String, Module> modules = new LinkedHashMap<>();

    public void register(Module module) {
        if (modules.putIfAbsent(module.id(), module) != null) {
            throw new IllegalArgumentException("Duplicate module id: " + module.id());
        }
    }

    public Optional<Module> find(String id) {
        return Optional.ofNullable(modules.get(id));
    }

    public List<Module> byCategory(Category category) {
        return modules.values().stream()
                .filter(module -> module.category() == category)
                .sorted(Comparator.comparing(Module::name))
                .toList();
    }

    public List<Module> search(String query) {
        String needle = query == null ? "" : query.trim().toLowerCase();
        return modules.values().stream()
                .filter(module -> needle.isEmpty()
                        || module.name().toLowerCase().contains(needle)
                        || module.id().toLowerCase().contains(needle))
                .sorted(Comparator.comparing(Module::name))
                .toList();
    }

    public Collection<Module> all() {
        return List.copyOf(modules.values());
    }

    public List<Module> enabled() {
        return modules.values().stream().filter(Module::enabled).toList();
    }

    public void tick() {
        for (Module module : new ArrayList<>(modules.values())) {
            if (module.enabled()) module.onTick();
        }
    }
}
