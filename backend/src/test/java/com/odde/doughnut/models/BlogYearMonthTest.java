package com.odde.doughnut.models;

import com.odde.doughnut.entities.BlogYearMonth;
import com.odde.doughnut.entities.Note;
import com.odde.doughnut.entities.Notebook;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.testability.MakeMe;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:repository.xml"})
@Transactional
public class BlogYearMonthTest {
  @Autowired MakeMe makeMe;

    @Autowired
    ModelFactoryService modelFactoryService;

    @Test
    void shouldGetYearMonthEmptyList() {
        UserModel userModel = makeMe.aUser().toModelPlease();
        Notebook notebook = makeMe.aNotebook().byUser(userModel).please();
        Note headNote = makeMe.aNote().byUser(userModel).underNotebook(notebook).please();
        BlogModel blogModel = new BlogModel(headNote, modelFactoryService);
        assertTrue(blogModel.getBlogYearMonths(headNote).isEmpty());
    }

    @Test
    void shouldGetYearMonthList() {
        UserModel userModel = makeMe.aUser().toModelPlease();
        Notebook notebook = makeMe.aNotebook().byUser(userModel).please();
        Note headNote = makeMe.aNote().byUser(userModel).underNotebook(notebook).please();
        BlogModel blogModel = new BlogModel(headNote, modelFactoryService);
        Note note1 = makeMe.aNote("2021").under(headNote).please();
        Note note2 = makeMe.aNote("2020").under(headNote).please();
        Note note3 = makeMe.aNote("2019").under(headNote).please();
        makeMe.refresh(notebook);
        makeMe.refresh(headNote);
        makeMe.refresh(note1);
        makeMe.refresh(note2);
        makeMe.refresh(note3);
        assertTrue(blogModel.getBlogYearMonths(headNote.getId()).size()==3);
    }

    @Test
    void shouldGetArticleList() {

        LocalDate now = LocalDate.now();
        int year = now.getYear();

        int day = now.getDayOfMonth();
        String yearNoteTitle = String.valueOf(year);
        String monthNoteTitle = now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        String dayNoteTitle = String.valueOf(day);

        Note headNote = makeMe.aNote("odd-e-blog").withNoDescription().please();
        Note yearNote = makeMe.aNote(yearNoteTitle).withNoDescription().under(headNote).please();
        Note monthNote = makeMe.aNote(monthNoteTitle).withNoDescription().under(yearNote).please();
        Note dayNote = makeMe.aNote(dayNoteTitle).withNoDescription().under(monthNote).please();
        Note note1 = makeMe.aNote("Article #1").description("Hello World").under(dayNote).please();
        Note note2 = makeMe.aNote("Article #2").description("Hello World").under(dayNote).please();

        BlogModel blogModel = new BlogModel(headNote, modelFactoryService);
        makeMe.refresh(headNote);
        makeMe.refresh(yearNote);
        makeMe.refresh(monthNote);
        makeMe.refresh(dayNote);
        makeMe.refresh(note2);
        makeMe.refresh(note1);
        BlogYearMonth targetYearMonth = new BlogYearMonth("2021", "Apr");
        assertTrue(blogModel.getBlogPosts(headNote,targetYearMonth).size()==2);
    }

}
