import { flushPromises } from "@vue/test-utils";
import { afterEach, beforeEach, describe, it } from "vitest";
import SuggestedQuestionEdit from "@/components/ai/SuggestedQuestionEdit.vue";
import helper from "../helpers";

helper.resetWithApiMock(beforeEach, afterEach);

describe("Edit Suggested Question", () => {
  describe("suggest question for fine tuning AI", () => {
    const suggestedQuestion: Generated.SuggestedQuestionForFineTuning = {
      preservedQuestion: {
        stem: "What is the capital of France?",
        choices: ["Paris", "London", "Berlin", "Madrid"],
        correctChoiceIndex: 0,
        confidence: 9,
      },
      comment: "",
    };

    let wrapper;

    beforeEach(() => {
      wrapper = helper
        .component(SuggestedQuestionEdit)
        .withStorageProps({ suggestedQuestion })
        .mount();
    });

    it("should be able to suggest a question as good example", async () => {
      helper.apiMock.expectingPatch(
        `/api/fine-tuning/update-suggested-question-for-fine-tuning`,
      );
      wrapper.get("button.btn-success").trigger("click");
      await flushPromises();
    });
  });
});