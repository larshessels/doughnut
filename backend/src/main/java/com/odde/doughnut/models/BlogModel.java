package com.odde.doughnut.models;

import com.odde.doughnut.entities.BlogPost;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.factoryServices.ModelFactoryService;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.*;

public class BlogModel extends ModelForEntity<Note> {
    public BlogModel(Note entity, ModelFactoryService modelFactoryService) {
        super(entity, modelFactoryService)        ;
    }

    public List<String> getBlogYears(Note headNote) {
        Set<String> years = new HashSet<>();

        headNote.getChildren().stream().map(n->getCreatedDatetime(n).split("/")[0]).forEach(
                years::add
        );
        final ArrayList<String> list = new ArrayList<>(years);
        sort(list);
        return list;
    }

    public List<BlogPost> getBlogPosts(Note parentNote) {
        if (parentNote == null) {
            return new ArrayList<>();
        }
        return parentNote.getChildren().stream()
                .map(this::toBlogPost)
                .collect(Collectors.toList());
    }

    public BlogPost toBlogPost(Note note) {
        BlogPost article = new BlogPost();
        article.setTitle(note.getTitle().split(": ")[1]);
        article.setDescription(note.getNoteContent().getDescription());
        article.setAuthor(note.getUser().getName());
        article.setCreatedDatetime(getCreatedDatetime(note));
        return article;
    }

    private String getCreatedDatetime(Note note) {
        return note.getTitle().split(": ")[0];
    }

}
