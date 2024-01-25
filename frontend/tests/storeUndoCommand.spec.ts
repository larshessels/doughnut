import NoteEditingHistory from "../src/store/NoteEditingHistory";
import makeMe from "./fixtures/makeMe";

describe("storeUndoCommand", () => {
  const note = makeMe.aNoteRealm.topic("Dummy Title").please();

  describe("addEditingToUndoHistory", () => {
    const histories = new NoteEditingHistory();
    it("should push textContent into store state noteUndoHistories ", () => {
      histories.addEditingToUndoHistory(
        note.id,
        "edit topic",
        note.note.topicConstructor,
      );

      expect(histories.noteUndoHistories.length).toEqual(1);
    });
  });

  describe("popUndoHistory", () => {
    let initialUndoCount;
    const histories = new NoteEditingHistory();

    beforeEach(() => {
      histories.addEditingToUndoHistory(
        note.id,
        "edit details",
        note.note.details,
      );
      initialUndoCount = histories.noteUndoHistories.length;
    });

    it("should undo to last history", () => {
      histories.popUndoHistory();

      expect(histories.noteUndoHistories.length).toEqual(initialUndoCount - 1);
    });

    it("should not undo to last history if there is no more history", () => {
      histories.popUndoHistory();
      histories.popUndoHistory();
      histories.popUndoHistory();

      expect(histories.noteUndoHistories.length).toEqual(0);
    });
  });
});
