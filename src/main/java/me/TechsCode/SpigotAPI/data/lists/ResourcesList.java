package me.TechsCode.SpigotAPI.data.lists;

import me.TechsCode.SpigotAPI.data.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ResourcesList extends ArrayList<Resource> {

    public ResourcesList(int initialCapacity) {
        super(initialCapacity);
    }

    public ResourcesList() {}

    public ResourcesList(Collection<? extends Resource> c) {
        super(c);
    }

    public Optional<Resource> id(String id){
        return stream().filter(resource -> resource.getId().equals(id)).findFirst();
    }

    public Optional<Resource> name(String name){
        return stream().filter(resource -> resource.getName().equalsIgnoreCase(name)).findFirst();
    }
}