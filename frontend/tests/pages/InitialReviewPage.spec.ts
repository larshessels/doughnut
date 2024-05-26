import { describe, it, expect, vi, beforeEach, afterEach } from "vitest";
import { flushPromises } from "@vue/test-utils";
import InitialReviewPage from "@/pages/InitialReviewPage.vue";
import ShowThing from "@/components/review/ShowThing.vue";
import { useRouter } from "vue-router";
import mockBrowserTimeZone from "../helpers/mockBrowserTimeZone";
import helper from "../helpers";
import makeMe from "../fixtures/makeMe";
import RenderingHelper from "../helpers/RenderingHelper";

const mockedPush = vi.fn();

vitest.mock("vue-router", () => ({
  useRouter: () => ({
    push: mockedPush,
  }),
}));

let renderer: RenderingHelper;
const mockedInitialReviewCall = vi.fn();
const mockedNoteInfoCall = vi.fn();
const mockedGetNoteCall = vi.fn();

let teleportTarget: HTMLDivElement;

beforeEach(() => {
  teleportTarget = document.createElement("div");
  teleportTarget.id = "head-status";
  document.body.appendChild(teleportTarget);
});
afterEach(() => {
  document.body.innerHTML = "";
});

mockBrowserTimeZone("Europe/Amsterdam", beforeEach, afterEach);

beforeEach(() => {
  helper.managedApi.restReviewsController.initialReview =
    mockedInitialReviewCall;
  helper.managedApi.restNoteController.getNoteInfo =
    mockedNoteInfoCall.mockResolvedValue({});
  helper.managedApi.restNoteController.show1 = mockedGetNoteCall;
  renderer = helper.component(InitialReviewPage).withStorageProps({});
});

describe("repeat page", () => {
  it("redirect to review page if nothing to review", async () => {
    mockedInitialReviewCall.mockResolvedValue([]);
    renderer.currentRoute({ name: "initial" }).mount();
    await flushPromises();
    expect(useRouter().push).toHaveBeenCalledWith({ name: "reviews" });
    expect(mockedInitialReviewCall).toBeCalledWith("Europe/Amsterdam");
  });

  describe("normal view", () => {
    const noteRealm = makeMe.aNoteRealm.please();
    const reviewPoint = makeMe.aReviewPoint.ofNote(noteRealm).please();
    const { thing } = reviewPoint;

    beforeEach(() => {
      mockedInitialReviewCall.mockResolvedValue([thing, thing]);
      mockedGetNoteCall.mockResolvedValue(noteRealm);
    });

    it("normal view", async () => {
      const wrapper = renderer.currentRoute({ name: "initial" }).mount();
      await flushPromises();
      expect(wrapper.findAll(".paused")).toHaveLength(0);
      expect(teleportTarget.textContent).toContain("Initial Review: 0/2");
      expect(mockedGetNoteCall).toBeCalledWith(noteRealm.id);
    });

    (["levelChanged"] as "levelChanged"[]).forEach((event) => {
      it(`reloads when ${event}`, async () => {
        const wrapper = renderer.currentRoute({ name: "initial" }).mount();
        await flushPromises();
        mockedInitialReviewCall.mockResolvedValue([]);
        wrapper.findComponent(ShowThing).vm.$emit(event);
      });
    });
  });

  it("minimized view for link", async () => {
    const link = makeMe.aLink.please();
    const reviewPoint = makeMe.aReviewPoint.ofLink(link).please();
    mockedInitialReviewCall.mockResolvedValue([reviewPoint.thing]);
    const wrapper = renderer
      .withStorageProps({ minimized: true })
      .currentRoute({ name: "initial" })
      .mount();
    await flushPromises();
    expect(useRouter().push).toHaveBeenCalledTimes(0);
    expect(wrapper.findAll(".paused")).toHaveLength(1);
  });
});
