/**
 * @jest-environment jsdom
 */
import NoteEditDialog from '@/components/notes/NoteEditDialog.vue';
import flushPromises from 'flush-promises';
import _ from 'lodash';
import store from '../fixtures/testingStore';
import { renderWithStoreAndMockRoute } from '../helpers';
import makeMe from '../fixtures/makeMe';

beforeEach(() => {
  fetch.resetMocks();
});

describe('note show', () => {
  it('fetch API to be called ONCE', async () => {
    const note = makeMe.aNote.please();
    const stubResponse = {
      notePosition: makeMe.aNotePosition.please(),
      notes: [note],
    };
    fetch.mockResponseOnce(JSON.stringify(stubResponse));
    renderWithStoreAndMockRoute(store, NoteEditDialog, {
      propsData: { noteId: note.id },
    });
    await flushPromises();
    expect(fetch).toHaveBeenCalledTimes(1);
    expect(fetch).toHaveBeenCalledWith(
      `/api/notes/${note.id}`,
      expect.anything()
    );
  });
});
