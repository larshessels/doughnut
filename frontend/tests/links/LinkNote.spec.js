/**
 * @jest-environment jsdom
 */
import LinkNoteFinalize from '@/components/links/LinkNoteFinalize.vue';
import { mount } from '@vue/test-utils';
import { createTestingPinia } from '@pinia/testing';
import makeMe from '../fixtures/makeMe';

describe('LinkNoteFinalize', () => {
  createTestingPinia();

  it('going back', async () => {
    const note = makeMe.aNote.please();
    const wrapper = mount(LinkNoteFinalize, {
      propsData: {
        note,
        targetNote: note,
      },
    });
    await wrapper.find('.go-back-button').trigger('click');
    expect(wrapper.emitted().goBack).toHaveLength(1);
  });
});
