package io.github.nwtgck.gh_card.emoji

import org.scalatest.{FunSpec, Matchers}

class EmojiRendererSpec extends FunSpec with Matchers {
  describe("EmojiRendererSpec") {
    it("should render") {
      val actual = EmojiRenderer.render("hello :tada: world")
      val expect = "hello 🎉 world"
      actual shouldBe expect
    }
    it("should render with many spaces") {
      val actual = EmojiRenderer.render("hello  :tada:    world")
      val expect = "hello  🎉    world"
      actual shouldBe expect
    }
    it("should render with space/tab mixed") {
      val actual = EmojiRenderer.render("hello :tada:\tworld")
      val expect = "hello 🎉\tworld"
      actual shouldBe expect
    }
  }
}
