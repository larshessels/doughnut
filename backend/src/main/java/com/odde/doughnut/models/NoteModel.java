package com.odde.doughnut.models;

import com.odde.doughnut.entities.Note;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

public class NoteModel {
  public final Note entity;
  private final ModelFactoryService modelFactoryService;

  public NoteModel(Note note, ModelFactoryService modelFactoryService) {
    this.entity = note;
    this.modelFactoryService = modelFactoryService;
  }

  public void destroy(Timestamp currentUTCTimestamp) {
    if (entity.getNotebook() != null) {
      if (entity.getNotebook().getHeadNote() == entity) {
        entity.getNotebook().setDeletedAt(currentUTCTimestamp);
        modelFactoryService.save(entity.getNotebook());
      }
    }

    entity.setDeletedAt(currentUTCTimestamp);
    modelFactoryService.save(entity);
    modelFactoryService.noteRepository.softDeleteDescendants(entity, currentUTCTimestamp);
  }

  public void restore() {
    if (entity.getNotebook() != null) {
      if (entity.getNotebook().getHeadNote() == entity) {
        entity.getNotebook().setDeletedAt(null);
        modelFactoryService.save(entity.getNotebook());
      }
    }
    modelFactoryService.noteRepository.undoDeleteDescendants(entity, entity.getDeletedAt());
    entity.setDeletedAt(null);
    modelFactoryService.save(entity);
  }

  public void checkDuplicateWikidataId() throws BindException {
    if (Strings.isEmpty(entity.getWikidataId())) {
      return;
    }
    List<Note> existingNotes =
        modelFactoryService.noteRepository.noteWithWikidataIdWithinNotebook(
            entity.getNotebook().getId(), entity.getWikidataId());
    if (existingNotes.stream().anyMatch(n -> !n.equals(entity))) {
      BindingResult bindingResult =
          new BeanPropertyBindingResult(entity.getWikidataId(), "wikidataId");
      bindingResult.rejectValue(null, "error.error", "Duplicate Wikidata ID Detected.");
      throw new BindException(bindingResult);
    }
  }

  public Collection<Note> getDescendantsInBreathFirstOrder() {
    return modelFactoryService.noteRepository.getDescendantsInBreathFirstOrder(entity.getId());
  }
}
