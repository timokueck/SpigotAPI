package me.TechsCode.SpigotAPI.data.lists;

import me.TechsCode.SpigotAPI.data.Post;
import me.TechsCode.SpigotAPI.data.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostsList extends ArrayList<Post> {

    public PostsList(int initialCapacity) {
        super(initialCapacity);
    }

    public PostsList() {}

    public PostsList(Collection<? extends Post> c) {
        super(c);
    }

    public Optional<Post> user(String id){
        return stream().filter(post -> post.getUser().equals(id)).findFirst();
    }
}