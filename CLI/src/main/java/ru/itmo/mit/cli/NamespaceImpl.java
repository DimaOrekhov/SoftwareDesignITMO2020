package ru.itmo.mit.cli;

import ru.itmo.mit.cli.domain.Namespace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NamespaceImpl implements Namespace {

    private final Map<String, String> mapping;

    public NamespaceImpl() {
        this.mapping = new HashMap<>();
    }

    public NamespaceImpl(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    @Override
    public int size() {
        return mapping.size();
    }

    @Override
    public boolean isEmpty() {
        return mapping.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return mapping.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return mapping.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return mapping.get(o);
    }

    @Override
    public String put(String s, String s2) {
        return mapping.put(s, s2);
    }

    @Override
    public String remove(Object o) {
        return mapping.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        mapping.putAll(map);
    }

    @Override
    public void clear() {
        mapping.clear();
    }

    @Override
    public Set<String> keySet() {
        return mapping.keySet();
    }

    @Override
    public Collection<String> values() {
        return mapping.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return mapping.entrySet();
    }

}
